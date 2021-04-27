package com.playground.starwars.datasource

import com.playground.starwars.Loggable
import com.playground.starwars.api.StarWarsApi
import com.playground.starwars.logDebug
import com.playground.starwars.model.Film
import com.playground.starwars.model.Person
import com.playground.starwars.model.Planet
import com.playground.starwars.ui.DefaultDispatcherProvider
import com.playground.starwars.ui.DispatcherProvider
import com.playground.starwars.ui.Simulator
import com.playground.starwars.ui.SimulatorImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

interface StarWarsDataSource {
    fun getPeople(): Flow<Result<List<Person>>>
    fun getPerson(id: Int): Flow<Result<Person>>
    fun getPlanet(id: Int): Flow<Result<Planet>>
    fun getFilm(id: Int): Flow<Result<Film>>
}

class StarWarsDataSourceImpl(
    private val starWarsApi: StarWarsApi,
    private val simulator: Simulator = SimulatorImpl(),
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) : StarWarsDataSource, Loggable {

    private var requestCount = 0

    override fun getPeople(): Flow<Result<List<Person>>> =
        flow {
            var page = 1
            var cont = true
            emit(Result.Success(emptyList()))
            while (cont) {
                logDebug { "getAll - page: $page" }
                simulator.delay()
                val response = starWarsApi.getPeople(page)
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        emit(Result.Success(data.results))
                        cont = data.next != null
                        page++
                    }
                } else {
                    cont = false
                    emit(Result.Error(response.message()))
                }
            }
        }.flowOn(dispatcherProvider.default())

    override fun getPerson(id: Int): Flow<Result<Person>> =
        flow {
            logDebug { "${requestCount++}: getPerson: $id " }
            simulator.delay()
            emit(starWarsApi.getPerson(id).dataOrError())
        }

    override fun getPlanet(id: Int): Flow<Result<Planet>> =
        flow {
            logDebug { "getPlanet: $id" }
            simulator.delay()
            emit(starWarsApi.getPlanet(id).dataOrError())
        }

    override fun getFilm(id: Int): Flow<Result<Film>> =
        flow {
            logDebug { "getFilm: $id" }
            simulator.delay()
            emit(starWarsApi.getFilm(id).dataOrError())
        }
}

private fun <T : Any> Response<T>.dataOrError(): Result<T> =
    if (isSuccessful) {
        val data = body()
        if (data != null) Result.Success(data)
        else Result.Error("Not found")
    } else Result.Error("Request failed")
