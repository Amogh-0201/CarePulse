package com.app.patientcareapp.feature_health_records.presentation.add_health_records

import com.app.patientcareapp.feature_health_records.domain.model.FileType
import com.app.patientcareapp.feature_health_records.domain.model.RecordCategory

sealed class AddHealthRecordsEvents {

    data class OnTitleChange(val title: String): AddHealthRecordsEvents()
    data class OnCategoryChange(val category: RecordCategory): AddHealthRecordsEvents()
    data class OnHospitalNameChange(val hospitalName: String): AddHealthRecordsEvents()
    data class OnDoctorNameChange(val doctorName: String): AddHealthRecordsEvents()
    data class OnDateChange(val date: Long): AddHealthRecordsEvents()
    data class OnNotesChange(val notes: String): AddHealthRecordsEvents()
    data class OnFilePicked(val uri: String, val fileType: FileType): AddHealthRecordsEvents()
    object OnSaveHealthRecord: AddHealthRecordsEvents()

}