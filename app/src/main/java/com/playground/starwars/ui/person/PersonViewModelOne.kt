package com.playground.starwars.ui.person

import androidx.lifecycle.viewModelScope
import com.playground.starwars.model.Person
import com.playground.starwars.model.bmi
import com.playground.starwars.datasource.Result
import com.playground.starwars.datasource.StarWarsDataSource
import com.playground.starwars.ui.share
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull


class PersonViewModelOne(
    private val starWarsDataSource: StarWarsDataSource,
    private val personId: Int,
) : PersonViewModel() {

    // Task 1
    private val person: Flow<Person> =
        starWarsDataSource.getPerson(personId)
            .mapNotNull { personResult ->
                when (personResult) {
                    is Result.Success -> personResult.data
                    is Result.Error -> null
                }
            }
            .share(viewModelScope)

    override val name: Flow<String> = person.map { it.name }
    override val height: Flow<String> = person.map { "Height: ${it.height}" }
    override val birthday: Flow<String> = person.map { "DoB: ${it.birth_year}" }

    override val bmi: Flow<String> = person.map { "BMI: ${it.bmi}" }

    // Future tasks

    override val isLoadingVisible: Flow<Boolean> = flowOf(false)
    override val isErrorVisible: Flow<Boolean> = flowOf(false)
    override val isDataContainerVisible: Flow<Boolean> = flowOf(true)
    override val planetName: Flow<String> = flowOf("")
    override val planetTerrain: Flow<String> = flowOf("")
    override val planetClimate: Flow<String> = flowOf("")
    override val planetGravity: Flow<String> = flowOf("")
    override val planetPopulation: Flow<String> = flowOf("")
    override val films: Flow<String> = flowOf("")
    override fun refresh() = Unit
}