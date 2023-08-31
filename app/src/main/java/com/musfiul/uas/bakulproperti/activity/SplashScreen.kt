package com.musfiul.uas.bakulproperti.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import com.musfiul.uas.bakulproperti.MainActivity
import com.musfiul.uas.bakulproperti.R

class SplashScreen : AppCompatActivity() {
    private val SPLASH_DELAY: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Use a Handler to delay the transition to the next activity
        Handler().postDelayed({
            val intent = Intent(this@SplashScreen, MainActivity::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)
    }
}

