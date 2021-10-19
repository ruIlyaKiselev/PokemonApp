package com.example.pokemonapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.io.IOException

@HiltAndroidApp
class BaseApplication: Application() {

    @Throws(InterruptedException::class, IOException::class)
    fun isConnected(): Boolean {
        val command = "ping -c 1 pokeapi.co"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }
}