package com.sentinelguard.security.intruder

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.email.EmailService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * IntruderCaptureService: Captures intruder selfie on unauthorized access.
 * 
 * Features:
 * - Silent front camera capture (no preview)
 * - Sends photo via email to owner
 * - Includes location and device info
 * - Respects user privacy settings
 */
@Singleton
class IntruderCaptureService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val emailService: EmailService,
    private val securePreferences: SecurePreferences
) {
    
    companion object {
        private const val TAG = "IntruderCapture"
        private const val PHOTO_PREFIX = "intruder_"
        private const val PHOTO_EXTENSION = ".jpg"
    }
    
    private val executor = Executors.newSingleThreadExecutor()
    
    /**
     * Check if intruder capture is enabled.
     */
    fun isEnabled(): Boolean {
        return securePreferences.isIntruderCaptureEnabled
    }
    
    /**
     * Enable or disable intruder capture.
     */
    fun setEnabled(enabled: Boolean) {
        securePreferences.isIntruderCaptureEnabled = enabled
    }
    
    /**
     * Capture intruder photo and send to owner email.
     * Returns true if capture and send succeeded.
     */
    suspend fun captureAndSendIntruderPhoto(
        reason: String,
        failedAttempts: Int = 0,
        location: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        if (!isEnabled()) {
            Log.d(TAG, "Intruder capture disabled, skipping")
            return@withContext false
        }
        
        try {
            Log.d(TAG, "Starting intruder capture: $reason")
            
            // Capture photo
            val photoFile = capturePhoto()
            
            if (photoFile != null && photoFile.exists()) {
                Log.d(TAG, "Photo captured: ${photoFile.absolutePath}")
                
                // Send email with photo
                val success = sendIntruderAlert(photoFile, reason, failedAttempts, location)
                
                // Clean up photo file after sending
                photoFile.delete()
                
                return@withContext success
            } else {
                Log.e(TAG, "Failed to capture photo")
                return@withContext false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Intruder capture failed", e)
            return@withContext false
        }
    }
    
    /**
     * Capture photo using front camera silently.
     */
    private suspend fun capturePhoto(): File? = suspendCancellableCoroutine { cont ->
        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    
                    // Create lifecycle owner for camera
                    val lifecycleOwner = object : LifecycleOwner {
                        private val registry = LifecycleRegistry(this)
                        
                        init {
                            registry.currentState = Lifecycle.State.STARTED
                        }
                        
                        override val lifecycle: Lifecycle
                            get() = registry
                        
                        fun destroy() {
                            registry.currentState = Lifecycle.State.DESTROYED
                        }
                    }
                    
                    // Set up image capture
                    val imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                    
                    // Select front camera
                    val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                    
                    // Bind camera
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        imageCapture
                    )
                    
                    // Create output file
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                    val photoFile = File(
                        context.cacheDir,
                        "$PHOTO_PREFIX$timestamp$PHOTO_EXTENSION"
                    )
                    
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    
                    // Capture image after small delay for camera to initialize
                    executor.execute {
                        Thread.sleep(500) // Allow camera to warm up
                        
                        imageCapture.takePicture(
                            outputOptions,
                            executor,
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    cameraProvider.unbindAll()
                                    lifecycleOwner.destroy()
                                    cont.resume(photoFile)
                                }
                                
                                override fun onError(exception: ImageCaptureException) {
                                    cameraProvider.unbindAll()
                                    lifecycleOwner.destroy()
                                    cont.resumeWithException(exception)
                                }
                            }
                        )
                    }
                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
            }, ContextCompat.getMainExecutor(context))
            
        } catch (e: Exception) {
            cont.resumeWithException(e)
        }
    }
    
    /**
     * Send intruder alert email with photo attachment.
     */
    private suspend fun sendIntruderAlert(
        photoFile: File,
        reason: String,
        failedAttempts: Int,
        location: String?
    ): Boolean {
        val recipientEmail = securePreferences.alertRecipient ?: return false
        
        val subject = "⚠️ ALERT: Unauthorized Access Attempt Detected"
        
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        
        val body = buildString {
            appendLine("🚨 INTRUDER ALERT")
            appendLine()
            appendLine("Someone attempted to access your device without authorization.")
            appendLine()
            appendLine("═══════════════════════════════════")
            appendLine("📅 Time: $timestamp")
            appendLine("⚠️ Reason: $reason")
            if (failedAttempts > 0) {
                appendLine("🔐 Failed Login Attempts: $failedAttempts")
            }
            if (location != null) {
                appendLine("📍 Location: $location")
            }
            appendLine("═══════════════════════════════════")
            appendLine()
            appendLine("A photo of the intruder has been captured and attached to this email.")
            appendLine()
            appendLine("If this was you, you can ignore this alert.")
            appendLine("If not, take immediate action to secure your device.")
            appendLine()
            appendLine("─────────────────────────────────────")
            appendLine("SentinelGuard Security System")
        }
        
        return emailService.sendEmailWithAttachment(
            recipientEmail = recipientEmail,
            subject = subject,
            body = body,
            attachmentFile = photoFile,
            attachmentName = "intruder_photo.jpg"
        )
    }
}
