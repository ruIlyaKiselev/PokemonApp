package com.example.pokemonapp.ui.pokemon_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.pokemonapp.R
import com.example.pokemonapp.databinding.FragmentPokemonDetailsBinding
import com.example.pokemonapp.domain.Pokemon
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PokemonDetailsFragment : Fragment() {

    private val viewModel: PokemonDetailsViewModel by viewModels()
    private lateinit var binding: FragmentPokemonDetailsBinding

    private val args: PokemonDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_pokemon_details,
            container,
            false
        )

        binding.lifecycleOwner = this

        val view: View = binding.root

        viewModel.loadPokemonDetails(args.pokemonId)

        viewModel.pokemonDetails.observe(viewLifecycleOwner) {
            bind(it)
        }

        return view;
    }

    private fun bind(pokemon: Pokemon) {
        binding.apply {
            pokemonDetailsImageView.load(pokemon.imageUrl)
            pokemonDetailsName.text = pokemon.pokemonName
            pokemonDetailsHeight.text = "Height: ${pokemon.height}"
            pokemonDetailsWeight.text = "Weight: ${pokemon.weight}"
        }
    }
}