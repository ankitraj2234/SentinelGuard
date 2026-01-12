package com.sentinelguard.report

import android.content.Context
import android.os.Build
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.sentinelguard.data.database.dao.CellTowerDao
import com.sentinelguard.data.local.preferences.SecurePreferences
import com.sentinelguard.security.alert.SecurityAlertManager
import com.sentinelguard.security.collector.AppUsageTracker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates comprehensive forensic PDF reports for security investigation.
 * 
 * Report includes:
 * - Device information
 * - Network/cell tower history
 * - Security incidents
 * - Behavioral patterns
 * - App usage analytics
 * - All security logs
 */
@Singleton
class ForensicReportGenerator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cellTowerDao: CellTowerDao,
    private val securePreferences: SecurePreferences,
    private val appUsageTracker: AppUsageTracker,
    private val alertManager: SecurityAlertManager
) {
    
    // Brand colors
    private val primaryColor = DeviceRgb(8, 145, 178) // Teal/Cyan
    private val headerBgColor = DeviceRgb(17, 24, 39) // Dark
    private val textColor = DeviceRgb(31, 41, 55)
    private val mutedColor = DeviceRgb(107, 114, 128)
    
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    // Configurable watermark opacity (set per report)
    private var watermarkOpacity: Float = 0.08f
    
    /**
     * Generate comprehensive forensic report
     * @param fileName Custom filename for the PDF (without extension)
     * @param opacity Watermark opacity (0.0 to 1.0)
     */
    suspend fun generateReport(fileName: String, opacity: Float = 0.08f): Result<File> = withContext(Dispatchers.IO) {
        try {
            // Store opacity for watermark function
            watermarkOpacity = opacity.coerceIn(0f, 0.3f)
            
            // Sanitize filename
            val safeName = fileName.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
            val outputFile = File(context.cacheDir, "${safeName}.pdf")
            
            val writer = PdfWriter(outputFile)
            val pdfDoc = PdfDocument(writer)
            val document = Document(pdfDoc, PageSize.A4)
            document.setMargins(50f, 40f, 60f, 40f)
            
            // Create watermark event handler (adds watermark as each page is finalized)
            val watermarkHandler = object : com.itextpdf.kernel.events.IEventHandler {
                override fun handleEvent(event: com.itextpdf.kernel.events.Event) {
                    val docEvent = event as com.itextpdf.kernel.events.PdfDocumentEvent
                    addWatermarkToPage(docEvent.page)
                }
            }
            pdfDoc.addEventHandler(com.itextpdf.kernel.events.PdfDocumentEvent.END_PAGE, watermarkHandler)
            
            android.util.Log.d("ForensicReport", "Watermark handler registered, opacity: $watermarkOpacity")
            
            val regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA)
            val boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
            
            // Add content sections
            addHeader(document, boldFont, regularFont)
            addDeviceInfo(document, boldFont, regularFont)
            addNetworkHistory(document, boldFont, regularFont)
            addSecurityIncidents(document, boldFont, regularFont)
            addBehavioralBaseline(document, boldFont, regularFont)
            addAppUsage(document, boldFont, regularFont)
            addSecurityLogs(document, boldFont, regularFont)
            addFooter(document, boldFont, regularFont)
            
            // Close document (watermarks already added via event handler)
            document.close()
            
            Result.success(outputFile)
        } catch (e: Exception) {
            android.util.Log.e("ForensicReport", "Failed to generate report: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    private fun addHeader(document: Document, boldFont: com.itextpdf.kernel.font.PdfFont, regularFont: com.itextpdf.kernel.font.PdfFont) {
        // Title
        val title = Paragraph("🛡️ SENTINELGUARD")
            .setFont(boldFont)
            .setFontSize(28f)
            .setFontColor(primaryColor)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(5f)
        document.add(title)
        
        val subtitle = Paragraph("SECURITY FORENSIC REPORT")
            .setFont(boldFont)
            .setFontSize(14f)
            .setFontColor(textColor)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20f)
        document.add(subtitle)
        
        // Report metadata
        val email = securePreferences.alertEmail ?: "Not configured"
        val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
        val generatedAt = dateFormat.format(Date())
        
        val metaTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 2f)))
            .setWidth(UnitValue.createPercentValue(100f))
            .setMarginBottom(20f)
        
        metaTable.addCell(createMetaCell("Device:", boldFont))
        metaTable.addCell(createMetaCell(deviceName, regularFont))
        metaTable.addCell(createMetaCell("Email:", boldFont))
        metaTable.addCell(createMetaCell(email, regularFont))
        metaTable.addCell(createMetaCell("Generated:", boldFont))
        metaTable.addCell(createMetaCell(generatedAt, regularFont))
        metaTable.addCell(createMetaCell("Android:", boldFont))
        metaTable.addCell(createMetaCell("${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})", regularFont))
        
        document.add(metaTable)
        
        // Divider
        document.add(Paragraph("─".repeat(80)).setFontColor(mutedColor).setMarginBottom(20f))
    }
    
    private fun createMetaCell(text: String, font: com.itextpdf.kernel.font.PdfFont): Cell {
        return Cell()
            .add(Paragraph(text).setFont(font).setFontSize(10f))
            .setBorder(Border.NO_BORDER)
            .setPadding(2f)
    }
    
    private suspend fun addDeviceInfo(document: Document, boldFont: com.itextpdf.kernel.font.PdfFont, regularFont: com.itextpdf.kernel.font.PdfFont) {
        addSectionTitle(document, "1. DEVICE INFORMATION", boldFont)
        
        val table = Table(UnitValue.createPercentArray(floatArrayOf(1f, 2f)))
            .setWidth(UnitValue.createPercentValue(100f))
            .setMarginBottom(20f)
        
        val infoItems = listOf(
            "Manufacturer" to Build.MANUFACTURER,
            "Model" to Build.MODEL,
            "Device" to Build.DEVICE,
            "Android Version" to Build.VERSION.RELEASE,
            "API Level" to Build.VERSION.SDK_INT.toString(),
            "Build ID" to Build.ID,
            "Security Patch" to (if (Build.VERSION.SDK_INT >= 23) Build.VERSION.SECURITY_PATCH else "N/A"),
            "Bootloader" to Build.BOOTLOADER,
            "Hardware" to Build.HARDWARE
        )
        
        infoItems.forEach { (label, value) ->
            table.addCell(createInfoCell(label, regularFont, true))
            table.addCell(createInfoCell(value, regularFont, false))
        }
        
        document.add(table)
    }
    
    private fun createInfoCell(text: String, font: com.itextpdf.kernel.font.PdfFont, isLabel: Boolean): Cell {
        return Cell()
            .add(Paragraph(text).setFont(font).setFontSize(9f).setFontColor(if (isLabel) mutedColor else textColor))
            .setBorder(Border.NO_BORDER)
            .setPadding(4f)
            .setBackgroundColor(if (isLabel) DeviceRgb(249, 250, 251) else ColorConstants.WHITE)
    }
    
    private suspend fun addNetworkHistory(document: Document, boldFont: com.itextpdf.kernel.font.PdfFont, regularFont: com.itextpdf.kernel.font.PdfFont) {
        addSectionTitle(document, "2. NETWORK CONNECTION HISTORY", boldFont)
        
        val history = cellTowerDao.getRecentHistory(1000) // Get up to 1000 entries
        
        if (history.isEmpty()) {
            document.add(Paragraph("No network connection history available.")
                .setFont(regularFont)
                .setFontSize(10f)
                .setFontColor(mutedColor)
                .setMarginBottom(20f))
            return
        }
        
        document.add(Paragraph("Total connections recorded: ${history.size}")
            .setFont(regularFont)
            .setFontSize(10f)
            .setMarginBottom(10f))
        
        // Table header
        val table = Table(UnitValue.createPercentArray(floatArrayOf(1.2f, 1f, 0.8f, 1.5f, 1f)))
            .setWidth(UnitValue.createPercentValue(100f))
            .setMarginBottom(20f)
        
        listOf("Timestamp", "Cell ID", "Network", "Location", "Signal").forEach { header ->
            table.addHeaderCell(Cell()
                .add(Paragraph(header).setFont(boldFont).setFontSize(8f).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(primaryColor)
                .setPadding(5f))
        }
        
        history.take(100).forEach { entry ->
            val timestamp = "${shortDateFormat.format(Date(entry.connectedAt))} ${timeFormat.format(Date(entry.connectedAt))}"
            val location = entry.areaName ?: "Unknown"
            val signal = entry.signalStrength?.let { "$it dBm" } ?: "N/A"
            
            table.addCell(createDataCell(timestamp, regularFont))
            table.addCell(createDataCell(entry.cellId, regularFont))
            table.addCell(createDataCell(entry.networkType ?: "Unknown", regularFont))
            table.addCell(createDataCell(location, regularFont))
            table.addCell(createDataCell(signal, regularFont))
        }
        
        if (history.size > 100) {
            document.add(Paragraph("... and ${history.size - 100} more entries")
                .setFont(regularFont)
                .setFontSize(9f)
                .setFontColor(mutedColor))
        }
        
        document.add(table)
    }
    
    private fun createDataCell(text: String, font: com.itextpdf.kernel.font.PdfFont): Cell {
        return Cell()
            .add(Paragraph(text).setFont(font).setFontSize(8f))
            .setPadding(4f)
    }
    
    private suspend fun addSecurityIncidents(document: Document, boldFont: com.itextpdf.kernel.font.PdfFont, regularFont: com.itextpdf.kernel.font.PdfFont) {
        addSectionTitle(document, "3. SECURITY INCIDENTS", boldFont)
        
        val incidents = cellTowerDao.getRecentIncidents(500)
        
        if (incidents.isEmpty()) {
            document.add(Paragraph("No security incidents recorded. ✅")
                .setFont(regularFont)
                .setFontSize(10f)
                .setFontColor(DeviceRgb(16, 185, 129)) // Green
                .setMarginBottom(20f))
            return
        }
        
        document.add(Paragraph("Total incidents: ${incidents.size}")
            .setFont(regularFont)
            .setFontSize(10f)
            .setMarginBottom(10f))
        
        val table = Table(UnitValue.createPercentArray(floatArrayOf(1.2f, 1f, 1f, 2f)))
            .setWidth(UnitValue.createPercentValue(100f))
            .setMarginBottom(20f)
        
        listOf("Timestamp", "Risk Level", "Cell ID", "Description").forEach { header ->
            table.addHeaderCell(Cell()
                .add(Paragraph(header).setFont(boldFont).setFontSize(8f).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(DeviceRgb(239, 68, 68)) // Red
                .setPadding(5f))
        }
        
        incidents.take(50).forEach { incident ->
            val timestamp = dateFormat.format(Date(incident.occurredAt))
            val riskColor = when (incident.riskLevel.uppercase()) {
                "CRITICAL" -> DeviceRgb(220, 38, 38)
                "HIGH" -> DeviceRgb(249, 115, 22)
                "MEDIUM" -> DeviceRgb(245, 158, 11)
                else -> textColor
            }
            
            table.addCell(createDataCell(timestamp, regularFont))
            table.addCell(Cell()
                .add(Paragraph(incident.riskLevel).setFont(boldFont).setFontSize(8f).setFontColor(riskColor))
                .setPadding(4f))
            table.addCell(createDataCell(incident.cellId, regularFont))
            table.addCell(createDataCell(incident.description ?: "Security threat detected", regularFont))
        }
        
        document.add(table)
    }
    
    private suspend fun addBehavioralBaseline(document: Document, boldFont: com.itextpdf.kernel.font.PdfFont, regularFont: com.itextpdf.kernel.font.PdfFont) {
        addSectionTitle(document, "4. BEHAVIORAL BASELINE ANALYSIS", boldFont)
        
        val learningDays = securePreferences.learningPeriodDays
        val dataRetention = securePreferences.dataRetentionDays
        
        val info = """
            Learning Period: $learningDays days
            Data Retention: $dataRetention days
            Cooldown: ${securePreferences.cooldownMinutes} minutes
            Learning Start: ${if (securePreferences.learningStartDate > 0) dateFormat.format(java.util.Date(securePreferences.learningStartDate)) else "Not started"}
        """.trimIndent()
        
        document.add(Paragraph(info)
            .setFont(regularFont)
            .setFontSize(10f)
            .setMarginBottom(20f))
    }
    
    private suspend fun addAppUsage(document: Document, boldFont: com.itextpdf.kernel.font.PdfFont, regularFont: com.itextpdf.kernel.font.PdfFont) {
        addSectionTitle(document, "5. APP USAGE ANALYTICS", boldFont)
        
        try {
            val endTime = System.currentTimeMillis()
            val startTime = endTime - (7 * 24 * 60 * 60 * 1000) // Last 7 days
            
            val usageSummary = appUsageTracker.getUsageSummary(startTime, endTime)
            
            if (usageSummary.isEmpty()) {
                document.add(Paragraph("No app usage data available. Grant Usage Access permission to track.")
                    .setFont(regularFont)
                    .setFontSize(10f)
                    .setFontColor(mutedColor)
                    .setMarginBottom(20f))
                return
            }
            
            document.add(Paragraph("Top apps used in the last 7 days:")
                .setFont(regularFont)
                .setFontSize(10f)
                .setMarginBottom(10f))
            
            val table = Table(UnitValue.createPercentArray(floatArrayOf(3f, 1f)))
                .setWidth(UnitValue.createPercentValue(100f))
                .setMarginBottom(20f)
            
            table.addHeaderCell(Cell()
                .add(Paragraph("App Package").setFont(boldFont).setFontSize(8f).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(primaryColor)
                .setPadding(5f))
            table.addHeaderCell(Cell()
                .add(Paragraph("Usage Time").setFont(boldFont).setFontSize(8f).setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(primaryColor)
                .setPadding(5f))
            
            usageSummary.entries
                .sortedByDescending { it.value }
                .take(20)
                .forEach { (pkg, duration) ->
                    val hours = duration / (1000 * 60 * 60)
                    val minutes = (duration % (1000 * 60 * 60)) / (1000 * 60)
                    val durationStr = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
                    
                    table.addCell(createDataCell(pkg.substringAfterLast('.'), regularFont))
                    table.addCell(createDataCell(durationStr, regularFont))
                }
            
            document.add(table)
            
        } catch (e: Exception) {
            document.add(Paragraph("Error loading app usage: ${e.message}")
                .setFont(regularFont)
                .setFontSize(10f)
                .setFontColor(mutedColor)
                .setMarginBottom(20f))
        }
    }
    
    private suspend fun addSecurityLogs(document: Document, boldFont: com.itextpdf.kernel.font.PdfFont, regularFont: com.itextpdf.kernel.font.PdfFont) {
        addSectionTitle(document, "6. SECURITY EVENT LOGS", boldFont)
        
        val logs = alertManager.getRecentLogs(200)
        
        if (logs.isEmpty()) {
            document.add(Paragraph("No security logs available.")
                .setFont(regularFont)
                .setFontSize(10f)
                .setFontColor(mutedColor)
                .setMarginBottom(20f))
            return
        }
        
        document.add(Paragraph("Recent security events (last ${logs.size} entries):")
            .setFont(regularFont)
            .setFontSize(10f)
            .setMarginBottom(10f))
        
        logs.take(50).forEach { log ->
            document.add(Paragraph("• $log")
                .setFont(regularFont)
                .setFontSize(8f)
                .setFontColor(textColor)
                .setMarginLeft(10f))
        }
        
        document.add(Paragraph("").setMarginBottom(20f))
    }
    
    private fun addFooter(document: Document, boldFont: com.itextpdf.kernel.font.PdfFont, regularFont: com.itextpdf.kernel.font.PdfFont) {
        document.add(Paragraph("─".repeat(80)).setFontColor(mutedColor).setMarginTop(20f))
        
        document.add(Paragraph("END OF FORENSIC REPORT")
            .setFont(boldFont)
            .setFontSize(12f)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(primaryColor)
            .setMarginTop(10f))
        
        document.add(Paragraph("This report was generated by SentinelGuard security monitoring application. " +
                "All data is stored locally on the device and has not been transmitted to any external servers.")
            .setFont(regularFont)
            .setFontSize(8f)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(mutedColor)
            .setMarginTop(10f))
    }
    
    private fun addSectionTitle(document: Document, title: String, boldFont: com.itextpdf.kernel.font.PdfFont) {
        document.add(Paragraph(title)
            .setFont(boldFont)
            .setFontSize(12f)
            .setFontColor(primaryColor)
            .setMarginTop(15f)
            .setMarginBottom(10f))
        
        document.add(Paragraph("─".repeat(40))
            .setFontSize(8f)
            .setFontColor(mutedColor)
            .setMarginBottom(10f))
    }
    
    /**
     * Add watermark and footer to a single page (called by event handler)
     */
    private fun addWatermarkToPage(page: com.itextpdf.kernel.pdf.PdfPage) {
        try {
            android.util.Log.d("ForensicReport", "Adding watermark to page, opacity: $watermarkOpacity")
            
            val font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
            // Ensure minimum opacity of 0.05 so watermark is always visible
            val effectiveOpacity = if (watermarkOpacity > 0f) maxOf(watermarkOpacity, 0.05f) else 0.08f
            val gs = PdfExtGState().setFillOpacity(effectiveOpacity)
            
            val canvas = PdfCanvas(page)
            
            // Add watermark
            canvas.saveState()
            canvas.setExtGState(gs)
            canvas.beginText()
            canvas.setFontAndSize(font, 60f)
            canvas.setFillColor(primaryColor)
            
            // Rotate and position watermark
            val pageSize = page.pageSize
            canvas.setTextMatrix(
                0.866f, 0.5f, -0.5f, 0.866f, // 30 degree rotation
                pageSize.width / 4, pageSize.height / 2
            )
            canvas.showText("SENTINELGUARD")
            canvas.endText()
            canvas.restoreState()
            
            // Add footer on each page
            canvas.beginText()
            canvas.setFontAndSize(font, 8f)
            canvas.setFillColor(DeviceRgb(156, 163, 175))
            canvas.setTextMatrix(40f, 25f)
            val email = securePreferences.alertEmail ?: "N/A"
            canvas.showText("${Build.MANUFACTURER} ${Build.MODEL} | $email")
            canvas.endText()
        } catch (e: Exception) {
            android.util.Log.w("ForensicReport", "Watermark failed on page: ${e.message}")
        }
    }
}
