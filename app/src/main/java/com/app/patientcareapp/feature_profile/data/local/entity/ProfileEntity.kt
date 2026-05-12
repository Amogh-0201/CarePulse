package com.app.patientcareapp.feature_profile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.patientcareapp.feature_profile.domain.model.BloodGroup
import com.app.patientcareapp.feature_profile.domain.model.Gender
import com.app.patientcareapp.feature_profile.domain.model.Profile
import kotlin.String


@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey
    val id: Int = 0,
    val name: String,
    val age: Int,
    val gender: Gender,
    val bloodGroup: BloodGroup,
    val conditions: List<String> = emptyList(),
    val allergies: List<String> = emptyList(),
    val bloodPressure: String? = null,
    val sugar: String? = null
) {

    fun toProfile(): Profile {
        return Profile(
            name = name,
            age =age,
            gender = gender,
            bloodGroup = bloodGroup,
            conditions = conditions,
            allergies = allergies,
            bloodPressure = bloodPressure,
            sugar = sugar
        )
    }
}
