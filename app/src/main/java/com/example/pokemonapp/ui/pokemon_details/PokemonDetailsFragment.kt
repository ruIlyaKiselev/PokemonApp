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

import com.google.android.material.chip.Chip

import com.google.android.material.chip.ChipGroup

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

            pokemon.type.forEach { nullableType ->
                nullableType?.let { type -> addChip(type, pokemonDetailsChipgroup) }
            }

            pokemonDetailsAttackProgressBar.max = viewModel.getMaxPokemonAttack()
            pokemonDetailsAttackProgressBar.progress = pokemon.stats?.attack ?: 0
            pokemonDetailsAttackTextview.text = pokemon.stats?.attack.toString()

            pokemonDetailsDefenceProgressBar.max = viewModel.getMaxPokemonDefence()
            pokemonDetailsDefenceProgressBar.progress = pokemon.stats?.defence ?: 0
            pokemonDetailsDefenceTextview.text = pokemon.stats?.defence.toString()

            pokemonDetailsHpProgressBar.max = viewModel.getMaxPokemonHp()
            pokemonDetailsHpProgressBar.progress = pokemon.stats?.hp ?: 0
            pokemonDetailsHpTextview.text = pokemon.stats?.hp.toString()
        }
    }

    private fun addChip(pItem: String, pChipGroup: ChipGroup) {
        val lChip = Chip(requireContext())
        lChip.text = pItem
        pChipGroup.addView(lChip, pChipGroup.childCount - 1)
    }
}