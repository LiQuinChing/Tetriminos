package com.project.android.tetriminos

import android.content.Context
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import kotlinx.coroutines.*
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {

    private val sharedPref by lazy { // Lazy initialization
        getSharedPreferences("TetrisHighScores", Context.MODE_PRIVATE)
    }
    val viewModel: TetrisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
       // window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()

        val currentHighScore = sharedPref.getInt("HIGH_SCORE", 0)
        viewModel.setInitialScore(currentHighScore)
        fun saveHighScore(newScore: Int) {
            val editor = sharedPref.edit()
            editor.putInt("HIGH_SCORE", newScore)
            editor.apply() // Asynchronous commit
        }

        viewModel.score.observe(this) { newScore ->
            val scoreTextView = findViewById<TextView>(R.id.scoreTextView)
            "Score: $newScore ".also { scoreTextView.text = it }
            if (newScore > currentHighScore) {
                saveHighScore(newScore)
            }

        }



        val button_rotate = findViewById<ImageButton>(R.id.button_rotate)
        val canvas = findViewById<CanvasView>(R.id.canvas)
        val button_left = findViewById<ImageButton>(R.id.button_left)
        val button_right = findViewById<ImageButton>(R.id.button_right)
        val button_fast_down = findViewById<ImageButton>(R.id.button_fast_down)


        button_rotate.setOnClickListener {
            if (Rotate.isRotable()) {
                Rotate.doRotate()
                canvas.invalidate()
            }
        }

        button_left.setOnClickListener {
            if (MoveLeft.isMovableLeft()) {
                MoveLeft.moveLeft()
                canvas.invalidate()
            }
        }
        button_right.setOnClickListener {
            if (MoveRight.isMovableRight()) {
                MoveRight.moveRight()
                canvas.invalidate()
            }
        }
        button_fast_down.setOnClickListener {
            while (!Falling.willLanding(1)) {
                Falling.fallingStep()
            }
        }
        // run game
        game()
    }

    fun game() {
        CoroutineScope(Dispatchers.IO).launch {
            // todo eliminate this
            Level.reset()
            Tetromino.newPiece()
            Level.insertNewPosition()
            setBest()

            // gamplay infinite
            while (true) {
                if (Falling.willLanding(1)) {
                    // check is need to clear rows
                    Level.checkRows()
                    // if landed piece cant entered
                    if (Level.isGameOver()) {
                        resetBest()
                        Level.reset()
                    }
                    Tetromino.newPiece()
                    Level.insertNewPosition()
                } else {
                    Falling.fallingStep()
                }
                //game speed in millisecond
                delay(Tetromino.speed)
                val canvas = findViewById<CanvasView>(R.id.canvas)
                canvas.invalidate()
            }
        }
    }

    private fun setBest() {
        val sharedPreference = getSharedPreferences("HIGH_SCORE", Context.MODE_PRIVATE)
        Level.best = sharedPreference.getInt("high_score", 0)
    }
    private fun resetBest(){
        val sharedPreference = getSharedPreferences("HIGH_SCORE", Context.MODE_PRIVATE)
        if (Level.score > sharedPreference.getInt("high_score", 0)) {
            val editor = sharedPreference.edit()
            editor.putInt("high_score", Level.score)
            editor.apply()
            Level.best = sharedPreference.getInt("high_score", 0)
        }
    }
}