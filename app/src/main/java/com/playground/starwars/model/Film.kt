package com.playground.starwars.model

import java.io.Serializable

data class Film(
    val url: String,
    val title: String,
    val release_date: String
) : Serializable

val Film.id
    get() = url
        .replace("http://swapi.dev/api/films/", "")
        .replace("/", "")
        .toInt()