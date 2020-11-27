package com.playground.starwars.ui.person

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow

abstract class PersonViewModel : ViewModel() {
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