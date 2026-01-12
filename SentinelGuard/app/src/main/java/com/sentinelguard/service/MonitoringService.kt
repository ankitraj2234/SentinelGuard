package com.sentinelguard.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.sentinelguard.R
import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.security.alert.SecurityAlertManager
import com.sentinelguard.security.baseline.BaselineEngine
import com.sentinelguard.security.collector.AppUsageTracker
import com.sentinelguard.security.risk.RiskScoringEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * MonitoringService: Foreground service for continuous security monitoring.
 * 
 * Runs in background and periodically:
 * - Collects app usage data
 * - Updates baselines
 * - Calculates risk scores
 * - Triggers alerts if needed
 * - Logs all security events
 */
@AndroidEntryPoint
class MonitoringService : Service() {
    
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "sentinel_monitoring"
        const val NOTIFICATION_CHANNEL_ALERTS = "sentinel_alerts"
        const val NOTIFICATION_ID = 1001
        private const val MONITORING_INTERVAL_MS = 15 * 60 * 1000L // 15 minutes
        
        fun start(context: Context) {
            val intent = Intent(context, MonitoringService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stop(context: Context) {
            context.stopService(Intent(context, MonitoringService::class.java))
        }
    }
    
    @Inject lateinit var appUsageTracker: AppUsageTracker
    @Inject lateinit var baselineEngine: BaselineEngine
    @Inject lateinit var riskScoringEngine: RiskScoringEngine
    @Inject lateinit var securePreferences: SecurePreferences
    @Inject lateinit var alertManager: SecurityAlertManager
    
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var monitoringJob: Job? = null
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        alertManager.logEvent("SERVICE", "Monitoring service started")
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startMonitoring()
        return START_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
        alertManager.logEvent("SERVICE", "Monitoring service stopped")
        monitoringJob?.cancel()
        serviceScope.cancel()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun startMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = serviceScope.launch {
            while (isActive) {
                try {
                    performSecurityCheck()
                } catch (e: Exception) {
                    alertManager.logEvent("ERROR", "Security check failed: ${e.message}")
                }
                delay(MONITORING_INTERVAL_MS)
            }
        }
    }
    
    private suspend fun performSecurityCheck() {
        // Check if session is active
        if (!securePreferences.isSessionActive) {
            return
        }
        
        alertManager.logEvent("CHECK", "Performing periodic security check")
        
        // Update baselines with latest data
        baselineEngine.updateBaselines()
        
        // Calculate current risk
        val riskScore = riskScoringEngine.calculateRiskScore()
        
        // Log risk score
        alertManager.logEvent("RISK", "Current risk score: ${riskScore.totalScore}/100")
        
        // Update notification with current status
        updateNotification(riskScore.totalScore)
        
        // Check if alert should be triggered
        if (alertManager.shouldTriggerAlert(riskScore)) {
            alertManager.logEvent("ALERT", "Risk threshold exceeded, sending alert")
            
            val result = alertManager.sendSecurityAlert(riskScore)
            if (result.isSuccess) {
                showAlertNotification(riskScore.totalScore)
            }
        }
        
        // App usage tracking for behavioral analysis
        val endTime = System.currentTimeMillis()
        val startTime = endTime - MONITORING_INTERVAL_MS
        val sessions = appUsageTracker.getAppSessions(startTime = startTime, endTime = endTime)
        
        // Log app usage
        if (sessions.isNotEmpty()) {
            alertManager.logEvent("USAGE", "${sessions.size} app sessions in last interval")
        }
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Monitoring channel (low priority)
            val monitoringChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Security Monitoring",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when SentinelGuard is monitoring for security threats"
                setShowBadge(false)
            }
            
            // Alerts channel (high priority)
            val alertsChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ALERTS,
                "Security Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Important security alerts that require attention"
                setShowBadge(true)
                enableVibration(true)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(monitoringChannel)
            notificationManager.createNotificationChannel(alertsChannel)
        }
    }
    
    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("SentinelGuard Active")
            .setContentText("Monitoring your device security")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun updateNotification(riskScore: Int) {
        val (title, text) = when {
            riskScore < 40 -> "✓ Device Secure" to "No threats detected"
            riskScore < 70 -> "⚠ Elevated Risk" to "Risk score: $riskScore - tap to review"
            else -> "🚨 High Risk" to "Risk score: $riskScore - immediate attention needed"
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    private fun showAlertNotification(riskScore: Int) {
        val pendingIntent = PendingIntent.getActivity(
            this,
            1,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ALERTS)
            .setContentTitle("🚨 Security Alert")
            .setContentText("High risk detected (score: $riskScore). Email alert sent.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID + 1, notification)
    }
}
