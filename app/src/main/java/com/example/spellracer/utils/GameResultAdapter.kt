package com.example.spellracer.utils

import android.content.Context
import android.content.Intent
import android.icu.text.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spellracer.GameDetailsActivity
import com.example.spellracer.databinding.GameRowBinding
import com.example.spellracer.models.GameResult

class GameResultAdapter
    : RecyclerView.Adapter<GameResultAdapter.VH>() {
    private var gameResults = mutableListOf<GameResult>()
    private lateinit var context : Context

    // ViewHolder pattern
    inner class VH(val rowBinding: GameRowBinding)
        : RecyclerView.ViewHolder(rowBinding.root) {
        init {
            rowBinding.root.setOnClickListener {
                Log.i(javaClass.simpleName, "selected: " + gameResults[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = GameRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        context = parent.context
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = gameResults[holder.adapterPosition]
        val rowBinding = holder.rowBinding

        rowBinding.tvAccuracy.text = item.accuracy.toString()
        rowBinding.tvSpeed.text = item.wpm.toString()
        val datetimeStr: String = if (item.timeStamp == null) ""
            else DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM)
                .format(item.timeStamp.toDate()).toString()
        rowBinding.tvDate.text = datetimeStr

        rowBinding.root.setOnClickListener {
            val gameIntent = Intent(context, GameDetailsActivity::class.java)
            gameIntent.putExtra(GameDetailsActivity.key_accuracy, item.accuracy.toString())
            gameIntent.putExtra(GameDetailsActivity.key_wpm, item.wpm.toString())
            gameIntent.putExtra(GameDetailsActivity.key_answer, item.answer)
            gameIntent.putExtra(GameDetailsActivity.key_userInput, item.userInput)
            gameIntent.putExtra(GameDetailsActivity.key_datetime, datetimeStr)
            context.startActivity(gameIntent)
        }
    }

    fun submitList(items: List<GameResult>) {
        gameResults.addAll(items)
    }

    override fun getItemCount() = gameResults.size
}