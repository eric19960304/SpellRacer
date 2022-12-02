package com.example.spellracer.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.spellracer.models.GameResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class DBHelper() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "allGameResults"

    fun fetchGameResults(gameResults: MutableLiveData<List<GameResult>>) {
        db.collection(rootCollection)
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .limit(1000)
            .get()
            .addOnSuccessListener { result ->
                Log.i(javaClass.simpleName, "fetchAllGameResults ${result!!.documents.size}")
                gameResults.postValue(result.documents.mapNotNull {
                    it.toObject(GameResult::class.java)
                })
            }
            .addOnFailureListener {
                Log.w(javaClass.simpleName, "fetchAllGameResults FAILED ", it)
            }
    }

    fun createGameResult(
        gameResult: GameResult,
        gameResults: MutableLiveData<List<GameResult>>
    ) {
        db.collection(rootCollection)
            .add(gameResult)
            .addOnSuccessListener { documentReference ->
                Log.d(javaClass.simpleName, "createGameResult DocumentSnapshot written with ID: ${documentReference.id}")
                fetchGameResults(gameResults)
            }
            .addOnFailureListener { e ->
                Log.w(javaClass.simpleName, "createGameResult Error adding document", e)
            }
    }
}