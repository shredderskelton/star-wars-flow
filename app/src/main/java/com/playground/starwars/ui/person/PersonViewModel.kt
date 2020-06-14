package com.playground.starwars.ui.person

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.playground.starwars.service.Result
import com.playground.starwars.service.StarWarsService
import com.playground.starwars.model.*
import com.playground.starwars.ui.DefaultDispatcherProvider
import com.playground.starwars.ui.DispatcherProvider
import com.playground.starwars.ui.StarWarsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class PersonViewModel(
    application: Application,
    private val starWars: StarWarsService,
    personId: Int,
    dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) : StarWarsViewModel(application, dispatcherProvider) {

    private val triggerRelay = BroadcastChannel<Long>(1)
    private val trigger = triggerRelay.asFlow()

    private val state =
        trigger.flatMapLatest {
            starWars.getPerson(personId)
                .flatMapConcat { personResult ->
                    when (personResult) {
                        Result.Loading -> flowOf(State(isLoading = true))
                        is Result.Error -> flowOf(State(isLoading = false, isError = true))
                        is Result.Success -> getDataFor(personResult.data)
                    }
                }
        }
            .broadcastIn(viewModelScope)
            .asFlow()

    private fun getDataFor(person: Person): Flow<State> =
        combine(
            starWars.getPlanet(person.planetId),
            createFilm(person)
        ) { planetResult, films ->
            when (planetResult) {
                Result.Loading -> State(person, null, films, isLoading = true)
                is Result.Error -> State(person, null, films, isError = true)
                is Result.Success -> State(person, planetResult.data, films)
            }
        }

    private fun createFilm(person: Person): Flow<String> =
        person.filmIds.asFlow()
            .flatMapConcat { filmId ->
                starWars.getFilm(filmId)
                    .map { filmResult ->
                        when (filmResult) {
                            is Result.Success -> filmResult.data.toFilmString()
                            is Result.Error -> "Error loading Film ID: $filmId"
                            Result.Loading -> null
                        }
                    }
                    .mapNotNull { it }
            }
            .scanReduce { accumulator, value -> "$accumulator, $value" }
            .onStart { emit("Loading") }

    fun refresh() = launchCoroutine {
        triggerRelay.send(System.currentTimeMillis())
    }

    private val person: Flow<Person> = state.mapNotNull { it.person }
    private val planet: Flow<Planet> = state.mapNotNull { it.planet }

    val isLoadingVisible: Flow<Boolean> = state.map { it.isLoading }
    val isErrorVisible: Flow<Boolean> = state.map { it.isError }
    val isDataContainerVisible: Flow<Boolean> = state.map { !it.isError && it.person != null }

    val name: Flow<String> = person.map { it.name }
    val height: Flow<String> = person.map { "Height: ${it.height}" }
    val birthday: Flow<String> = person.map { "DoB: ${it.birth_year}" }
    val bmi: Flow<String> = person.map { "BMI: ${it.bmi}" }
    val planetName: Flow<String> = planet.map { "Planet of Birth: ${it.name}" }
    val planetTerrain: Flow<String> = planet.map { "Terrain: ${it.terrain}" }
    val planetClimate: Flow<String> = planet.map { "Climate: ${it.climate}" }
    val planetGravity: Flow<String> = planet.map { "Gravity: ${it.gravity}" }
    val planetPopulation: Flow<String> = planet.map { "Population: ${it.population}" }
    val films: Flow<String> = state.map { "Films: ${it.films}" }
}

private data class State(
    val person: Person? = null,
    val planet: Planet? = null,
    val films: String = "",
    val isError: Boolean = false,
    val isLoading: Boolean = false
)

private fun Film.toFilmString() = "\n${title} (${release_date.substring(IntRange(0, 3))})"