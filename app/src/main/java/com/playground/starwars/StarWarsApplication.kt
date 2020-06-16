package com.playground.starwars

import android.app.Application
import com.playground.starwars.di.modules.apiModule
import com.playground.starwars.di.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@ExperimentalStdlibApi
class StarWarsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StarWarsApplication)
            modules(listOf(viewModelModule, apiModule))
        }
    }
}