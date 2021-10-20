package com.example.pokemonapp.ui.pokemon_recycler_view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pokemonapp.R
import com.example.pokemonapp.databinding.PokemonRecyclerViewItemBinding
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.domain.PokemonPreview
import com.example.pokemonapp.ui.pokemon_list.PokemonListFragmentDirections

/*
*       This is adapter for recyclerView
*       It use pokemonPreview model instead of pokemon model because we can load list of pokemonDetails
*       very fast with 1 api call and we can load only one pokemon with one api call.
*
*       Pokemon api call is very heavy compare to list of pokemonPreview.
*       I chose this way for speed of loading. Way with loading every pokemon model for api (30 big
*       api call for one recyclerView page) will kill user experience because recycler view show
*       item very slowly;
*
*       First time items puts to recyclerView without details (only name, id and image URL), items
*       like that have red cross in recycler view, red cross mean that details not yet downloaded.
*
*       When details downloads, red cross is replaced by a green check mark, it mean that item
*       details is loaded and this item can be compared with another items with green check mark.
*       Green check mark - items in "index" (like git)
* */
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
            rcItemName.text = pokemonPreview.pokemonName
            rcItemImage.load(pokemonPreview.imageUrl)
            rcItemId.text = "id: ${pokemonPreview.id}"

            /*
            *   If item has true in loadedFullInfo, it'll has green check mark in recyclerView
            *   otherwise it'll has red cross in recyclerView
            * */
            if (pokemonPreview.loadedFullInfo) {
                rcItemLoadStatus.setImageResource(R.drawable.ic_done)
                rcItemLoadStatus.setColorFilter(Color.GREEN)
            } else {
                rcItemLoadStatus.setImageResource(R.drawable.ic_fail)
                rcItemLoadStatus.setColorFilter(Color.RED)
            }

            /*
            *   When item is sorted by selected specs, best item (one or few) will has yellow stroke
            * */
            if (pokemonPreview.isBest) {
                rcItemCardView.strokeColor = Color.YELLOW
                rcItemCardView.strokeWidth = 2
            } else {
                rcItemCardView.strokeColor = Color.TRANSPARENT
                rcItemCardView.strokeWidth = 0
            }

            /*
            *   Every item has onClick reaction, that use navigation to details fragment by id
            * */
            root.setOnClickListener {
                val action = PokemonListFragmentDirections.actionToDetails(pokemonPreview.id ?: 0)
                itemView.findNavController().navigate(action)
            }
        }
    }
}

/*
*   recyclerView callbacks for good animation behavior and other recyclerView features
* */
private object PokemonDiffItemCallbacks: DiffUtil.ItemCallback<PokemonPreview>() {
    override fun areItemsTheSame(oldItem: PokemonPreview, newItem: PokemonPreview): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: PokemonPreview, newItem: PokemonPreview): Boolean {
        return oldItem.pokemonName == oldItem.pokemonName
    }
}