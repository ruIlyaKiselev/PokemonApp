package com.example.pokemonapp.di

import com.example.pokemonapp.network.PokeApiContract
import com.example.pokemonapp.network.PokeApiPageSource
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

    @Singleton
    @Provides
    fun providePokeApiPageSource(
        pokeApiService: PokeApiService
    ): PokeApiPageSource {
        return PokeApiPageSource(
            pokeApiService, 1
        )
    }
}