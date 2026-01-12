package com.sentinelguard.incident

import com.sentinelguard.domain.repository.IncidentRepository
import com.sentinelguard.domain.repository.SecuritySignalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * TimelineBuilder: Constructs Forensic Timeline
 * 
 * WHY THIS EXISTS:
 * Merges signals and incidents into a unified chronological view.
 * Provides filtering by date range, severity, and type.
 */
class TimelineBuilder(
    private val signalRepository: SecuritySignalRepository,
    private val incidentRepository: IncidentRepository
) {
    /**
     * Builds timeline for a date range.
     */
    suspend fun buildTimeline(
        startTime: Long,
        endTime: Long,
        includeSignals: Boolean = true,
        includeIncidents: Boolean = true,
        minSeverity: EventSeverity = EventSeverity.INFO
    ): List<TimelineEvent> = withContext(Dispatchers.IO) {
        val events = mutableListOf<TimelineEvent>()

        if (includeSignals) {
            val signals = signalRepository.getInRange(startTime, endTime)
            events.addAll(signals.map { it.toTimelineEvent() })
        }

        if (includeIncidents) {
            val incidents = incidentRepository.getInRange(startTime, endTime)
            events.addAll(incidents.map { it.toTimelineEvent() })
        }

        // Filter by severity and sort by timestamp descending
        events
            .filter { it.severity.ordinal >= minSeverity.ordinal }
            .sortedByDescending { it.timestamp }
    }

    /**
     * Gets recent timeline events.
     */
    suspend fun getRecentEvents(
        limit: Int = 50,
        minSeverity: EventSeverity = EventSeverity.INFO
    ): List<TimelineEvent> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val weekAgo = now - (7 * 24 * 60 * 60 * 1000L)
        
        buildTimeline(weekAgo, now, minSeverity = minSeverity).take(limit)
    }

    /**
     * Observes timeline as Flow.
     */
    fun observeTimeline(limit: Int = 50): Flow<List<TimelineEvent>> {
        val signalsFlow = signalRepository.observeRecent(limit).map { signals ->
            signals.map { it.toTimelineEvent() }
        }
        
        val incidentsFlow = incidentRepository.observeRecent(limit).map { incidents ->
            incidents.map { it.toTimelineEvent() }
        }

        return signalsFlow.combine(incidentsFlow) { signals, incidents ->
            (signals + incidents).sortedByDescending { it.timestamp }.take(limit)
        }
    }

    /**
     * Gets events by severity.
     */
    suspend fun getEventsBySeverity(
        severity: EventSeverity,
        limit: Int = 50
    ): List<TimelineEvent> = withContext(Dispatchers.IO) {
        getRecentEvents(limit * 2, minSeverity = severity)
            .filter { it.severity == severity }
            .take(limit)
    }

    /**
     * Gets critical events only.
     */
    suspend fun getCriticalEvents(limit: Int = 20): List<TimelineEvent> {
        return getEventsBySeverity(EventSeverity.CRITICAL, limit)
    }

    /**
     * Gets today's events.
     */
    suspend fun getTodayEvents(): List<TimelineEvent> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val startOfDay = now - (now % (24 * 60 * 60 * 1000L))
        buildTimeline(startOfDay, now)
    }

    /**
     * Gets event count by severity for summary.
     */
    suspend fun getEventSummary(): Map<EventSeverity, Int> = withContext(Dispatchers.IO) {
        val events = getRecentEvents(200, EventSeverity.LOW)
        events.groupBy { it.severity }.mapValues { it.value.size }
    }
}
