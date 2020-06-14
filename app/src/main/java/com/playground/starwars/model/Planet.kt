package com.playground.starwars.model

import java.io.Serializable

data class Planet(
    val name: String,
    val climate: String,
    val gravity: String,
    val terrain: String,
    val population: String,
    val url: String
) : Serializable

val Planet.id
    get() = url
        .replace("http://swapi.dev/api/planets/", "")
        .replace("/", "")
        .toInt()