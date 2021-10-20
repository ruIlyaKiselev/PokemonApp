package com.example.pokemonapp.database

import androidx.room.TypeConverter
import com.example.pokemonapp.database.entity.StatsEntity
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun listToJson(value: List<String?>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String?) = Gson().fromJson(value, Array<String>::class.java).toList()

    @TypeConverter
    fun statsToJson(value: StatsEntity?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToStats(value: String?) = Gson().fromJson(value, StatsEntity::class.java)
}