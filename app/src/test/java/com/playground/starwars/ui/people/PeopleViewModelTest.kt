package com.playground.starwars.ui.people

import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.mock
import com.playground.starwars.api.dummyPerson
import com.playground.starwars.datasource.Result
import com.playground.starwars.datasource.StarWarsDataSource
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime


@ExperimentalTime
class PeopleViewModelTest {

    private lateinit var underTest: PeopleViewModel

    private val service = mock<StarWarsDataSource> {
        onBlocking { getPeople() }
            .then {
                flow {
                    // Simulate multiple requests to backend
                    emit(Result.Success(listOf(dummyPerson)))
                    emit(Result.Success(listOf(dummyPerson2)))
                }
            }
    }

    @Before
    fun setup() {
        underTest = PeopleViewModel(service)
    }

    @Test
    fun `People items flow into screen in the correct order`() {
        runBlocking {
            underTest.people.test {
                assertEquals(listOf(lukeItem), expectItem())
                assertEquals(listOf(lukeItem, nickItem), expectItem())
                expectComplete()
            }
        }
    }

}

private val lukeItem = SimpleListItem(
    id = 1,
    title = "Luke SkyWalker",
    subtitle = "male"
)

private val nickItem = SimpleListItem(
    id = 2,
    title = "Nick Skelton",
    subtitle = "male"
)

private val dummyPerson2 = dummyPerson.copy(
    name = "Nick Skelton",
    url = "http://swapi.dev/api/people/2/",
)
