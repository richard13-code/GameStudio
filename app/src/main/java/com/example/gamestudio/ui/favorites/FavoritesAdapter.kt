package com.example.gamestudio.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gamestudio.databinding.ItemFavoriteBinding
import com.example.gamestudio.model.FavoriteGame

class FavoritesAdapter(
    private val onItemClick: (FavoriteGame) -> Unit
) : ListAdapter<FavoriteGame, FavoritesAdapter.FavoriteViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FavoriteViewHolder(
        private val binding: ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(game: FavoriteGame) {
            binding.tvTitle.text = game.name
            binding.tvGenres.text = game.genres
            binding.tvRating.text = "⭐ ${game.rating}"

            Glide.with(binding.ivCover)
                .load(game.backgroundImage)
                .placeholder(com.example.gamestudio.R.drawable.ic_launcher_background)
                .centerCrop()
                .into(binding.ivCover)

            binding.root.setOnClickListener { onItemClick(game) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<FavoriteGame>() {
            override fun areItemsTheSame(oldItem: FavoriteGame, newItem: FavoriteGame) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: FavoriteGame, newItem: FavoriteGame) =
                oldItem == newItem
        }
    }
}