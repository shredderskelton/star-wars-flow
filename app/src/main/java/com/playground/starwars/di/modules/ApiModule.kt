package com.playground.starwars.di.modules

import android.content.Context
import android.net.wifi.WifiManager
import com.playground.starwars.service.HttpStarWarsService
import com.playground.starwars.service.api.FakeStarWarsApi
import com.playground.starwars.service.StarWarsService
import com.playground.starwars.service.api.StarWarsApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

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
        Retrofit.Builder()
            .baseUrl("https://swapi.dev")
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            .client(get())
            .build()
    }

    single<StarWarsApi> { get<Retrofit>().create(StarWarsApi::class.java) }
//    single<StarWarsApi> { FakeStarWarsApi() }
    single<StarWarsService> { HttpStarWarsService(get()) }
}