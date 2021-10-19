package com.example.pokemonapp.di

import com.example.pokemonapp.database.PokemonAppDatabase
import com.example.pokemonapp.network.PokeApiService
import com.example.pokemonapp.repository.PokemonRepository
import com.example.pokemonapp.repository.PokemonRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun providePokemonRepository(
        pokeApiService: PokeApiService,
        pokemonAppDatabase: PokemonAppDatabase
    ): PokemonRepository {
        return PokemonRepositoryImpl(pokeApiService, pokemonAppDatabase)
    }
}