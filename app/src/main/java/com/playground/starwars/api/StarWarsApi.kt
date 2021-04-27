package com.playground.starwars.api

import com.playground.starwars.model.PeopleResponse
import com.playground.starwars.model.Film
import com.playground.starwars.model.Person
import com.playground.starwars.model.Planet
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StarWarsApi {

    @GET("api/people")
    suspend fun getPeople(@Query("page") page: Int): Response<PeopleResponse>

    @GET("api/people/{id}")
    suspend fun getPerson(@Path("id") id: Int): Response<Person>

    @GET("api/planets/{id}")
    suspend fun getPlanet(@Path("id") id: Int): Response<Planet>

    @GET("api/films/{id}")
    suspend fun getFilm(@Path("id") id: Int): Response<Film>
}
