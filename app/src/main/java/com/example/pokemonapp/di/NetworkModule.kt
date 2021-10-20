package com.example.pokemonapp.di

import com.example.pokemonapp.database.PokemonAppDatabase
import com.example.pokemonapp.network.PokeApiContract
import com.example.pokemonapp.repository.PokeApiPageSource
import com.example.pokemonapp.network.PokeApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /*
    * Provides retrofit service with log interceptor, timeouts
    * */
    @Singleton
    @Provides
    fun providePokeApiService(): PokeApiService {

        val loggingInterceptor by lazy {
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        val httpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build()
        }

        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(PokeApiContract.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApiService::class.java)
    }

    /*
    * Provides page source (jetpack paging3) for pagination;
    * PokemonListViewModel use information from this, transforms it to state flow and gives
    * that flow to RecyclerView in PokemonListFragment
    * */
    @Singleton
    @Provides
    fun providePokeApiPageSource(
        pokeApiService: PokeApiService,
        database: PokemonAppDatabase
    ): PokeApiPageSource {
        return PokeApiPageSource(
            pokeApiService, 1, database
        )
    }
}