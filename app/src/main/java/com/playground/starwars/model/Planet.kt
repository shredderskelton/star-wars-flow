package com.playground.starwars.model

import kotlinx.serialization.Serializable

@Serializable
data class Planet(
    val name: String,
    val climate: String,
    val gravity: String,
    val terrain: String,
    val population: String,
    val url: String
)

val Planet.id
    get() = url
        .replace("http://swapi.dev/api/planets/", "")
        .replace("/", "")
        .toInt()