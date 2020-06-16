package com.playground.starwars.api.people


import com.playground.starwars.service.HttpStarWarsService
import com.playground.starwars.service.Result
import com.playground.starwars.service.api.FakeStarWarsApi
import com.playground.starwars.service.api.StarWarsApi
import com.playground.starwars.service.api.dummyPerson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

//@ExperimentalCoroutinesApi
//@InternalCoroutinesApi
class HttpStarWarsServiceTest {

    private lateinit var underTest: HttpStarWarsService

    private val api: StarWarsApi = FakeStarWarsApi()

    @Test
    fun `when started loading is returned`() {
        runBlocking {
            underTest = HttpStarWarsService(api)
            val flow = underTest.getPerson(1)
            assertThat(flow.first()).isEqualTo(Result.Loading)
        }
    }

    @Test
    fun `when success person is returned`() {
        runBlocking {
            underTest = HttpStarWarsService(api)
            val flow = underTest.getPerson(1)
            assertThat(flow.toList()).containsExactly(
                Result.Loading,
                Result.Success(dummyPerson)
            )
        }
    }

    @Test
    fun `when failure, result error is emitted`() {
        runBlocking {
            underTest = HttpStarWarsService(api)
            val flow = underTest.getPerson(2)
            assertThat(flow.toList()).containsExactly(
                Result.Loading,
                Result.Error(Exception("Request failed"))
            )
        }
    }
}


