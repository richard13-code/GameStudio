package com.example.gamestudio.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestudio.databinding.ItemGameBinding
import com.example.gamestudio.model.Game

class GameAdapter(
    private val onItemClick: (Game) -> Unit = {}
) : ListAdapter<Game, GameAdapter.GridViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val binding = ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GridViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GridViewHolder(private val binding: ItemGameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(game: Game) {
            binding.tvTitle.text = game.name

            Glide.with(binding.ivCover.context)
                .load(game.backgroundImage)
                .centerCrop()
                .placeholder(com.example.gamestudio.R.drawable.ic_launcher_background)
                .into(binding.ivCover)

            binding.root.setOnClickListener { onItemClick(game) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Game>() {
            override fun areItemsTheSame(oldItem: Game, newItem: Game) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Game, newItem: Game) = oldItem == newItem
        }
    }
}