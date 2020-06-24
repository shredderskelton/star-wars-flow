package com.playground.starwars.ui.person

import com.playground.starwars.model.Film
import com.playground.starwars.model.Person
import com.playground.starwars.model.Planet
import com.playground.starwars.ui.DispatcherProvider
import com.playground.starwars.ui.StarWarsViewModel
import kotlinx.coroutines.flow.Flow

abstract class PersonViewModel(dispatcherProvider: DispatcherProvider) :
    StarWarsViewModel(dispatcherProvider) {
    abstract val isLoadingVisible: Flow<Boolean>
    abstract val isErrorVisible: Flow<Boolean>
    abstract val isDataContainerVisible: Flow<Boolean>

    abstract val name: Flow<String>
    abstract val height: Flow<String>
    abstract val birthday: Flow<String>

    abstract val bmi: Flow<String>

    abstract val planetName: Flow<String>
    abstract val planetTerrain: Flow<String>
    abstract val planetClimate: Flow<String>
    abstract val planetGravity: Flow<String>
    abstract val planetPopulation: Flow<String>
    abstract val films: Flow<String>
    abstract fun refresh()
}