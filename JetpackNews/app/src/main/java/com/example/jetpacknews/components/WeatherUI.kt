package com.example.jetpacknews.components

import android.view.Display.Mode
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpacknews.database.CurrentWeather
import com.example.jetpacknews.ui.theme.JetpackNewsTheme
import com.example.jetpacknews.viewmodel.WeatherViewModel

@Composable
fun WeatherUI(
    weatherViewModel: WeatherViewModel
) {
    val currentWeather by weatherViewModel.currentWeather.observeAsState()
    val locationName by weatherViewModel.locationNameFlow.collectAsState()

    currentWeather?.let {
        WeatherPage(
            currentWeather = it,
            locationName = locationName
        )
    }
}

@Composable
fun WeatherPage(
    modifier: Modifier = Modifier,
    currentWeather: CurrentWeather,
    locationName: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 20.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = locationName,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = currentWeather.temperature.toString() + " °C",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            WeatherDetail(title = "Wind Speed", value = "${currentWeather.windSpeed} km/h", icon = "🌬️")
            WeatherDetail(title = "UV", value = "${currentWeather.uvIndex}", icon = "☀️")
            WeatherDetail(title = "Humidity", value = "${currentWeather.humidity} %", icon = "💧")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            WeatherDetail(title = "Wind Direction", value = "${currentWeather.windDirection} °", icon = "🌫️")
            WeatherDetail(title = "Visibility", value = "${currentWeather.visibility} km", icon = "👀")
        }
    }
}

fun dateFormat(date: String): String{

    return date
}

@Composable
fun WeatherDetail(
    title: String,
    value: String,
    icon: String
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .padding(8.dp),

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherDetailPreview() {
    JetpackNewsTheme {
        WeatherDetail(title = "wind speed", value = "4 km/h", icon = "🌬️")
    }
}