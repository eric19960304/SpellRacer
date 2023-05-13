package com.example.spellracer.ui.play

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.spellracer.MainActivity
import com.example.spellracer.MainViewModel
import com.example.spellracer.R
import com.example.spellracer.databinding.FragmentPlayBinding
import com.example.spellracer.models.GameResult
import com.example.spellracer.utils.DiffTextMaker
import com.example.spellracer.utils.Timer
import kotlinx.coroutines.*
import java.time.Duration
import java.time.Instant


class PlayFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentPlayBinding? = null
    private val viewModel: MainViewModel by activityViewModels()

    private val binding get() = _binding!!
    private lateinit var mainActivity : MainActivity
    private lateinit var playViewModel: PlayViewModel
    private lateinit var speakTask: Deferred<Unit>

    private var startTime: Instant? = null


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(!this::playViewModel.isInitialized) {
            playViewModel =
                ViewModelProvider(this).get(PlayViewModel::class.java)
        }

        mainActivity = requireActivity() as MainActivity

        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        playViewModel.questions.observe(viewLifecycleOwner) {
            if(it.isEmpty()) {
                playViewModel.fetchQuestions()
            }
        }

        playViewModel.currentQuestion.observe(viewLifecycleOwner) {
            Log.i(javaClass.simpleName, "currentQuestion: ***** $it *****")
        }

        viewModel.displayName.observe(viewLifecycleOwner) {
            binding.tvWelcome.text = "Welcome, ${it}!"
        }

        binding.btnStartInit.setOnClickListener {
            startGame()
        }
        binding.btnStartResult.setOnClickListener {
            startGame()
        }
        binding.btnSubmit.setOnClickListener {
            submitAnswer()
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startGame() {
        if(!this::playViewModel.isInitialized || playViewModel.questions.value!!.isEmpty()) {
            Toast.makeText(context, "Please wait, loading questions...", Toast.LENGTH_LONG).show()
            return
        }

        playViewModel.popQuestion()

        // clean up input area
        mainActivity.tts.stop()
        if(this::speakTask.isInitialized && speakTask.isActive) {
            speakTask.cancel()
        }
        binding.etInput.text.clear()

        // set screen
        binding.initScreen.visibility = View.GONE
        binding.playScreen.visibility = View.VISIBLE
        binding.resultScreen.visibility = View.GONE

        focusAndShowSoftKeyboard(binding.etInput)

        val timer = Timer(binding.tvCountDown)
        launch {
            speakTask = async {
                timer.countDown(3500L)
                mainActivity.speak(playViewModel.currentQuestion.value!!)
                while(mainActivity.tts.isSpeaking){
                    delay(100)
                }
                binding.tvCountDown.text = "End of Audio"
                startTime = Instant.now()
            }
        }
    }

    private fun submitAnswer() {
        hideKeyboard()

        val answer: String = playViewModel.currentQuestion.value!!
        val userInput: String = binding.etInput.text.toString()

        // calculate the time used in milliseconds
        val endTime = Instant.now()
        val timeUsed: Duration = if(startTime != null) Duration.between(startTime, endTime) else Duration.ZERO
        Log.d(javaClass.simpleName, "timeUsed (millis): " + timeUsed.toMillis().toString())

        val answerWords = answer.split(" ")
        val userInputWords = userInput.split(" ")
        val temp = DiffTextMaker.make(answerWords, userInputWords)
        val userInputDisplay = temp.first
        val correctWordsCount = temp.second

        // calculate accuracy and speed
        val accuracy = (correctWordsCount.toFloat() / answerWords.size.toFloat() * 100).toInt()
        var wpm = (userInputWords.size.toFloat() / (timeUsed.toMillis().toFloat() / (60 * 1000).toFloat()) ).toInt()

        if(correctWordsCount == 0) {
            wpm = 0
        }

        // set UI value
        binding.tvUserInput.text = userInputDisplay
        binding.accuracy.text = accuracy.toString()
        binding.wpm.text = wpm.toString()
        binding.tvAnswer.text = DiffTextMaker.toColoredText(playViewModel.currentQuestion.value!!, Color.BLACK)

        // set screen
        binding.initScreen.visibility = View.GONE
        binding.playScreen.visibility = View.GONE
        binding.resultScreen.visibility = View.VISIBLE

        // save game result to Firestore
        viewModel.dbHelp.createGameResult(
            GameResult(
                uid = viewModel.uid.value!!,
                userDisplayName = viewModel.displayName.value!!,
                accuracy = accuracy,
                wpm = wpm,
                answer = answer,
                userInput = userInput),
            viewModel.gameResults
        )
    }

    private fun focusAndShowSoftKeyboard(view: View) {
        view.requestFocus()
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireActivity().window.decorView.rootView.windowToken, 0)
    }
}