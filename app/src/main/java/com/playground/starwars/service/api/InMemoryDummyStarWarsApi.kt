package com.playground.starwars.service.api

import com.playground.starwars.model.PeopleResponse
import com.playground.starwars.model.Film
import com.playground.starwars.model.Person
import com.playground.starwars.model.Planet
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

val dummyPerson = Person(
    "Luke SkyWalker",
    "12",
    "http://swapi.dev/api/people/1/",
    "male",
    "12",
    "http://swapi.dev/api/planets/2/",
    "1981",
    listOf(
        "http://swapi.dev/api/films/1",
        "http://swapi.dev/api/films/2",
        "http://swapi.dev/api/films/3",
        "http://swapi.dev/api/films/4",
        "http://swapi.dev/api/films/5",
        "http://swapi.dev/api/films/6"
    )
)

val dummyPlanet = Planet(
    name = "Tatooine",
    climate = "Hot",
    gravity = "9.2",
    terrain = "Rocky",
    population = "19999",
    url = ""
)

val dummyFilm = Film("http://swapi.dev/api/films/1", "A New Hope", "1985")
val dummyFilm2 = Film("http://swapi.dev/api/films/2", "The Empire Strikes Back", "1986")
val dummyFilm3 = Film("http://swapi.dev/api/films/3", "Return of the Jedi", "1987")
val dummyFilm4 = Film("http://swapi.dev/api/films/4", "The Phantom Menace", "1990")
val dummyFilm5 = Film("http://swapi.dev/api/films/5", "Attack of the Clones", "1995")
val dummyFilm6 = Film("http://swapi.dev/api/films/6", "Revenge of the Sith", "1999")

val responseErrorBody = "".toResponseBody("text/plain".toMediaTypeOrNull())

class InMemoryDummyStarWarsApi : StarWarsApi {
    override suspend fun getPeople(page: Int): Response<PeopleResponse> {
        return Response.success(
            PeopleResponse(
                results = listOf(element = dummyPerson),
                count = 1,
                next = null,
                previous = null
            )
        )
    }

    override suspend fun getPerson(id: Int): Response<Person> =
        when (id) {
            1 -> Response.success(dummyPerson)
            else -> Response.error(404, "".toResponseBody("application/json".toMediaTypeOrNull()))
        }

    private var shouldError = true
    override suspend fun getPlanet(id: Int): Response<Planet> {
        shouldError = !shouldError
        return if (shouldError)
            Response.success(dummyPlanet)
        else
            Response.error(
                404,
                responseErrorBody
            )
    }

    override suspend fun getFilm(id: Int): Response<Film> {
        val film = when (id) {
            1 -> dummyFilm
            2 -> dummyFilm2
            3 -> return Response.error(
                404,
                responseErrorBody
            )
            4 -> dummyFilm4
            5 -> dummyFilm5
            6 -> dummyFilm6
            else -> dummyFilm
        }
        return Response.success(film)
    }

}