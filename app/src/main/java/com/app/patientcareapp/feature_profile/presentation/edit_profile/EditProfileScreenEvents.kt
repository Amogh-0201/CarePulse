package com.app.patientcareapp.feature_profile.presentation.edit_profile

import com.app.patientcareapp.feature_profile.domain.model.BloodGroup

sealed class EditProfileScreenEvents {

    data class OnDateOfBirthChange(val dateOfBirth: Long): EditProfileScreenEvents()
    data class OnBloodGroupChange(val bloodGroup: BloodGroup): EditProfileScreenEvents()
    data class OnConditionInputChange(val condition: String): EditProfileScreenEvents()
    object OnAddConditionClick: EditProfileScreenEvents()
    data class OnAllergyInputChange(val allergy: String): EditProfileScreenEvents()
    object OnAddAllergyClick: EditProfileScreenEvents()
    data class OnBloodPressureChange(val bloodPressure: String): EditProfileScreenEvents()
    data class OnSugarChange(val sugar: String): EditProfileScreenEvents()
    object OnSaveChangesClick: EditProfileScreenEvents()
    data class OnRemoveCondition(val condition: String): EditProfileScreenEvents()
    data class OnRemoveAllergy(val allergy: String): EditProfileScreenEvents()

}