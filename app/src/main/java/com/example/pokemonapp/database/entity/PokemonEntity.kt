package com.example.pokemonapp.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.pokemonapp.database.RoomContract

@Entity(
    tableName = RoomContract.Pokemon.TABLE_NAME,
    indices = [Index(RoomContract.Pokemon.COLUMN_NAME_ID)]
)
data class PokemonEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_ID)
    val id: Int = 0,
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_POKEMON_NAME)
    val pokemonName: String = "",
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_IMAGE_URL)
    val imageUrl: String = "",
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_HEIGHT)
    val height: Int = 0,
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_WEIGHT)
    val weight: Int = 0,
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_TYPE)
    val type: List<String?> = listOf(),
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_STATS)
    val stats: StatsEntity
)