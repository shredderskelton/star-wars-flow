package com.playground.starwars.ui.people

import androidx.lifecycle.ViewModel
import com.playground.starwars.model.Person
import com.playground.starwars.model.id
import com.playground.starwars.service.Result
import com.playground.starwars.service.StarWarsService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class PeopleViewModel(starWars: StarWarsService, ) : ViewModel() {

    private val isLoadingRelay = MutableStateFlow(false)

    val people: Flow<List<SimpleListItem>> =
        starWars.getPeople()
            .filterIsInstance<Result.Success<List<Person>>>() // not handling errors!
            .map { result ->
                result.data.map { person -> person.toItem() }
            }
            .runningReduce { accumulator, value -> (accumulator + value) }
            .onStart { isLoadingRelay.value = true }
            .onCompletion { isLoadingRelay.value = false }

    val isLoading: Flow<Boolean> = isLoadingRelay
}

private fun Person.toItem(): SimpleListItem =
    SimpleListItem(
        id = this.id,
        title = this.name,
        subtitle = this.gender
    )
