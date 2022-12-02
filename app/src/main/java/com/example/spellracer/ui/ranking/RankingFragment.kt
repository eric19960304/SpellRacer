package com.example.spellracer.ui.ranking

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
import com.example.spellracer.databinding.FragmentRankingBinding
import com.example.spellracer.models.Player
import com.example.spellracer.utils.GameResultAdapter
import com.example.spellracer.utils.PlayerResultAdapter

class RankingFragment : Fragment() {

    private var _binding: FragmentRankingBinding? = null
    private val viewModel: MainViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRankingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv1 = binding.recyclerView1
        val adapter1 = PlayerResultAdapter(true)
        rv1.adapter = adapter1
        rv1.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv1.setHasFixedSize(true)

        val rv2 = binding.recyclerView2
        val adapter2 = PlayerResultAdapter(false)
        rv2.adapter = adapter2
        rv2.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv2.setHasFixedSize(true)

        viewModel.gameResults.observe(viewLifecycleOwner) { it ->
            Log.i(javaClass.simpleName, "gameResults.observe list size: " + it.size)
            val hashMap : HashMap<String, Player> = HashMap<String, Player> ()
            it.forEach { game ->
                if(!hashMap.containsKey(game.uid)) {
                    val player = Player(
                        uid = game.uid,
                        userDisplayName = game.userDisplayName,
                        avgAccuracy = game.accuracy.toDouble(),
                        avgWpm = game.wpm.toDouble()
                    )
                    hashMap.put(game.uid, player)
                } else {
                    val oldPlayer = hashMap.get(game.uid)!!
                    val newAvgAccuracy = ((oldPlayer.numberOfGame * oldPlayer.avgAccuracy) + game.accuracy) / (oldPlayer.numberOfGame + 1)
                    val newAvgWpm = ((oldPlayer.numberOfGame * oldPlayer.avgWpm) + game.wpm) / (oldPlayer.numberOfGame + 1)
                    val newPlayer = Player(
                        uid = game.uid,
                        userDisplayName = game.userDisplayName,
                        avgAccuracy = newAvgAccuracy,
                        avgWpm = newAvgWpm
                    )
                    hashMap.put(game.uid, newPlayer)
                }
            }

            val players : List<Player> = hashMap.keys.map { k ->
                hashMap[k]!!
            }
            players.sortedByDescending {
                it.avgAccuracy
            }
            val playersByAccuracy: List<Player> = players.toList()

            players.sortedByDescending {
                it.avgWpm
            }
            val playersBySpeed: List<Player> = players.toList()

            adapter1.submitList(playersByAccuracy)
            adapter1.notifyDataSetChanged()


            adapter2.submitList(playersBySpeed)
            adapter2.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}