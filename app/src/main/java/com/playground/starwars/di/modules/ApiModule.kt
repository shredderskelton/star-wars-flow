package com.playground.starwars.di.modules

import android.content.Context
import android.net.wifi.WifiManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.playground.starwars.service.HttpStarWarsService
import com.playground.starwars.service.StarWarsService
import com.playground.starwars.service.api.FakeStarWarsApi
import com.playground.starwars.service.api.StarWarsApi
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit

private const val useNetwork = false

val apiModule = module {
    factory { androidContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager }
    single {
        val cacheSize = 10 * 1024 * 1024
        Cache(androidContext().cacheDir, cacheSize.toLong())
    }
    single {
        val client = OkHttpClient.Builder()//.cache(get())
        client.addInterceptor {
            it.proceed(
                it.request().newBuilder().addHeader("Content-Type", "application/json").build()
            )
        }
        // For debugging traffic
//        if (BuildConfig.DEBUG) {
//            val logging = HttpLoggingInterceptor()
//            logging.level = HttpLoggingInterceptor.Level.BODY
//            client.addInterceptor(logging)
//        }
        client.build()
    }
    single {
        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl("https://swapi.dev")
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(get())
            .build()
    }

    single<StarWarsApi> {
        if (useNetwork) get<Retrofit>().create(StarWarsApi::class.java)
        else FakeStarWarsApi()
    }
    single<StarWarsService> { HttpStarWarsService(get()) }
}

/**
 * Kotlin serializer
 */
private val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    isLenient = true
}