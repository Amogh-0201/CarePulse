package com.app.patientcareapp.core.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    fun calculateAge(dateOfBirth: Long): Int {
        val dob = Calendar.getInstance().apply { timeInMillis = dateOfBirth }
        val now = Calendar.getInstance()
        
        var age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        
        if (now.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        
        return age
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
