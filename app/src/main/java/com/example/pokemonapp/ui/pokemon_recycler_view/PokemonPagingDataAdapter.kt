package com.example.pokemonapp.ui.pokemon_recycler_view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pokemonapp.R
import com.example.pokemonapp.databinding.PokemonRecyclerViewItemBinding
import com.example.pokemonapp.domain.PokemonPreview
import com.example.pokemonapp.ui.pokemon_list.PokemonListFragmentDirections

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

    fun notifyPokemonLoaded(loadedPokemonsIdList: List<Int?>) {
        snapshot().items.forEachIndexed {i, pokemonPreview ->
            if (loadedPokemonsIdList.contains(pokemonPreview.id)) {
                pokemonPreview.loadedFullInfo = true
                notifyItemChanged(i)
            }
        }
    }
}

class PokemonViewHolder(
    private val binding: PokemonRecyclerViewItemBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(pokemonPreview: PokemonPreview) {
        with(binding) {
            rcItemName.text = pokemonPreview.pokemonName
            rcItemImage.load(pokemonPreview.imageUrl)
            rcItemId.text = "id: ${pokemonPreview.id}"

            if (pokemonPreview.loadedFullInfo) {
                rcItemLoadStatus.setImageResource(R.drawable.ic_done)
                rcItemLoadStatus.setColorFilter(Color.GREEN)
            } else {
                rcItemLoadStatus.setImageResource(R.drawable.ic_fail)
                rcItemLoadStatus.setColorFilter(Color.RED)
            }

            root.setOnClickListener {
                val action = PokemonListFragmentDirections.actionToDetails(pokemonPreview.id ?: 0)
                itemView.findNavController().navigate(action)
            }
        }
    }
}

private object PokemonDiffItemCallbacks: DiffUtil.ItemCallback<PokemonPreview>() {
    override fun areItemsTheSame(oldItem: PokemonPreview, newItem: PokemonPreview): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: PokemonPreview, newItem: PokemonPreview): Boolean {
        return oldItem.pokemonName == oldItem.pokemonName && oldItem.loadedFullInfo == newItem.loadedFullInfo
    }
}