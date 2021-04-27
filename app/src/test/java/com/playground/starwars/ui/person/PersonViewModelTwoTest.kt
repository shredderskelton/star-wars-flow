package com.playground.starwars.ui.person

import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.mock
import com.playground.starwars.TestCoroutineRule
import com.playground.starwars.api.dummyPerson
import com.playground.starwars.api.dummyPlanet
import com.playground.starwars.datasource.Result
import com.playground.starwars.datasource.StarWarsDataSource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class PersonViewModelTwoTest{

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var underTest: PersonViewModelTwo

    private val service = mock<StarWarsDataSource> {
        onBlocking { getPerson(2) }
            .then { flowOf(Result.Success(dummyPerson)) }
        onBlocking { getPlanet(2) }
            .then { flowOf(Result.Success(dummyPlanet)) }
    }

    private fun setup() {
        underTest = PersonViewModelTwo(service, 2)
    }

    @Test
    fun test() =
        testCoroutineRule.testDispatcher.runBlockingTest {
            setup()
            underTest.name.test {
                assertEquals("Luke SkyWalker", expectItem())
                expectComplete()
            }
        }
}