package com.example.spellracer.api

class TriviaRepository(private val api: TriviaApi) {
    suspend fun fetchQuestions(token: String): List<TriviaQuestion> {
        return api.getQuestions(token).results
    }

    suspend fun fetchApiToken(): String {
        return api.getApiToken().token
    }
}