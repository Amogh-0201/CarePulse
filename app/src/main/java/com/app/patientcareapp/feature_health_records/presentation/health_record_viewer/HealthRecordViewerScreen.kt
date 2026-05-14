package com.app.patientcareapp.feature_health_records.presentation.health_record_viewer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.app.patientcareapp.feature_health_records.domain.model.FileType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordViewerScreen(
    navController: NavController,
    recordId: Long,
    viewModel: HealthRecordViewerViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    LaunchedEffect(recordId) {
        viewModel.loadRecord(recordId)
    }

    val record = viewModel.healthRecord

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Record Viewer")
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            if(record == null) {
                CircularProgressIndicator()
            } else {

                val uri = Uri.parse(record.fileUri)

                if(record.fileType == FileType.IMAGE) {

                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/pdf")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(intent)
                    Button(
                        onClick =  {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "application/pdf")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Text("Open PDF")
                    }
                }
            }
        }
    }

}