package com.playground.starwars.ui.person

import androidx.lifecycle.viewModelScope
import com.playground.starwars.model.Person
import com.playground.starwars.model.Planet
import com.playground.starwars.model.bmi
import com.playground.starwars.model.planetId
import com.playground.starwars.service.Result
import com.playground.starwars.service.StarWarsService
import com.playground.starwars.ui.CoroutineViewModel
import com.playground.starwars.ui.DefaultDispatcherProvider
import com.playground.starwars.ui.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class PersonViewModelTwo(
    private val starWars: StarWarsService,
    personId: Int,
) : PersonViewModel() {

    //Task 2

    private data class State(
        val person: Person? = null,
        val planet: Planet? = null
    )

    private val state: Flow<State> =
        starWars.getPerson(personId)
            .flatMapConcat { personResult ->
                when (personResult) {
                    is Result.Success -> getPlanet(personResult.data)
                    is Result.Error -> flowOf(State())
                }
            }
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(0, 0),
                replay = 1
            )

    private fun getPlanet(person: Person): Flow<State> =
        starWars.getPlanet(person.planetId).map { planetResult ->
            when (planetResult) {
                is Result.Success -> State(person, planetResult.data)
                is Result.Error -> State(person, null)
            }
        }


    private val person: Flow<Person> = state.mapNotNull { it.person }

    override val name: Flow<String> = person.map { it.name }
    override val height: Flow<String> = person.map { "Height: ${it.height}" }
    override val birthday: Flow<String> = person.map { "DoB: ${it.birth_year}" }
    override val bmi: Flow<String> = person.map { "BMI: ${it.bmi}" }

    private val planet: Flow<Planet> = state.mapNotNull { it.planet }

    override val planetName: Flow<String> = planet.map { "Planet of Birth: ${it.name}" }
    override val planetTerrain: Flow<String> = planet.map { "Terrain: ${it.terrain}" }
    override val planetClimate: Flow<String> = planet.map { "Climate: ${it.climate}" }
    override val planetGravity: Flow<String> = planet.map { "Gravity: ${it.gravity}" }
    override val planetPopulation: Flow<String> = planet.map { "Population: ${it.population}" }


    // TODO in next task(s)
    override val films: Flow<String> = flowOf("")
    override fun refresh() = Unit
    override val isLoadingVisible: Flow<Boolean> = flowOf(false)
    override val isErrorVisible: Flow<Boolean> = flowOf(false)
    override val isDataContainerVisible: Flow<Boolean> = flowOf(true)


}