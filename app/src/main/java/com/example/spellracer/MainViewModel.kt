package com.example.spellracer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spellracer.models.GameResult
import com.example.spellracer.utils.DBHelper
import com.google.firebase.auth.FirebaseAuth

class MainViewModel : ViewModel() {
    var displayName = MutableLiveData<String>("")
    var uid = MutableLiveData<String>("")
    var gameResults = MutableLiveData<List<GameResult>>()
    val dbHelp = DBHelper()

    fun updateUser() {
        val user = FirebaseAuth.getInstance().currentUser
        displayName.value = user?.displayName
        uid.value = user?.uid
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        userLogout()
    }

    fun fetchGameResults() {
        dbHelp.fetchGameResults(gameResults)
    }

    private fun userLogout() {
        displayName.postValue("")
        uid.postValue("")
    }
}