package com.playground.starwars.service

import com.playground.starwars.model.Film
import com.playground.starwars.model.Person
import com.playground.starwars.model.Planet
import kotlinx.coroutines.flow.Flow

interface StarWarsService {
    fun getPeople():        Flow<Result<List<Person>>>
    fun getPerson(id: Int): Flow<Result<Person>>
    fun getPlanet(id: Int): Flow<Result<Planet>>
    fun getFilm(id: Int):   Flow<Result<Film>>
}