package com.app.patientcareapp.feature_onboarding.presentation.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.patientcareapp.core.data.preferences.PreferenceManager
import com.app.patientcareapp.feature_onboarding.presentation.events.OnBoardingScreenEvents
import com.app.patientcareapp.feature_profile.domain.model.BloodGroup
import com.app.patientcareapp.feature_profile.domain.model.Gender
import com.app.patientcareapp.feature_profile.domain.model.Profile
import com.app.patientcareapp.feature_profile.domain.use_case.ProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val useCases: ProfileUseCases,
    private val preferenceManager: PreferenceManager
): ViewModel() {

    var name by mutableStateOf("")
        private set

    var age by mutableStateOf("")
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

        data class ShowSnackBar(val message: String): UiEvent()
        object NavigateToMainApp: UiEvent()
    }

    fun onEvent(event: OnBoardingScreenEvents) {
        when(event) {
            is OnBoardingScreenEvents.OnNameChange -> {
                name = event.name
            }
            is OnBoardingScreenEvents.OnAgeChange -> {
                age = event.age
            }
            is OnBoardingScreenEvents.OnGenderChange -> {
                gender = event.gender
            }
            is OnBoardingScreenEvents.OnBloodGroupChange -> {
                bloodGroup = event.bloodGroup
            }
            is OnBoardingScreenEvents.OnConditionsChange -> {
                conditions = event.conditions
            }
            is OnBoardingScreenEvents.OnAllergiesChange -> {
                allergies = event.allergies
            }
            is OnBoardingScreenEvents.OnBloodPressureChange -> {
                bloodPressure = event.bloodPressure
            }
            is OnBoardingScreenEvents.OnSugarChange -> {
                sugar = event.sugar
            }
            is OnBoardingScreenEvents.OnConditionInputChange -> {
                conditionInput = event.condition
            }
            is OnBoardingScreenEvents.OnAddCondition -> {
                conditions = conditions + conditionInput.trim()
                conditionInput = ""
            }
            is OnBoardingScreenEvents.OnRemoveCondition -> {
                conditions = conditions - event.condition
            }
            is OnBoardingScreenEvents.OnAllergyInputChange -> {
                allergyInput = event.allergy
            }
            is OnBoardingScreenEvents.OnAddAllergy -> {
                allergies = allergies + allergyInput.trim()
                allergyInput = ""
            }
            is OnBoardingScreenEvents.OnRemoveAllergy -> {
                allergies = allergies - event.allergy
            }
            OnBoardingScreenEvents.OnFinishButtonClick -> {
                viewModelScope.launch {
                    try {
                        saveProfile()
                        preferenceManager.setOnBoardingCompleted(completed = true)
                        _uiEvent.send(UiEvent.NavigateToMainApp)
                    } catch(e: Exception) {
                        _uiEvent.send(
                            UiEvent.ShowSnackBar(e.message ?: "Something went wrong")
                        )
                    }

                }
            }
        }
    }

    fun isBasicInfoValid(): Boolean {
        return (
                name.isNotBlank() &&
                age.isNotBlank() &&
                age.toIntOrNull() in 1..120 &&
                gender != null &&
                bloodGroup != null
                )
    }

    fun isConditionInputValid(): Boolean {
        return (conditionInput.isNotBlank() &&
                conditionInput.trim() !in conditions)
    }

    fun isVitalsEmpty(): Boolean {
        return bloodPressure.isBlank() && sugar.isBlank()
    }

    fun isAllergyInputValid(): Boolean {
        return (allergyInput.isNotBlank() &&
                allergyInput.trim() !in allergies)
    }

    private suspend fun saveProfile() {
        useCases.saveProfileUseCase(
            Profile(
                name = name,
                age = age.toInt(),
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