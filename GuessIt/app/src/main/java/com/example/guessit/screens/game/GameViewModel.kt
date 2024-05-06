package com.example.guessit.screens.game

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the time when the phone will start buzzing each second
        private const val COUNTDOWN_PANIC_SECONDS = 10L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 60000L
    }

    private var timer : CountDownTimer

    // The current word
    var word = MutableLiveData<String>()

    // The current score
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private val _gameFinished = MutableLiveData<Boolean>()
    val gameFinished: LiveData<Boolean>
        get() = _gameFinished;

    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    // Event which triggers the end of the game
    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>
    private var len: Int

    init {
        _gameFinished.value = false
        Log.i("GameViewModel", "GameViewModel Created")
        resetList()
        Log.i("GameViewModel", "word size = ${wordList.size}")
        len = wordList.size
        nextWord()
        Log.i("GameViewModel", "len = ${len}")
        _score.value = 0
        // Creates a timer which triggers the end of the game when it finishes
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = (millisUntilFinished / ONE_SECOND)
            }

            override fun onFinish() {
                _currentTime.value = DONE
                _eventGameFinish.value = true
            }
        }

        timer.start()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
//            "queen",
//            "hospital",
//            "basketball",
//            "cat",
//            "change",
//            "snail",
//            "soup",
//            "calendar",
//            "sad",
//            "desk",
//            "guitar",
//            "home",
//            "railway",
//            "zebra",
//            "jelly",
//            "car",
//            "crow",
//            "trade",
//            "bag",
//            "roll",
//            "bubble"
            "Hari",
            "Nagaraj",
            "Jothi",
            "Ameen",
            "Dharakeswar",
            "Selva",
            "SriGowri",
            "Navreen",
            "Chandru",
            "Balaji",
            "satheesh",
            "Nivetha",
            "JayaGowri",
            "Anand",
            "Sadhana",
            "Swami",
            "Santhosh",
            "Praveen Rao",
            "Gowsalya"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        Log.i("GameViewModel", "score = ${score.value}")
        if (wordList.isEmpty()) {
            Log.i("GameViewModel", "score = ${score.value} and len ${len}")
            if(score.value == len)
            {
                _gameFinished.value = true
            }
            resetList()
        } else {
            word.value = wordList.removeAt(0)
        }
    }

    /** Methods for buttons presses **/

    fun onSkip() {
        _score.value = (score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = (score.value)?.plus(1)
        nextWord()
    }

    fun onGameFinished() {
        _eventGameFinish.value = false
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
        Log.i("GameViewModel", "GameViewModel Destroyed")
    }

}