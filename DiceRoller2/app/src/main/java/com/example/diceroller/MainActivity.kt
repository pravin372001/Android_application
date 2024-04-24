package com.example.diceroller

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    lateinit var diceImage: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        diceImage = findViewById(R.id.diceId)
        val rollButton : Button = findViewById(R.id.roll_button)
        rollButton.setOnClickListener{
            Toast.makeText(this, "Rolled", Toast.LENGTH_SHORT).show()
            rollDice()
        }

        val reset : Button  = findViewById(R.id.reset)
        reset.setOnClickListener {
            Toast.makeText(this, "Reseted", Toast.LENGTH_SHORT).show()
            reset()
        }
    }

    private fun rollDice() {
        val resText: TextView = findViewById(R.id.text_view)
        val rand = Random.nextInt(6) + 1
        val drawableResource = when (rand) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
        val drawable: Drawable? = resources.getDrawable(drawableResource)
        diceImage.setImageDrawable(drawable)
        resText.text = "Dice Rolled : $rand"
    }


    private fun reset() {
        val resText : TextView = findViewById(R.id.text_view)
        findViewById<ImageView>(R.id.diceId).setImageDrawable(resources.getDrawable(R.drawable.empty_dice))
        resText.text = "Reseted"
    }
}