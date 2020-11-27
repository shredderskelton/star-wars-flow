package com.playground.starwars.ui.person

import androidx.lifecycle.viewModelScope
import com.playground.starwars.model.Person
import com.playground.starwars.model.bmi
import com.playground.starwars.service.Result
import com.playground.starwars.service.StarWarsService
import com.playground.starwars.ui.CoroutineViewModel
import com.playground.starwars.ui.DefaultDispatcherProvider
import com.playground.starwars.ui.DispatcherProvider
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
class PersonViewModelOne(
    starWarsService: StarWarsService,
    personId: Int,
) : PersonViewModel() {

    // Task 1
    private val person: Flow<Person> =
        starWarsService.getPerson(personId)
            .mapNotNull { personResult ->
                when (personResult) {
                    is Result.Success -> personResult.data
                    is Result.Error -> null
                }
            }
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(0, 0),
                replay = 1
            )

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