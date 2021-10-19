package com.example.pokemonapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pokemonapp.database.entity.PokemonEntity

@Dao
interface PokemonAppDao {
    @Query("SELECT * FROM Pokemon ORDER BY _id ASC")
    suspend fun getAllPokemons(): List<PokemonEntity>
    @Query("SELECT * FROM Pokemon WHERE _id == :id")
    suspend fun getPokemonById(id: Int): PokemonEntity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: PokemonEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemons(pokemons: List<PokemonEntity>)
    @Query("DELETE FROM Pokemon WHERE _id == :id")
    suspend fun deletePokemon(id: Int)
    @Query("DELETE FROM Pokemon")
    suspend fun deleteAllPokemons()
}