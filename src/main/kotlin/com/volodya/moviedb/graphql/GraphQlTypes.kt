package com.volodya.moviedb.graphql

data class SortedBy(
    val field: String,
    val order: OrderBy,
)

enum class OrderBy { ASC, DESC }