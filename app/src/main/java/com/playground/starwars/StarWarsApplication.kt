package com.playground.starwars

import android.app.Application
import com.playground.starwars.api.apiModule
import com.playground.starwars.datasource.dataSourceModule
import com.playground.starwars.di.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class StarWarsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StarWarsApplication)
            modules(listOf(viewModelModule, dataSourceModule, apiModule))
        }
    }
}