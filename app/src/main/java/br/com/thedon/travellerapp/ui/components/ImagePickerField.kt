package br.com.thedon.travellerapp.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import br.com.thedon.travellerapp.BuildConfig
import br.com.thedon.travellerapp.R
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@Composable
fun ImagePickerField(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    label: String,
    value: String?,
    onValueChange: (String) -> Unit
) {
    val context = LocalContext.current
    val file = context.createImageFile()
    val cameraPickUri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )
    val pickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                onValueChange(uri.toString())
            }
        }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            onValueChange(cameraPickUri.toString())
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            cameraLauncher.launch(cameraPickUri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleCameraSelect() {
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(cameraPickUri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun handlePicSelect() {
        pickerLauncher.launch(
            PickVisualMediaRequest(
                mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    Button(
        onClick = { handlePicSelect() },
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
        shape = RectangleShape
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
            ) {
                if (value == null || value == "") {
                    Icon(
                        painter = painterResource(id = R.drawable.add_photo),
                        contentDescription = "Add new photo",
                        modifier = Modifier
                            .size(46.dp)
                            .padding(dimensionResource(id = R.dimen.padding_extra_large)),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                } else {
                    val painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(LocalContext.current).data(data = value).build()
                    )
                    RemoteImage(
                        url = value,
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = {
                        handleCameraSelect()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = "Add image from camera icon",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(text = "Camera")
                }
                TextButton(onClick = {
                    handlePicSelect()
                }) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoLibrary,
                        contentDescription = "Add image from gallery icon",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(text = "Gallery")
                }
                if (value != null && value != "") {
                    TextButton(onClick = {
                        onValueChange("")
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete, contentDescription = "Delete icon",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(text = "Remove")
                    }
                }
            }
        }
    }
}


fun Context.createImageFile(): File {
    // Create an image file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}