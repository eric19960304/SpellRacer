package com.example.spellracer.api

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApi {
    // https://opentdb.com/api.php?amount=100&type=boolean
    @GET("api.php?amount=100&type=boolean")
    suspend fun getQuestions(@Query("token") token: String) : TriviaResponse

    // https://opentdb.com/api_token.php?command=request
    @GET("api_token.php?command=request")
    suspend fun getApiToken() : TriviaTokenResponse

    data class TriviaResponse(val results: List<TriviaQuestion>)
    data class TriviaTokenResponse(val token: String)

    companion object {
        var url = HttpUrl.Builder()
            .scheme("https")
            .host("opentdb.com")
            .build()

        fun create(): TriviaApi = create(url)

        private fun create(httpUrl: HttpUrl): TriviaApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TriviaApi::class.java)
        }
    }
}