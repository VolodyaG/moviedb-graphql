package com.volodya.moviedb.movies.graphql.graphs

import com.volodya.moviedb.movies.Movie

class Character(
    val id: Int,
    val name: String,
    val order: Int,
    val actor: Person?,
    val movie: Movie?,
)