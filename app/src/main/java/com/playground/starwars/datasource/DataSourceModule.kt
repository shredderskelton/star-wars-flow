package com.playground.starwars.datasource

import org.koin.dsl.module


val dataSourceModule = module {
    single<StarWarsDataSource> { StarWarsDataSourceImpl(get()) }
}