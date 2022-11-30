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

    // I just looked at the response and "parsed" it by eye
    data class TriviaResponse(val results: List<TriviaQuestion>)
    data class TriviaTokenResponse(val token: String)

    companion object {
        // Leave this as a simple, base URL.  That way, we can have many different
        // functions (above) that access different "paths" on this server
        // https://square.github.io/okhttp/4.x/okhttp/okhttp3/-http-url/
        var url = HttpUrl.Builder()
            .scheme("https")
            .host("opentdb.com")
            .build()

        // Public create function that ties together building the base
        // URL and the private create function that initializes Retrofit
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