package com.example.groceryapp.ui.screens

import android.widget.Toast
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.groceryapp.R
import com.example.groceryapp.MainActivity
import com.example.groceryapp.data.model.GroceryItem
import java.io.ByteArrayOutputStream
import java.io.File

@Composable
fun UploadScreen(
    onGroceryListProcessed: (File) -> Unit
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            selectedImageUri = getImageUri(context, it)
        }
    }

    fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED -> {
                cameraLauncher.launch(null)
            }
            context is MainActivity -> {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.CAMERA),
                    101
                )
            }
        }
    }

    val processImage: () -> Unit = {
        val currentUri = selectedImageUri
        if (currentUri != null) {
            try {
                isProcessing = true
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, currentUri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, currentUri)
                    ImageDecoder.decodeBitmap(source)
                }

                // Create a temporary file from the bitmap
                val file = File(context.cacheDir, "temp_image.jpg")
                file.outputStream().use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }

                onGroceryListProcessed(file)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Failed to process image: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isProcessing = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        selectedImageUri?.let { uri ->
            val bitmap = remember(uri) {
                try {
                    if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, uri)
                        ImageDecoder.decodeBitmap(source)
                    }
                } catch (e: Exception) {
                    null
                }
            }
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 16.dp)
                )
            }
        } ?: run {
            Icon(
                imageVector = Icons.Filled.UploadFile,
                contentDescription = "Upload Icon",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.upload_grocery_list),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Pick Image")
            }
            Button(
                onClick = { requestCameraPermission() },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Take Photo")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = processImage,
            enabled = selectedImageUri != null && !isProcessing,
            shape = RoundedCornerShape(8.dp)
        ) {
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Processing...")
            } else {
                Text("Process Image")
            }
        }
    }
}

/**
 * Helper to turn a Bitmap into a local Uri so we can store a quick photo from camera preview.
 */
fun getImageUri(context: android.content.Context, bitmap: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
    return Uri.parse(path ?: "")
}
