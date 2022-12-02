package com.example.spellracer

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spellracer.databinding.ActivityGameDetailsBinding
import com.example.spellracer.utils.DiffTextMaker

class GameDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameDetailsBinding

    companion object {
        val key_accuracy = "accuracy"
        val key_wpm = "wpm"
        val key_answer = "answer"
        val key_userInput = "userInput"
        val key_datetime = "date"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val accuracy = intent.extras?.getString(key_accuracy)!!
        val wpm = intent.extras?.getString(key_wpm)!!
        val answer = intent.extras?.getString(key_answer)!!
        val userInput = intent.extras?.getString(key_userInput)!!
        val date = intent.extras?.getString(key_datetime)!!

        val answerWords = answer.split(" ")
        val userInputWords = userInput.split(" ")
        val temp = DiffTextMaker.make(answerWords, userInputWords)
        val userInputDisplay = temp.first

        binding.tvUserInput.text = userInputDisplay
        binding.accuracy.text = accuracy
        binding.wpm.text = wpm
        binding.tvAnswer.text = DiffTextMaker.toColoredText(answer, Color.BLACK)
        binding.date.text = date
    }
}