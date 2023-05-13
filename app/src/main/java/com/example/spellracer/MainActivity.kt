package com.example.spellracer

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.spellracer.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    companion object {
        const val loginResultKey = "loginResult"
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    lateinit var tts: TextToSpeech

    private var loginLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d(javaClass.simpleName, "login ok")
                val data: Intent? = result.data
                data?.extras?.apply {
                    val loginResult = getBoolean(loginResultKey)
                    if(!loginResult) {
                        Toast.makeText(applicationContext, "Login Failed", Toast.LENGTH_LONG)
                    } else {
                        actionUponSuccessfulLogin()
                    }
                }
            } else {
                Log.w(javaClass.simpleName, "Bad activity return code ${result.resultCode}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // redirect to login page if user haven't logged in
        val mFirebaseAuth = FirebaseAuth.getInstance()
        if (mFirebaseAuth.currentUser == null) {
            // Not signed in, launch the Sign In activity
            startLoginActivity()
        } else {
            actionUponSuccessfulLogin()
        }

        tts = TextToSpeech(this, this)
        viewModel.fetchGameResults()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                AuthUI.getInstance().signOut(applicationContext).addOnCompleteListener {
                    FirebaseAuth.getInstance().signOut()
                    viewModel.userLogout()
                    startLoginActivity()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startLoginActivity() {
        loginLauncher.launch(Intent(this, LoginActivity::class.java))
    }

    override fun onInit(status: Int) {
        Log.d(javaClass.simpleName, "onInit" + status.toString())
        if(status == TextToSpeech.SUCCESS) {
            tts.language = Locale.UK
            tts.setSpeechRate(0.75f)
            viewModel.fetchGameResults()
        } else {
            finish()
        }
    }

    fun speak(text: String) {
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,null)
    }

    private fun actionUponSuccessfulLogin() {
        viewModel.updateUser()
        viewModel.fetchGameResults()
    }
}