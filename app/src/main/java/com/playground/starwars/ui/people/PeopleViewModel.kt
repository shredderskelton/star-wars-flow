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
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scanReduce

@ExperimentalCoroutinesApi
@FlowPreview
class PeopleViewModel(
    application: Application,
    starWars: StarWarsService,
    dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) : StarWarsViewModel(application, dispatcherProvider) {

    val people =
        starWars.getPeople()
            .filterIsInstance<Result.Success<List<Person>>>() // not handling errors!
            .map { result ->
                result.data
                    .sortedBy { it.name }
                    .map { person ->
                        SimpleListItem(
                            person.id,
                            person.name,
                            person.gender
                        )
                    }
            }
            .scanReduce { accumulator, value -> (accumulator + value).sortedBy { it.id } }
}
