package com.example.spellracer.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spellracer.MainViewModel
import com.example.spellracer.databinding.FragmentHistoryBinding
import com.example.spellracer.models.GameResult
import com.example.spellracer.utils.GameResultAdapter

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val viewModel: MainViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = binding.recyclerView
        val adapter = GameResultAdapter()
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv.setHasFixedSize(true)

        viewModel.gameResults.observe(viewLifecycleOwner) {
            val userGames = it.filter {
                it.uid.equals(viewModel.uid.value)
            }
            Log.i(javaClass.simpleName, "gameResults.observe list size: " + userGames.size)
            adapter.submitList(userGames)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}