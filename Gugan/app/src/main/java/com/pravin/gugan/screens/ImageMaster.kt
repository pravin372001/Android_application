package com.pravin.gugan.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.pravin.gugan.R
import com.pravin.gugan.database.ImageEntity
import com.pravin.gugan.ui.theme.GuganTheme
import com.pravin.gugan.R.string as Apptext

@Composable
fun ImageMaster(
    imageSave: (ImageEntity) -> Unit,
    imageEntity: ImageEntity
) {

    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            imageUri.value = it
        } ?: run {
            Log.d("AddNewsScreen", "No image selected")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = if (imageUri.value != null) {
               rememberAsyncImagePainter(imageUri.value)
            } else {
                if(imageEntity.imageUri != "") {
                    rememberAsyncImagePainter(imageEntity.imageUri)
                } else {
                    painterResource(R.drawable.ic_launcher_foreground)
                }
            },
            contentDescription = null,
            modifier = Modifier
                .wrapContentSize()
        )
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (imageUri.value != null) {
                        imageSave(ImageEntity(imageUri = imageUri.value.toString()))
                        Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = stringResource(Apptext.save))
            }
            Button(
                onClick = {
                    pickImageLauncher.launch(arrayOf("image/*"))
                },
            ) {
                Text(text = stringResource(Apptext.upload_image))
            }
        }
    }
}

@Preview
@Composable
private fun ImageMasterPreview() {
    GuganTheme {
        ImageMaster(
            imageSave = {},
            imageEntity = ImageEntity(0, "")
        )
    }
}