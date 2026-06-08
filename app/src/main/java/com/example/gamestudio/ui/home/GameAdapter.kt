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

    // 1. Se elimina getItemViewType porque ya solo hay un tipo de vista

    // 2. Simplificamos onCreateViewHolder para que solo infle el diseño de Grid
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemGameBinding.inflate(inflater, parent, false)
        return GridViewHolder(binding)
    }

    // 3. Simplificamos onBindViewHolder
    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val game = getItem(position)
        holder.bind(game)
    }

    // El ViewHolder permanece igual, usando las variables correctas (name y backgroundImage)
    inner class GridViewHolder(private val binding: ItemGameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(game: Game) {
            binding.tvTitle.text = game.name

            Glide.with(binding.ivCover.context)
                .load(game.backgroundImage)
                .centerCrop()
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