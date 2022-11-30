package com.example.spellracer.ui.play

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.spellracer.MainActivity
import com.example.spellracer.MainViewModel
import com.example.spellracer.R
import com.example.spellracer.databinding.FragmentPlayBinding
import com.example.spellracer.utils.Timer
import kotlinx.coroutines.*


class PlayFragment : Fragment(), CoroutineScope by MainScope() {

    private var _binding: FragmentPlayBinding? = null
    private val viewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mainActivity : MainActivity
    private lateinit var playViewModel: PlayViewModel
    private var darkGreen: Int = 0
    private var darkRed: Int = 0
    private lateinit var speakTask: Deferred<Unit>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        playViewModel =
            ViewModelProvider(this).get(PlayViewModel::class.java)

        mainActivity = requireActivity() as MainActivity

        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        darkGreen = ContextCompat.getColor(requireContext(), R.color.dark_green)
        darkRed = ContextCompat.getColor(requireContext(), R.color.dark_red)

        playViewModel.questions.observe(viewLifecycleOwner) {
            if(it.isEmpty()) {
                playViewModel.fetchQuestions()
            }
        }

        playViewModel.currentQuestion.observe(viewLifecycleOwner) {
            binding.tvAnswer.text = toColoredText(it, Color.BLACK)
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

        Log.d("startGame number of questions", playViewModel.questions.value!!.size.toString())
        Log.d("startGame next questions", playViewModel.questions.value!![0])

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
            }
        }
    }

    private fun submitAnswer() {
        hideKeyboard()

        val answerWords: List<String> = playViewModel.currentQuestion.value!!.split(" ")
        val userInputWords: List<String> = binding.etInput.text.split(" ")
        binding.tvUserInput.text = ""

        userInputWords.forEachIndexed { index, inputWord ->
            if(index > 0) {
                binding.tvUserInput.append(toColoredText(" ", Color.BLACK))
            }
            if(index < answerWords.size) {
                val answerWord = answerWords[index]
                val isCorrect = answerWord.equals(inputWord, ignoreCase = true)
                binding.tvUserInput.append(toColoredText(inputWord, if(isCorrect) darkGreen else darkRed))
            } else {
                binding.tvUserInput.append(toColoredText(inputWord, darkRed))
            }
        }

        // set screen
        binding.initScreen.visibility = View.GONE
        binding.playScreen.visibility = View.GONE
        binding.resultScreen.visibility = View.VISIBLE
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

    private fun toColoredText(s: String, colorId: Int): SpannableString {
        val ss = SpannableString(s)
        ss.setSpan(
            ForegroundColorSpan(colorId),
            0, ss.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        return ss
    }
}