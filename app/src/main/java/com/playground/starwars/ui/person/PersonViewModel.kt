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

@ExperimentalStdlibApi
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
                        is Result.Error -> flowOf(State(isLoading = false, isError = true))
                        is Result.Success -> personDetailsFlow(personResult.data)
                    }
                }
                .onStart { emit(State(isLoading = true)) }
        }.broadcastIn(viewModelScope)
            .asFlow()

    private fun personDetailsFlow(person: Person): Flow<State> =
        combine(
            starWars.getPlanet(person.planetId),
            filmsFlow(person)
        ) { planetResult, films ->
            when (planetResult) {
                is Result.Error -> State(person, null, films, isError = true)
                is Result.Success -> State(person, planetResult.data, films)
            }
        }.onStart { emit(State(person = person, isLoading = true)) }

    @ExperimentalStdlibApi
    private fun filmsFlow(person: Person): Flow<String> =
        person.filmIds
            .asFlow()
            .flatMapMerge { filmId ->
                starWars.getFilm(filmId)
                    .map { filmResult ->
                        val result: Pair<Int, String> = when (filmResult) {
                            is Result.Success -> filmId to filmResult.data.toFilmString()
                            is Result.Error -> filmId to "Error loading Film ID: $filmId"
                        }
                        result
                    }
                    .onStart { emit(filmId to "\nLoading...") }
            }
            .scan(mutableMapOf<Int, String>()) { acc, it ->
                acc[it.first] = it.second
                acc
            }
            .map { entry ->
                entry.map { it.value }
                    .scanReduce { acc, s ->
                        "$acc, $s"
                    }
                    .lastOrNull()
            }
            .mapNotNull { it }
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