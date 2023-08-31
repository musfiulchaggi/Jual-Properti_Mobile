package com.musfiul.uas.bakulproperti.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.musfiul.uas.bakulproperti.R
import com.musfiul.uas.bakulproperti.helper.Helper

class Tentang : AppCompatActivity() {
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tentang)

        toolbar = findViewById(R.id.toolbar)

        Helper().setToolbar(this, toolbar, "Tentang Kami")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}