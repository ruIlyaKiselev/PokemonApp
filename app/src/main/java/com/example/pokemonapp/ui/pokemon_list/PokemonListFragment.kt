package com.example.pokemonapp.ui.pokemon_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pokemonapp.R
import com.example.pokemonapp.databinding.FragmentPokemonListBinding
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.domain.getStatsSum
import com.example.pokemonapp.ui.pokemon_recycler_view.PokemonLoaderStateAdapter
import com.example.pokemonapp.ui.pokemon_recycler_view.PokemonPagingDataAdapter
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.TimeUnit

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
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        loadDataFromPageSource()

        binding.floatingActionButton.setOnClickListener {
            viewModel.resetPokemonPagerRandomly()
            pagingAdapter.snapshot()
        }

        configurePokemonDetailsLoadingToRecyclerView()
        configureCheckBoxes()

        return view;
    }

    private fun configurePokemonDetailsLoadingToRecyclerView() {
        viewModel.pokemonsObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
            { emittedValue ->
                val associatedValueIndex = pagingAdapter.snapshot().indexOfFirst {
                    it?.id == emittedValue.id
                }

                pagingAdapter.snapshot()[associatedValueIndex]?.loadedFullInfo = true
                pagingAdapter.notifyItemChanged(associatedValueIndex)
            },
            {
                Log.e("MyLog", it.toString())
            }
        )
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
        val byAttack = binding.mainScreenAttackCheckBox.isChecked
        val byDefence = binding.mainScreenDefenceCheckBox.isChecked
        val byHp = binding.mainScreenHpCheckBox.isChecked

        var loadedPokemonList: List<Pokemon>


        binding.apply {
            loadedPokemonList = viewModel.sortPokemons(byAttack, byDefence, byHp)
        }

        val countOfBest = loadedPokemonList.count {
            it.getStatsSum(byAttack, byDefence, byHp) ==
                    loadedPokemonList[0].getStatsSum(byAttack, byDefence, byHp) &&
                    loadedPokemonList[0].getStatsSum(byAttack, byDefence, byHp) != 0
        } - 1

        var currentListIndex = 0
        loadedPokemonList.forEachIndexed { i: Int, pokemon: Pokemon ->
            if (pagingAdapter.snapshot().size > currentListIndex) {
                pagingAdapter.snapshot()[currentListIndex]?.apply {
                    id = loadedPokemonList[i].id
                    pokemonName = loadedPokemonList[i].pokemonName
                    imageUrl = loadedPokemonList[i].imageUrl
                    isBest = false
                }
                currentListIndex++
            }
        }

        for (i in 0..countOfBest) {
            pagingAdapter.snapshot()[i]?.isBest = true
        }

        pagingAdapter.notifyDataSetChanged()
        binding.pokemonListRecyclerView.smoothScrollToPosition(0)
        pagingAdapter.notifyDataSetChanged()
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