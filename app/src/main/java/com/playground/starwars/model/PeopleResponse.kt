package com.playground.starwars.model

import kotlinx.serialization.Serializable

@Serializable
data class PeopleResponse(
    val results: List<Person>,
    val count: Int?,
    val next: String?,
    val previous: String?
)