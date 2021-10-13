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
import com.example.pokemonapp.ui.pokemon_recycler_view.PokemonRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PokemonListFragment : Fragment() {

    private val viewModel: PokemonsListVIewModel by viewModels()
    private lateinit var binding: FragmentPokemonListBinding
    private lateinit var pokemonRecyclerViewAdapter: PokemonRecyclerViewAdapter

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

        pokemonRecyclerViewAdapter = PokemonRecyclerViewAdapter()

        val pokemonRecyclerViewLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.pokemonListRecyclerView.layoutManager = pokemonRecyclerViewLayoutManager
        binding.pokemonListRecyclerView.adapter = pokemonRecyclerViewAdapter

        viewModel.pokemonList.observe(viewLifecycleOwner) {
            Log.d("MyLog", it.toString())
            pokemonRecyclerViewAdapter.itemsList = it
        }

        viewModel.loadPokemonList()

        return view;
    }

}