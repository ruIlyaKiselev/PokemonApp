package com.example.pokemonapp.di

import com.example.pokemonapp.BaseApplication
import com.example.pokemonapp.database.PokemonAppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {


    /*
    * Provides room database with stored pokemons;
    * If we have not loaded pokemons from api before, they will not be in database
    * and recyclerView may  works not very well (show pokemon duplicates).
    * */
    @Singleton
    @Provides
    fun providePokemonRoomDatabase(
        baseApplication: BaseApplication
    ): PokemonAppDatabase {
        return PokemonAppDatabase.create(baseApplication)
    }
}