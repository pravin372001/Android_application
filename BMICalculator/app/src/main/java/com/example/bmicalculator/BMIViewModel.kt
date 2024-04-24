package com.example.bmicalculator

// BMIViewModel.kt
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableField
import kotlin.math.pow

class BMIViewModel : ViewModel() {
    val weight = ObservableField<String>()
    val height = ObservableField<String>()
    val bmiResult = ObservableField<String>()
    val bmiStatus = ObservableField<String>()

    fun calculateBMI() {
        val weightValue = weight.get()?.toDoubleOrNull()
        val heightValue = height.get()?.toDoubleOrNull()

        if (weightValue != null && heightValue != null && heightValue > 0) {
            val bmi = weightValue / (heightValue / 100).pow(2)
            bmiResult.set("Your BMI is ${String.format("%.2f", bmi)}")

            // Determine BMI status
            bmiStatus.set(getBMIStatus(bmi))
        } else {
            bmiResult.set("Invalid input")
            bmiStatus.set("")
        }
    }

    private fun getBMIStatus(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi in 18.5..24.9 -> "Normal weight"
            bmi in 25.0..29.9 -> "Overweight"
            else -> "Obesity"
        }
    }
}

