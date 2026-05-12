package com.app.patientcareapp.feature_onboarding.presentation.events

import com.app.patientcareapp.feature_profile.domain.model.BloodGroup
import com.app.patientcareapp.feature_profile.domain.model.Gender

sealed class OnBoardingScreenEvents {

    data class OnNameChange(val name: String): OnBoardingScreenEvents()
    data class OnAgeChange(val age: String): OnBoardingScreenEvents()
    data class OnGenderChange(val gender: Gender): OnBoardingScreenEvents()
    data class OnBloodGroupChange(val bloodGroup: BloodGroup): OnBoardingScreenEvents()
    data class OnBloodPressureChange(val bloodPressure: String): OnBoardingScreenEvents()
    data class OnSugarChange(val sugar: String): OnBoardingScreenEvents()
    object OnFinishButtonClick: OnBoardingScreenEvents()
    data class OnConditionInputChange(val condition: String): OnBoardingScreenEvents()
    object OnAddCondition: OnBoardingScreenEvents()
    data class OnRemoveCondition(val condition: String): OnBoardingScreenEvents()
    data class OnAllergyInputChange(val allergy: String): OnBoardingScreenEvents()
    object OnAddAllergy: OnBoardingScreenEvents()
    data class OnRemoveAllergy(val allergy: String): OnBoardingScreenEvents()
}