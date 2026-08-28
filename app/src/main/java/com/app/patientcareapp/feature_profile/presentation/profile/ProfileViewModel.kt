package com.app.patientcareapp.feature_profile.presentation.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.patientcareapp.core.util.DateUtils
import com.app.patientcareapp.feature_profile.domain.model.BloodGroup
import com.app.patientcareapp.feature_profile.domain.model.Gender
import com.app.patientcareapp.feature_profile.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val useCases: ProfileUseCases
): ViewModel() {

    init {
        viewModelScope.launch {
            val profiles = useCases.getProfileUseCase()
            profiles.collectLatest { profile ->
                profile?.let {
                    name = it.name
                    age = DateUtils.calculateAge(it.dateOfBirth)
                    gender = it.gender
                    bloodGroup = it.bloodGroup

                    if(it.conditions.isEmpty()) {
                        conditions = listOf("NA")
                    } else {
                        conditions = it.conditions
                    }
                    if(it.allergies.isEmpty()) {
                        allergies = listOf("NA")
                    } else {
                        allergies = it.allergies
                    }

                    bloodPressure = it.bloodPressure?: "NA"
                    sugar = it.sugar?: "NA"
                }
            }
        }
    }

    var name by mutableStateOf("")
        private set

    var age by mutableStateOf<Int?>(null)
        private set

    var gender by mutableStateOf<Gender?>(null)
        private set

    var bloodGroup by mutableStateOf<BloodGroup?>(null)
        private set

    var conditions by mutableStateOf<List<String>>(emptyList())
        private set

    var allergies by mutableStateOf<List<String>>(emptyList())
        private set

    var bloodPressure by mutableStateOf("")
        private set

    var sugar by mutableStateOf("")
        private set


    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    sealed class UiEvent {
        object NavigateToEditProfileScreen: UiEvent()
    }

    fun onEvent(event: ProfileScreenEvents) {
        when(event) {
            is ProfileScreenEvents.OnEditProfileClick -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.NavigateToEditProfileScreen)
                }
            }
        }
    }

}