package com.app.patientcareapp.feature_med_reminder.data.local.converters

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromTimeList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun toTimeList(data: String): List<String> {
        if(data.isEmpty()) return emptyList()
        return data.split(",")
    }

}