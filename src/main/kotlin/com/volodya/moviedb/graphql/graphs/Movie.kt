package com.volodya.moviedb.graphql.graphs

import java.time.LocalDate

class Movie(
    val id: Int,
    val title: String,
    val originalTitle: String,
    val url: String?,
    val tagline: String?,
    val description: String,
    val releaseDate: LocalDate?,
    val budgedUsd: Long?,
    val revenueUsd: Long?,
    val rating: Double?,
    val votesCount: Int?,

    val tags: List<Tag> = emptyList(),
    val genres: List<Genre> = emptyList(),
)