package com.example.groceryapp.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.groceryapp.utils.CameraManager
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CameraScreen(
    onImageCaptured: (File) -> Unit,
    onNavigateToUpload: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    var showCamera by remember { mutableStateOf(false) }

    val cameraManager = remember { CameraManager(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCamera = true
        }
    }

    LaunchedEffect(showCamera) {
        if (showCamera) {
            // Start camera when permission is granted
            cameraManager.startCamera(lifecycleOwner, PreviewView(context))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showCamera) {
            Box(modifier = Modifier.weight(1f)) {
                AndroidView(
                    factory = { PreviewView(it) },
                    modifier = Modifier.fillMaxSize()
                )
                
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                val uri: Uri? = cameraManager.takePhoto()
                                uri?.path?.let { path ->
                                    onImageCaptured(File(path))
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Capture")
                    }
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Start Camera")
                }
                
                OutlinedButton(
                    onClick = onNavigateToUpload,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Upload from Gallery")
                }
            }
        }
    }
}
