package com.playground.starwars.service

import com.playground.starwars.model.Film
import com.playground.starwars.model.Person
import com.playground.starwars.model.Planet
import com.playground.starwars.service.api.StarWarsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

private const val DELAY = 2000L

class HttpStarWarsService(
    private val starWarsApi: StarWarsApi
) : StarWarsService, Loggable {

    override fun getPeople(): Flow<Result<List<Person>>> = flow {
        var page = 1
        var cont = true
        emit(Result.Success(emptyList()))
        while (cont) {
            logDebug { "getAll - page: $page" }
            delay(1000)
            val response = starWarsApi.getPeople(page)
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    emit(Result.Success(data.results))
                    cont = data.next != null
                    page++
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getPerson(id: Int): Flow<Result<Person>> = flow {
        logDebug { "getPerson: $id" }
        delay(DELAY)
        emit(starWarsApi.getPerson(id).dataOrError())
    }.flowOn(Dispatchers.IO)

    private fun <T : Any> Response<T>.dataOrError(): Result<T> =
        if (isSuccessful) {
            val data = body()
            if (data != null) Result.Success(data)
            else Result.Error(Exception("Not found"))
        } else Result.Error(Exception("Request failed"))

    override fun getPlanet(id: Int): Flow<Result<Planet>> = flow {
        logDebug { "getPlanet: $id" }
        delay(DELAY)
        emit(starWarsApi.getPlanet(id).dataOrError())
    }.flowOn(Dispatchers.IO)

    override fun getFilm(id: Int): Flow<Result<Film>> = flow {
        logDebug { "getFilm: $id" }
        delay(DELAY)
        emit(starWarsApi.getFilm(id).dataOrError())
    }.flowOn(Dispatchers.IO)

}