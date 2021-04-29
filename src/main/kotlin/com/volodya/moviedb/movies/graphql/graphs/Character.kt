package com.volodya.moviedb.movies.graphql.graphs

class Character(
    val id: Int,
    val name: String,
    val order: Int,
    val actor: Person
)