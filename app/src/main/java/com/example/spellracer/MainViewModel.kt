package com.example.spellracer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class MainViewModel : ViewModel() {
    var displayName = MutableLiveData<String>("")
    var uid = MutableLiveData<String>("")

    fun updateUser() {
        val user = FirebaseAuth.getInstance().currentUser
        displayName.value = user?.displayName
        uid.value = user?.uid
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        userLogout()
    }

    private fun userLogout() {
        displayName.postValue("")
        uid.postValue("")
    }
}