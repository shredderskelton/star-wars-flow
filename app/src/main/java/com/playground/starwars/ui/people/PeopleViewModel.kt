package com.playground.starwars.ui.people

import android.app.Application
import com.playground.starwars.service.Result
import com.playground.starwars.service.StarWarsService
import com.playground.starwars.model.Person
import com.playground.starwars.model.id
import com.playground.starwars.ui.DefaultDispatcherProvider
import com.playground.starwars.ui.DispatcherProvider
import com.playground.starwars.ui.StarWarsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*

class PeopleViewModel(
    starWars: StarWarsService,
    dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) : StarWarsViewModel(dispatcherProvider) {

    private val isLoadingRelay = BroadcastChannel<Boolean>(1)

    val people =
        starWars.getPeople()
            .filterIsInstance<Result.Success<List<Person>>>() // not handling errors!
            .map { result ->
                result.data
                    .map { person ->
                        SimpleListItem(
                            id = person.id,
                            title = person.name,
                            subtitle = person.gender
                        )
                    }
            }
            .scanReduce { accumulator, value -> (accumulator + value) }
            .onStart { isLoadingRelay.send(true) }
            .onCompletion { isLoadingRelay.send(false) }

    val isLoading = isLoadingRelay.asFlow()
}
