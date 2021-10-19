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
    @Singleton
    @Provides
    fun providePokemonRoomDatabase(
        baseApplication: BaseApplication
    ): PokemonAppDatabase {
        return PokemonAppDatabase.create(baseApplication)
    }
}