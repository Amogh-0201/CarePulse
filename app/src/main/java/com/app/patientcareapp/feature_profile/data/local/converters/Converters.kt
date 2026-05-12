package com.app.patientcareapp.feature_profile.data.local.converters

import androidx.room.TypeConverter
import com.app.patientcareapp.feature_profile.domain.model.BloodGroup
import com.app.patientcareapp.feature_profile.domain.model.Gender

class Converters {

    @TypeConverter
    fun fromList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toList(value: String): List<String> {
        return if(value.isEmpty()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun fromGender(gender: Gender): String {
        return gender.name
    }

    @TypeConverter
    fun toGender(value: String): Gender {
        return Gender.valueOf(value)
    }

    @TypeConverter
    fun fromBloodGroup(bloodGroup: BloodGroup): String {
        return bloodGroup.name
    }

    @TypeConverter
    fun toBloodGroup(value: String): BloodGroup {
        return BloodGroup.valueOf(value)
    }
}