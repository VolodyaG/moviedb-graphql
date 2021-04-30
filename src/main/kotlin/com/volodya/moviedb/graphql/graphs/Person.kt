package com.volodya.moviedb.graphql.graphs

import com.volodya.moviedb.people.Gender

open class Person(
    val id: Int,
    val name: String,
    val gender: Gender?,
)