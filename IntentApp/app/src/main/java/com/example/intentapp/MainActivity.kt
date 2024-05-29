package com.example.intentapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.startActivity
import com.example.intentapp.ui.theme.IntentAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntentAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    IntentToNextScreen(modifier = Modifier.padding(it))
                }
            }
        }
    }
}

@Composable
fun IntentToNextScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Box (
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ElevatedButton(onClick ={
            Intent(context, SecondActivity::class.java).also {
                context.startActivity(it)
            }
        } ) {
            Text(text = "Click Me")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IntentToNextScreenPreview() {
    IntentAppTheme {
        IntentToNextScreen()
    }
}