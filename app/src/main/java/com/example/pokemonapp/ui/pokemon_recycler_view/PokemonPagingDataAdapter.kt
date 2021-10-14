package com.example.pokemonapp.ui.pokemon_recycler_view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pokemonapp.databinding.PokemonRecyclerViewItemBinding
import com.example.pokemonapp.domain.PokemonPreview

class PokemonPagingDataAdapter(context: Context):
    PagingDataAdapter<PokemonPreview, PokemonViewHolder>(PokemonDiffItemCallbacks) {

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PokemonRecyclerViewItemBinding.inflate(inflater, parent, false)
        return PokemonViewHolder(binding)
    }
}

class PokemonViewHolder(
    private val binding: PokemonRecyclerViewItemBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(pokemonPreview: PokemonPreview) {
        with(binding) {
            rcItemTextView.text = pokemonPreview.pokemonName
            rcItemImage.load(pokemonPreview.imageUrl)
        }
    }
}

private object PokemonDiffItemCallbacks: DiffUtil.ItemCallback<PokemonPreview>() {
    override fun areItemsTheSame(oldItem: PokemonPreview, newItem: PokemonPreview): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: PokemonPreview, newItem: PokemonPreview): Boolean {
        return oldItem.pokemonName == oldItem.pokemonName
    }
}