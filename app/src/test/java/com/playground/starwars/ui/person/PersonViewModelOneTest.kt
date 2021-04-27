package com.playground.starwars.ui.person

import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.playground.starwars.TestCoroutineRule
import com.playground.starwars.api.dummyPerson
import com.playground.starwars.datasource.Result
import com.playground.starwars.datasource.StarWarsDataSource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PersonViewModelOneTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var underTest: PersonViewModelOne

    private val service = mock<StarWarsDataSource> {
        onBlocking { getPerson(2) }
            .then { flowOf(Result.Success(dummyPerson)) }
    }

    private fun setup() {
        underTest = PersonViewModelOne(service, 2)
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