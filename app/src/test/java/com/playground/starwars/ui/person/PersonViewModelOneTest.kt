package com.playground.starwars.ui.person

import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.mock
import com.playground.starwars.TestCoroutineRule
import com.playground.starwars.service.Result
import com.playground.starwars.service.StarWarsService
import com.playground.starwars.service.api.dummyPerson
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PersonViewModelOneTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var underTest: PersonViewModelOne

    private val service = mock<StarWarsService> {
        onBlocking { getPerson(2) }
            .then { flowOf(Result.Success(dummyPerson)) }
    }

    fun setup() {
        underTest = PersonViewModelOne(service, 2)
    }

    @Test
    fun test() {
        runBlocking {
            setup()
            underTest.name.test {
                assertEquals("Luke SkyWalker", expectItem())
            }
        }
    }

}