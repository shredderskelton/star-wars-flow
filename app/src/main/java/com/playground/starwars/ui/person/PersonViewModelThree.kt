package com.playground.starwars.ui.person

import androidx.lifecycle.viewModelScope
import com.playground.starwars.model.*
import com.playground.starwars.service.Result
import com.playground.starwars.service.StarWarsService
import com.playground.starwars.ui.CoroutineViewModel
import com.playground.starwars.ui.DefaultDispatcherProvider
import com.playground.starwars.ui.DispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@FlowPreview
@ExperimentalStdlibApi
class PersonViewModelThree(
    private val starWars: StarWarsService,
    personId: Int,
) : PersonViewModel() {

    private val trigger = MutableSharedFlow<Long>(1)

    // Task 3
    private val state = trigger
        .flatMapLatest {
            starWars.getPerson(personId)
                .flatMapConcat { personResult ->
                    when (personResult) {
                        is Result.Success ->
                            personDetailsFlow(personResult.data)
                        is Result.Error ->
                            flowOf(State(isLoading = false, isError = true))
                    }
                }
                .onStart { emit(State(isLoading = true)) }
        }
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0, 0),
            replay = 1
        )

    private fun personDetailsFlow(person: Person): Flow<State> =
        combine(
            starWars.getPlanet(person.planetId),
            filmsFlow_scan(person)
        ) { planetResult, films ->
            when (planetResult) {
                is Result.Success ->
                    State(person = person, planet = planetResult.data, films = films)
                is Result.Error ->
                    State(person = person, films = films, isError = true)
            }
        }.onStart {
            emit(State(person = person, isLoading = true))
        }

    private fun filmsFlow(person: Person): Flow<String> =
        person.filmIds.asFlow()
            .flatMapMerge { filmId ->
                starWars.getFilm(filmId)
                    .map { filmResult ->
                        when (filmResult) {
                            is Result.Success -> filmResult.data.toFilmString()
                            is Result.Error -> null
                        }
                    }
            }
            .mapNotNull { it }
            .runningReduce { acc, it -> "$acc, $it" }
            .onStart { emit("Loading") }

    // Task 3 Bonus Time

    @ExperimentalStdlibApi
    private fun filmsFlow_scan(person: Person): Flow<String> =
        person.filmIds
            .asFlow()
            .flatMapMerge { filmId ->
                starWars.getFilm(filmId)
                    .map { filmResult ->
                        val result: Pair<Int, String> = when (filmResult) {
                            is Result.Success -> filmId to filmResult.data.toFilmString()
                            is Result.Error -> filmId to "\nError loading Film ID: $filmId"
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
                    .runningReduce { acc, s -> "$acc, $s" }
                    .lastOrNull()
            }
            .mapNotNull { it }
            .onStart { emit("Loading") }

    override fun refresh() {
        trigger.tryEmit(System.currentTimeMillis())
    }


    private val person: Flow<Person> = state.mapNotNull { it.person }
    private val planet: Flow<Planet> = state.mapNotNull { it.planet }

    override val isLoadingVisible: Flow<Boolean> = state.map { it.isLoading }
    override val isErrorVisible: Flow<Boolean> = state.map { it.isError }
    override val isDataContainerVisible: Flow<Boolean> =
        state.map { !it.isError && it.person != null }
    override val name: Flow<String> = person.map { it.name }
    override val height: Flow<String> = person.map { "Height: ${it.height}" }
    override val birthday: Flow<String> = person.map { "DoB: ${it.birth_year}" }
    override val bmi: Flow<String> = person.map { "BMI: ${it.bmi}" }
    override val planetName: Flow<String> = planet.map { "Planet of Birth: ${it.name}" }
    override val planetTerrain: Flow<String> = planet.map { "Terrain: ${it.terrain}" }
    override val planetClimate: Flow<String> = planet.map { "Climate: ${it.climate}" }
    override val planetGravity: Flow<String> = planet.map { "Gravity: ${it.gravity}" }
    override val planetPopulation: Flow<String> = planet.map { "Population: ${it.population}" }
    override val films: Flow<String> = state.map { "Films: ${it.films}" }
}

private data class State(
    val person: Person? = null,
    val planet: Planet? = null,
    val films: String = "",
    val isError: Boolean = false,
    val isLoading: Boolean = false
)