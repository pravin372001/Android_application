package com.example.newswithweather.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.newswithweather.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.LightWithoutAppBar)
        setContentView(R.layout.activity_splash)
        val imageView : ImageView = findViewById(R.id.animationView)
        Glide.with(this).load(R.drawable.splash_loading).into(imageView)
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000
        )
    }
}