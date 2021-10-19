package com.example.pokemonapp.domain

data class Pokemon (
    var id: Int?,
    var pokemonName: String?,
    var imageUrl: String?
) {
    var height: Int? = null
    var weight: Int? = null
    var type: List<String?> = listOf()
    var stats: Stats? = null
}

fun Pokemon.getStatsSum(
    byAttack: Boolean,
    byDefence: Boolean,
    byHp: Boolean
): Int {
    var result = 0

    if (byAttack) {
        result += this.stats?.attack ?: 0
    }

    if (byDefence) {
        result += this.stats?.defence ?: 0
    }

    if (byHp) {
        result += this.stats?.hp ?: 0
    }

    return result
}