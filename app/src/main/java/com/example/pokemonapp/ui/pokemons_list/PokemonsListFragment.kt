package com.example.pokemonapp.ui.pokemons_list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.pokemonapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PokemonsListFragment : Fragment() {

    private val viewModel: PokemonsListVIewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel.pokemonList.observe(viewLifecycleOwner) {
            Log.d("MyLog", it.toString())
            it.forEach {
                viewModel.loadPokemonDetails(it.id ?: 0)
            }
        }

        viewModel.pokemonDetails.observe(viewLifecycleOwner) {
            Log.d("MyLog", it.toString())
        }

        viewModel.loadPokemonList()

        return inflater.inflate(R.layout.fragment_pokemons_list, container, false)
    }

}