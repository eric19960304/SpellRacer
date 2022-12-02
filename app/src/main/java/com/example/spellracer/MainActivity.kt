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
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
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
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.signOut()
                startLoginActivity()
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
            viewModel.uid.value.apply {
                val uid = viewModel.uid.value
                if (uid != null && uid.isNotEmpty()) {
                    speakWelcomeSpeech()
                }
            }

        } else {
            finish()
        }
    }

    fun speak(text: String) {
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,null)
    }

    private fun actionUponSuccessfulLogin() {
        viewModel.updateUser()
        if(this::tts.isInitialized) {
            speakWelcomeSpeech()
        }
        viewModel.fetchGameResults()
    }

    private fun speakWelcomeSpeech() {
        speak("Welcome to spell racer! Please start a new game by clicking the button")
    }
}