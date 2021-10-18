package com.example.pokemonapp.ui.pokemon_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokemonapp.R
import com.example.pokemonapp.databinding.FragmentPokemonListBinding
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.ui.pokemon_recycler_view.PokemonLoaderStateAdapter
import com.example.pokemonapp.ui.pokemon_recycler_view.PokemonPagingDataAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PokemonListFragment : Fragment() {

    private val viewModel: PokemonListViewModel by viewModels()
    private lateinit var binding: FragmentPokemonListBinding

    private val pagingAdapter by lazy ( LazyThreadSafetyMode.NONE ) {
        PokemonPagingDataAdapter(requireContext())
    }

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main)

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

        val pokemonRecyclerViewLayoutManager = GridLayoutManager(
            requireContext(),
            2,
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.pokemonListRecyclerView.layoutManager = pokemonRecyclerViewLayoutManager
        binding.pokemonListRecyclerView.adapter = pagingAdapter.withLoadStateHeaderAndFooter(
            header = PokemonLoaderStateAdapter(),
            footer = PokemonLoaderStateAdapter()
        )

        pagingAdapter.addLoadStateListener { state: CombinedLoadStates ->
            if (state.refresh == LoadState.Loading) {
                binding.progressBar.visibility= View.VISIBLE
            } else {
                binding.progressBar.visibility= View.GONE
            }
        }

        loadDataFromPageSource()

        binding.floatingActionButton.setOnClickListener {
            viewModel.resetPokemonPagerRandomly()
            loadDataFromPageSource()
        }

        configurePokemonDetailsLoadingToRecyclerView()
        configureCheckBoxes()

        return view;
    }

    private fun configurePokemonDetailsLoadingToRecyclerView() {
        viewModel.storedPokemons.observe(viewLifecycleOwner) { updatedPokemons ->
            pagingAdapter.snapshot().forEach { preloadedPokemon ->
                val updatedPokemon = updatedPokemons.find {
                    it.id == preloadedPokemon?.id
                }

                preloadedPokemon?.height = updatedPokemon?.height
                preloadedPokemon?.weight = updatedPokemon?.weight
                preloadedPokemon?.stats = updatedPokemon?.stats
                preloadedPokemon?.type = updatedPokemon?.type ?: listOf()
            }

            pagingAdapter.notifyDataSetChanged()
        }
    }

    private fun configureCheckBoxes() {
        binding.apply {
            mainScreenAttackCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                sortPokemons()
            }

            mainScreenDefenceCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                sortPokemons()
            }

            mainScreenHpCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                sortPokemons()
            }
        }
    }

    private fun sortPokemons() {
        var loadedPokemonList: List<Pokemon>

        binding.apply {
            loadedPokemonList = viewModel.sortPokemons(
                byAttack = mainScreenAttackCheckBox.isChecked,
                byDefence = mainScreenDefenceCheckBox.isChecked,
                byHp = mainScreenHpCheckBox.isChecked,
            )
        }

        for (i in loadedPokemonList.indices) {
            pagingAdapter.snapshot()[i]?.apply {
                id = loadedPokemonList[i].id
                pokemonName = loadedPokemonList[i].pokemonName
                imageUrl = loadedPokemonList[i].imageUrl
                height = loadedPokemonList[i].height
                weight = loadedPokemonList[i].weight
                stats = loadedPokemonList[i].stats
                type = loadedPokemonList[i].type
            }
        }
    }

    private fun loadDataFromPageSource() {
        uiScope.launch {
            viewModel.pokemons.collectLatest {
                pagingAdapter.submitData(it)
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}