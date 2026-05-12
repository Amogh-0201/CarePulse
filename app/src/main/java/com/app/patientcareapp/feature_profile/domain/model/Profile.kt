package com.app.patientcareapp.feature_profile.domain.model

import com.app.patientcareapp.feature_profile.data.local.entity.ProfileEntity

data class Profile(
    val name: String,
    val age: Int,
    val gender: Gender,
    val bloodGroup: BloodGroup,
    val conditions: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val bloodPressure: String? = null,
    val sugar: String? = null
) {

    fun toProfileEntity(): ProfileEntity {
        return ProfileEntity(
            name = name,
            age = age,
            gender = gender,
            bloodGroup = bloodGroup,
            conditions = conditions,
            allergies = allergies,
            bloodPressure = bloodPressure,
            sugar = sugar
        )
    }
}
