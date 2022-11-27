package com.example.spellracer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spellracer.databinding.ActivityLoginBinding
import com.example.spellracer.firebase.AuthInit
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mSignInClient: GoogleSignInClient
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.startMainActivity(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(com.google.android.gms.common.api.Scope("https://www.googleapis.com/auth/cloud-platform"))
            .build()

        mSignInClient = GoogleSignIn.getClient(this, options)

        val signInButton: SignInButton = binding.signInButton
        signInButton.setOnClickListener{
            startGoogleSingOn()
        }
    }

    private fun startGoogleSingOn() {
        AuthInit(signInLauncher)
    }

    private fun startMainActivity(result: FirebaseAuthUIAuthenticationResult) {
        val returnIntent = Intent().apply {
            putExtra(MainActivity.loginResultKey, result.resultCode == Activity.RESULT_OK)
        }
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}