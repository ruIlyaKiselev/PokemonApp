package com.example.pokemonapp.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.pokemonapp.database.RoomContract

@Entity(
    tableName = RoomContract.Stats.TABLE_NAME,
    indices = [Index(RoomContract.Stats.COLUMN_NAME_ID)]
)
data class StatsEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = RoomContract.Stats.COLUMN_NAME_ID)
    val id: Int = 0,
    @ColumnInfo(name = RoomContract.Stats.COLUMN_NAME_HP)
    val hp: Int = 0,
    @ColumnInfo(name = RoomContract.Stats.COLUMN_NAME_ATTACK)
    val attack: Int = 0,
    @ColumnInfo(name = RoomContract.Stats.COLUMN_NAME_DEFENCE)
    val defence: Int = 0,
    @ColumnInfo(name = RoomContract.Stats.COLUMN_NAME_SPECIAL_ATTACK)
    val specialAttack: Int = 0,
    @ColumnInfo(name = RoomContract.Stats.COLUMN_NAME_SPECIAL_DEFENCE)
    val specialDefence: Int = 0,
    @ColumnInfo(name = RoomContract.Stats.COLUMN_NAME_SPEED)
    val speed: Int = 0,
)