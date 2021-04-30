package com.volodya.moviedb.graphql.graphs

class Character(
    val id: Int,
    val name: String,
    val order: Int,
    val actor: Person?,
    val movie: Movie?,
)