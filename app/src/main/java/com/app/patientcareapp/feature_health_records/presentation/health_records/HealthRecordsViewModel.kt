package com.app.patientcareapp.feature_health_records.presentation.health_records

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.patientcareapp.feature_health_records.domain.model.HealthRecord
import com.app.patientcareapp.feature_health_records.domain.model.RecordCategory
import com.app.patientcareapp.feature_health_records.domain.use_case.HealthRecordUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthRecordsViewModel @Inject constructor(
    private val useCases: HealthRecordUseCases
): ViewModel() {

    var healthRecords by mutableStateOf<List<HealthRecord>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf<RecordCategory?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    private var getRecordsJob: Job? = null

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    sealed class UiEvent {
        object NavigateToAddHealthRecords: UiEvent()
        data class NavigateToHealthRecordViewer(val healthRecordId: Long): UiEvent()
    }

    init {
        loadHealthRecords()
    }

    fun onEvent(event: HealthRecordsScreenEvents) {
        when(event) {
            is HealthRecordsScreenEvents.OnSearchQueryChange -> {
                searchQuery = event.query
                if(searchQuery.isBlank()) {
                    loadHealthRecords()
                } else {
                    searchHealthRecords()
                }
            }
            is HealthRecordsScreenEvents.OnCategorySelected -> {
                selectedCategory = event.category
                if(selectedCategory == null) {
                    loadHealthRecords()
                } else {
                    loadRecordsByCategory()
                }
            }
            is HealthRecordsScreenEvents.OnHealthRecordClick -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.NavigateToHealthRecordViewer(event.healthRecord.id))
                }
            }
            is HealthRecordsScreenEvents.OnDeleteHealthRecord -> {
                viewModelScope.launch {
                    useCases.deleteHealthRecordUseCase(healthRecord = event.healthRecord)
                }
            }
            is HealthRecordsScreenEvents.OnAddHealthRecordClick -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.NavigateToAddHealthRecords)
                }
            }
        }
    }

    private fun loadHealthRecords() {

        getRecordsJob?.cancel()

        getRecordsJob = viewModelScope.launch {
            isLoading = true
            useCases.getAllHealthRecordsUseCase().collectLatest {
                healthRecords = it
                isLoading = false
            }
        }
    }

    private fun searchHealthRecords() {

        getRecordsJob?.cancel()

        getRecordsJob = viewModelScope.launch {
            useCases.searchHealthRecordsUseCase(query = searchQuery).collectLatest {
                healthRecords = it
            }
        }
    }

    private fun loadRecordsByCategory() {

        val category = selectedCategory ?: return

        getRecordsJob?.cancel()

        getRecordsJob = viewModelScope.launch {

            useCases.getHealthRecordsByCategoryUseCase(
                category
            ).collectLatest { records ->

                healthRecords = records

            }
        }
    }

}