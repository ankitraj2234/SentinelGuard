package com.sentinelguard.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sentinelguard.incident.TimelineBuilder
import com.sentinelguard.incident.TimelineEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val timelineBuilder: TimelineBuilder
) : ViewModel() {

    val timelineEvents: StateFlow<List<TimelineEvent>> = 
        timelineBuilder.observeTimeline(limit = 100)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}
