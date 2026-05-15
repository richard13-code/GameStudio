package com.example.gamestudio.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gamestudio.databinding.ItemGameNeonBinding

data class Game(val summary: String)

class GameAdapter(private val games: List<Game>) : RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemGameNeonBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGameNeonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = games[position]
        holder.binding.tvGameSummary.text = game.summary
        
        holder.itemView.setOnClickListener {
            val isVisible = holder.binding.tvGameSummary.visibility == View.VISIBLE
            holder.binding.tvGameSummary.visibility = if (isVisible) View.GONE else View.VISIBLE
        }
    }

    override fun getItemCount() = games.size
}