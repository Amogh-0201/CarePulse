package com.app.patientcareapp.feature_profile.presentation.edit_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.patientcareapp.feature_profile.domain.model.BloodGroup
import com.app.patientcareapp.feature_profile.domain.model.Gender
import com.app.patientcareapp.feature_profile.domain.model.Profile
import com.app.patientcareapp.feature_profile.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.ranges.contains
import kotlin.text.isNotBlank
import kotlin.text.toIntOrNull


@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val useCases: ProfileUseCases
): ViewModel() {

    init {
        viewModelScope.launch {
            val profiles = useCases.getProfileUseCase()
            profiles.collectLatest { profile ->
                profile?.let {
                    name = it.name
                    dateOfBirth = it.dateOfBirth
                    gender = it.gender
                    bloodGroup = it.bloodGroup
                    conditions = it.conditions
                    allergies = it.allergies
                    bloodPressure = it.bloodPressure?: ""
                    sugar = it.sugar?: ""
                }
            }
        }
    }

    var name by mutableStateOf("")
        private set

    var dateOfBirth by mutableStateOf<Long?>(null)
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

    var conditionInput by mutableStateOf("")
        private set

    var allergyInput by mutableStateOf("")
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    sealed class UiEvent {
        object NavigateBackToProfileScreen: UiEvent()
        data class ShowSnackBar(val message: String): UiEvent()
    }

    fun onEvent(event: EditProfileScreenEvents) {
        when(event) {
            is EditProfileScreenEvents.OnDateOfBirthChange -> {
                dateOfBirth = event.dateOfBirth
            }
            is EditProfileScreenEvents.OnBloodGroupChange -> {
                bloodGroup = event.bloodGroup
            }
            is EditProfileScreenEvents.OnConditionInputChange -> {
                conditionInput = event.condition
            }
            is EditProfileScreenEvents.OnAddConditionClick -> {
                conditions = conditions + conditionInput.trim()
                conditionInput = ""
            }
            is EditProfileScreenEvents.OnAllergyInputChange -> {
                allergyInput = event.allergy
            }
            is EditProfileScreenEvents.OnAddAllergyClick -> {
                allergies = allergies + allergyInput.trim()
                allergyInput = ""
            }
            is EditProfileScreenEvents.OnBloodPressureChange -> {
                bloodPressure = event.bloodPressure
            }
            is EditProfileScreenEvents.OnSugarChange -> {
                sugar = event.sugar
            }
            is EditProfileScreenEvents.OnRemoveCondition -> {
                conditions = conditions - event.condition
            }
            is EditProfileScreenEvents.OnRemoveAllergy -> {
                allergies = allergies - event.allergy
            }
            is EditProfileScreenEvents.OnSaveChangesClick -> {
                viewModelScope.launch {
                    try {
                        updateProfile()
                        _uiEvent.send(UiEvent.NavigateBackToProfileScreen)
                    } catch(e: Exception) {
                        _uiEvent.send(UiEvent.ShowSnackBar(e.message ?: "Something went wrong"))
                    }
                }
            }
        }
    }

    fun isBasicInfoValid(): Boolean {  //don't allow to save the changes if not
        return (
                name.isNotBlank() &&
                dateOfBirth != null &&
                gender != null &&
                bloodGroup != null)
    }

    fun isConditionInputValid(): Boolean {
        return (conditionInput.isNotBlank() &&
                conditionInput.trim() !in conditions)
    }

    fun isAllergyInputValid(): Boolean {
        return (allergyInput.isNotBlank() &&
                allergyInput.trim() !in allergies)
    }

    private suspend fun updateProfile() {
        useCases.saveProfileUseCase(
            Profile(
                name = name,
                dateOfBirth = dateOfBirth!!,
                gender = gender!!,
                bloodGroup = bloodGroup!!,
                conditions = conditions,
                allergies = allergies,
                bloodPressure = bloodPressure,
                sugar = sugar
            )
        )
    }

}