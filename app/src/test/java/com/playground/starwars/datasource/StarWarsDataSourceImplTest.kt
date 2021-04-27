package com.playground.starwars.datasource

import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.mock
import com.playground.starwars.TestCoroutineRule
import com.playground.starwars.api.StarWarsApi
import com.playground.starwars.api.dummyPerson
import com.playground.starwars.model.PeopleResponse
import com.playground.starwars.model.Person
import com.playground.starwars.ui.SimulatorDummy
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class StarWarsDataSourceImplTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var underTest: StarWarsDataSource

    private fun setup(vararg result: Response<PeopleResponse>) {
        val api = mock<StarWarsApi>() {
            onBlocking { getPeople(1) }.then { result[0] }
            onBlocking { getPeople(2) }.then { result[1] }
            onBlocking { getPeople(3) }.then { result[2] }
            onBlocking { getPeople(4) }.then { result[3] }
        }

        underTest = StarWarsDataSourceImpl(
            api,
//            SimulatorDummy,
            dispatcherProvider = testCoroutineRule.provider
        )
    }

    @Test
    fun `when there are no results, we get an empty success result`() =
        testCoroutineRule.testDispatcher.runBlockingTest {
            // Given
            setup(Response.success(PeopleResponse(emptyList(), null, null, null)))

            // When
            underTest.getPeople().test {
                //Then
                assertEquals(Result.Success(emptyList<Person>()), expectItem())
                assertEquals(Result.Success(emptyList<Person>()), expectItem())
                expectComplete()
            }
        }

    @Test
    fun `when there are several pages, we get several success results`() =
        testCoroutineRule.testDispatcher.runBlockingTest {
            // Given
            setup(
                Response.success(PeopleResponse(listOf(dummyPerson), null, "something", null)),
                Response.success(PeopleResponse(listOf(dummyPerson2), null, null, null))
            )

            // When
            underTest.getPeople()
                .test {
                    //Then
                    assertEquals(Result.Success(emptyList<Person>()), expectItem())
                    assertEquals(Result.Success(listOf(dummyPerson)), expectItem())
                    assertEquals(Result.Success(listOf(dummyPerson2)), expectItem())
                    expectComplete()
                }
        }
}

private val dummyPerson2 = dummyPerson.copy(
    name = "Nick Skelton",
    url = "http://swapi.dev/api/people/2/",
)
