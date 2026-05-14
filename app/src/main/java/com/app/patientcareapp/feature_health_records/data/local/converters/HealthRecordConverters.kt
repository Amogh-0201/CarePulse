package com.app.patientcareapp.feature_health_records.data.local.converters

import androidx.room.TypeConverter
import com.app.patientcareapp.feature_health_records.domain.model.FileType
import com.app.patientcareapp.feature_health_records.domain.model.RecordCategory


class HealthRecordConverters {

    @TypeConverter
    fun fromRecordCategory(category: RecordCategory): String {
        return category.name
    }

    @TypeConverter
    fun toRecordCategory(category: String): RecordCategory {
        return RecordCategory.valueOf(category)
    }

    @TypeConverter
    fun fromFileType(fileType: FileType): String {
        return fileType.name
    }

    @TypeConverter
    fun toFileType(fileType: String): FileType {
        return FileType.valueOf(fileType)
    }

}