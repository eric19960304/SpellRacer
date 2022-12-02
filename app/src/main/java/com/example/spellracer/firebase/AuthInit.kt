package com.example.spellracer.firebase

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class AuthInit(signInLauncher: ActivityResultLauncher<Intent>) {
    init {
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null) {
            Log.d(javaClass.simpleName, "user null")
            // Choose authentication providers
            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build())

            // Create and launch sign-in intent
            // XXX Write me. Set authentication providers and start sign-in for user
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()
            signInLauncher.launch(signInIntent)
        } else {
            Log.d(javaClass.simpleName, " user ${user.displayName} email ${user.email}")
        }
    }
}
