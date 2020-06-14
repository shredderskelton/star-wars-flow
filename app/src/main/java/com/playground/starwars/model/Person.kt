package com.playground.starwars.model

import java.io.Serializable

data class Person(
    val name: String,
    val height: String,
    val url: String,
    val gender: String,
    val mass: String,
    val homeworld: String,
    val birth_year: String,
    val films: List<String>
) : Serializable

val Person.planetId
    get() = homeworld
        .replace("http://swapi.dev/api/planets/", "")
        .replace("/", "")
        .toInt()

val Person.id
    get() = url
        .replace("http://swapi.dev/api/people/", "")
        .replace("/", "")
        .toInt()

val Person.bmi: String
    get() {
        val m = mass.toBigDecimalOrNull() ?: return "unknown"
        val h = height.toBigDecimalOrNull() ?: return "unknown"
        return (m / h).setScale(2).toString()
    }

val Person.filmIds
    get() = films.map {
        it.replace("http://swapi.dev/api/films/", "")
            .replace("/", "")
            .toInt()
    }
