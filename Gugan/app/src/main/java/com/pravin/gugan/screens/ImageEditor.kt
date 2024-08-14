package com.pravin.gugan.screens

import androidx.compose.ui.graphics.asImageBitmap
import com.pravin.gugan.R
import com.pravin.gugan.ui.theme.GuganTheme
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import coil.compose.rememberAsyncImagePainter
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.pravin.gugan.database.ImageEntity
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import com.itextpdf.layout.element.Image as PdfImage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun ImageEditor(
    imageEntity: ImageEntity
) {
    var text by remember { mutableStateOf("Your Address Here") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var capturedBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var textOffset by remember { mutableStateOf(IntOffset.Zero) }
        val density = LocalDensity.current
        val captureController = rememberCaptureController()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consumeAllChanges()
                        textOffset = IntOffset(
                            x = textOffset.x + dragAmount.x.roundToInt(),
                            y = textOffset.y + dragAmount.y.roundToInt()
                        )
                    }
                }
                .capturable(captureController)
        ) {
            Image(
                painter = if (imageEntity.imageUri.isNotEmpty()) {
                    rememberAsyncImagePainter(Uri.parse(imageEntity.imageUri))
                } else {
                    painterResource(id = R.drawable.ic_launcher_foreground) // Replace with a default image
                },
                contentDescription = null,
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center) // Center the image in the box
            )
            BasicText(
                text = text,
                style = TextStyle(fontSize = 10.sp), // Adjust text size as needed
                modifier = Modifier
                    .width(175.dp)
                    .offset { textOffset }
                    .align(Alignment.Center) // Center the text in the box
            )
        }

        // Text input and download button
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Enter Address") },
                placeholder = { Text("Your Address Here") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
            Button(
                onClick = {
                    if(text.isEmpty() || text == "Your Address Here") {
                        Toast.makeText(context, "Please enter address", Toast.LENGTH_SHORT).show()
                    } else {
                        scope.launch {
                            capturedBitmap = captureController.captureAsync().await()
                        }
                    }
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Download PDF")
            }
        }
        capturedBitmap?.let {
            scope.launch {
                convertBitmapToPdf(bitmap = it.asAndroidBitmap(), text, context)
            }
        }
    }
}

fun convertBitmapToPdf(bitmap: Bitmap, fileName: String, context: Context) {
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val pdfFile = File(downloadsDir, "$fileName.pdf")

    try {
        val pdfWriter = PdfWriter(FileOutputStream(pdfFile))
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val imageData = ImageDataFactory.create(outputStream.toByteArray())
        val pdfImage = PdfImage(imageData)
        document.add(pdfImage)
        document.close()

        Toast.makeText(context, "PDF saved successfully at ${pdfFile.absolutePath}", Toast.LENGTH_SHORT).show()
        println("PDF saved successfully at ${pdfFile.absolutePath}")

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}




@Preview
@Composable
fun ImageEditorPreview() {
    GuganTheme {
        ImageEditor(
            imageEntity = ImageEntity(0, "")
        )
    }
}
