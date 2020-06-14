package com.playground.starwars.model

import java.io.Serializable

data class PeopleResponse(
    val results: List<Person>,
    val count: Int?,
    val next: String?,
    val previous: String?
) : Serializable