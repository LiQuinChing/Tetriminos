package com.project.android.tetriminos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val tetris = findViewById<Button>(R.id.tetris)
        tetris.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}