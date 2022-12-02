package com.example.spellracer.ui.play

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spellracer.api.TriviaApi
import com.example.spellracer.api.TriviaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayViewModel : ViewModel() {
    private val triviaApi = TriviaApi.create()
    private val repository = TriviaRepository(triviaApi)
    private var apiToken: String = ""

    var questions: MutableLiveData<List<String>> = MutableLiveData<List<String>>().apply {
        value = emptyList()
    }
    var currentQuestion: MutableLiveData<String> = MutableLiveData<String>().apply {
        value = ""
    }

    fun popQuestion() {
        val originalArray: List<String> = questions.value!!
        if(originalArray.isEmpty()) {
            throw java.lang.RuntimeException("popQuestion: popping empty list")
        }

        val newList: List<String> = originalArray.subList(1, originalArray.size)
        questions.postValue(newList)
        currentQuestion.postValue(originalArray[0])
    }

    fun fetchQuestions() {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext + Dispatchers.IO) {
            if(apiToken.isEmpty()) {
                apiToken = repository.fetchApiToken()
            }
            val validQuestions: List<String> = repository.fetchQuestions(apiToken)
                .filter {
                    it.correctAnswer == "True" && isSimpleSentence(it.question)
                }
                .map { q ->
                    q.question.filter { c ->
                        c.isLetter() || c == ' '
                    }
                }
                .filter {
                    q ->
                        val ws = q.split(" ")
                        val maxWord = ws.maxBy { w -> w.length }
                        maxWord.length <= 16 && ws.size <= 15
                }
            questions.postValue(validQuestions)
        }
    }

    private fun isSimpleSentence(text: String) : Boolean {
        text.forEach {
            if(it !in arrayOf(',', '.', ' ') && !it.isLetter()) {
                return false
            }
        }
        return true
    }
}