package com.example.spellracer.utils

import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spellracer.databinding.PlayerRowBinding
import com.example.spellracer.models.Player

class PlayerResultAdapter(val isHideSpeed: Boolean = false, val uid: String) : RecyclerView.Adapter<PlayerResultAdapter.VH>() {
    private var players = mutableListOf<Player>()
    private lateinit var context : Context

    // ViewHolder pattern
    inner class VH(val rowBinding: PlayerRowBinding)
        : RecyclerView.ViewHolder(rowBinding.root) {
        init {
            rowBinding.root.setOnClickListener {
                Log.i(javaClass.simpleName, "selected: " + players[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = PlayerRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        context = parent.context
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = players[holder.adapterPosition]
        val rowBinding = holder.rowBinding

        rowBinding.tvPlayerName.text = item.userDisplayName
        rowBinding.tvAccuracy.text = String.format("%.2f", item.avgAccuracy)
        rowBinding.tvSpeed.text = String.format("%.2f", item.avgWpm)
        rowBinding.tvPlayerRank.text = (holder.adapterPosition + 1).toString()

        if(item.uid.equals(uid)) {
            rowBinding.root.setBackgroundColor(Color.YELLOW)
        }

        if(isHideSpeed) {
            rowBinding.tvSpeed.visibility = View.GONE
        } else {
            rowBinding.tvAccuracy.visibility = View.GONE
        }
    }

    fun submitList(items: List<Player>) {
        players.clear()
        players.addAll(items)
    }

    override fun getItemCount() = players.size
}