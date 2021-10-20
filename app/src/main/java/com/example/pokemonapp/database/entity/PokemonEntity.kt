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
    val _id: Int,
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_POKEMON_ID)
    val pokemonId: Int = 0,
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_POKEMON_NAME)
    val pokemonName: String = "",
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_IMAGE_URL)
    val imageUrl: String = "",
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_HEIGHT)
    val height: Int? = 0,
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_WEIGHT)
    val weight: Int? = 0,
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_TYPE)
    val type: List<String?>? = listOf(),
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_STATS)
    val stats: StatsEntity?
) {
    constructor(
        pokemonId: Int,
        pokemonName: String,
        imageUrl: String,
        height: Int?,
        weight: Int?,
        type: List<String?>,
        stats: StatsEntity?
    ) : this(
        _id = 0,
        pokemonId = pokemonId,
        pokemonName = pokemonName,
        imageUrl = imageUrl,
        height = height,
        weight = weight,
        type = type,
        stats = stats
    )
}