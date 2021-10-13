package com.example.pokemonapp.ui.pokemon_recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pokemonapp.databinding.PokemonRecyclerViewItemBinding
import com.example.pokemonapp.domain.PokemonPreview

class PokemonRecyclerViewAdapter: RecyclerView.Adapter<PokemonRecyclerViewAdapter.PokemonItemViewHolder>() {

    var itemsList: List<PokemonPreview> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class PokemonItemViewHolder(
        val binding: PokemonRecyclerViewItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PokemonRecyclerViewItemBinding.inflate(inflater, parent, false)
        return PokemonItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonItemViewHolder, position: Int) {
        val pokemonPreviewItem = itemsList[position]

        with(holder.binding) {
            this.rcItemImage.load(pokemonPreviewItem.imageUrl)
            this.rcItemTextView.text = pokemonPreviewItem.pokemonName
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}