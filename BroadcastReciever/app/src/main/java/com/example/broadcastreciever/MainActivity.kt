package com.example.broadcastreciever

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.broadcastreciever.boardcast.AirPlaneModeReceiver
import com.example.broadcastreciever.boardcast.TestBroadcastReceiver
import com.example.broadcastreciever.ui.theme.BroadcastRecieverTheme

class MainActivity : ComponentActivity() {

    private val airplaneRecevier = AirPlaneModeReceiver()
    private val testBroadcast = TestBroadcastReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(airplaneRecevier, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))
        registerReceiver(testBroadcast, IntentFilter("TEST_ACTION"))
        setContent {
            BroadcastRecieverTheme {
                MyApp(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                    )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(airplaneRecevier)
        unregisterReceiver(testBroadcast)
    }
}

@Composable
fun MyApp(modifier: Modifier = Modifier){
    Greeting(name = "Broadcast Reciever Example")
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$name!",
            modifier = modifier
        )
        ElevatedButton(onClick = {

        }) {
            Text(text = "Click Me")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BroadcastRecieverTheme {
        MyApp()
    }
}