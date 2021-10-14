package com.example.pokemonapp.ui.pokemon_list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokemonapp.R
import com.example.pokemonapp.databinding.FragmentPokemonListBinding
import com.example.pokemonapp.ui.pokemon_recycler_view.PokemonPagingDataAdapter
import com.example.pokemonapp.ui.pokemon_recycler_view.PokemonRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonListFragment : Fragment() {

    private val viewModel: PokemonsListVIewModel by viewModels()
    private lateinit var binding: FragmentPokemonListBinding
//    private lateinit var pokemonRecyclerViewAdapter: PokemonRecyclerViewAdapter

    private val pagingAdapter by lazy ( LazyThreadSafetyMode.NONE ) {
        PokemonPagingDataAdapter(requireContext())
    }

    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_pokemon_list,
            container,
            false
        )

        binding.lifecycleOwner = this

        val view: View = binding.root

        val pokemonRecyclerViewLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.pokemonListRecyclerView.layoutManager = pokemonRecyclerViewLayoutManager
        binding.pokemonListRecyclerView.adapter = pagingAdapter

        uiScope.launch {
            viewModel.pokemons.collectLatest {
                pagingAdapter.submitData(it)
            }
        }

        return view;
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}