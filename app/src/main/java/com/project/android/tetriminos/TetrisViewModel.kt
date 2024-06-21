package com.project.android.tetriminos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TetrisViewModel : ViewModel() {

    // UI-related data
    private val _score = MutableLiveData(0)
    val score: LiveData<Int> = _score

    private val _level = MutableLiveData(1)
    val level: LiveData<Int> = _level

    private val _isGameOver = MutableLiveData(false)
    val isGameOver: LiveData<Boolean> = _isGameOver


    // Game Logic functions
    fun setInitialScore(score: Int) {
        _score.value = score // Update MutableLiveData internally
    }
    fun incrementScore() {
        _score.value = (_score.value ?: 0) + 10  // Example
    }

    fun increaseLevel() {
        _level.value = (_level.value ?: 1) + 1
    }

    fun onGameOver() {
        _isGameOver.value = true
    }
}
