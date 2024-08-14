package com.pravin.gugan.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.widget.Toast
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import java.io.ByteArrayOutputStream
import com.itextpdf.layout.element.Image as PdfImage
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas as ComposeCanvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun CaptureComposableAsBitmap(
    content: @Composable () -> Unit,
    onBitmapCaptured: (Bitmap) -> Unit
) {
    val density = LocalDensity.current.density
    val context = LocalContext.current

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val bitmap = Bitmap.createBitmap(
            (size.width * density).toInt(),
            (size.height * density).toInt(),
            Bitmap.Config.ARGB_8888
        )
//        val canvas = ComposeCanvas(bitmap)
//        canvas.drawContent(content)

        onBitmapCaptured(bitmap)
    }
}

fun convertBitmapToPdf(bitmap: Bitmap, fileName: String, context: Context) {
    val pdfFile = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "$fileName.pdf")
    val pdfWriter = PdfWriter(FileOutputStream(pdfFile))
    val pdfDocument = PdfDocument(pdfWriter)
    val document = Document(pdfDocument)

    try {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val imageData = ImageDataFactory.create(outputStream.toByteArray())
        val pdfImage = PdfImage(imageData)
        document.add(pdfImage)

        document.close()
        Toast.makeText(context, "PDF saved successfully", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error saving PDF", Toast.LENGTH_SHORT).show()
    }
}

