package com.volodya.moviedb.common

data class SortedBy(
    val field: String,
    val order: OrderBy,
)

enum class OrderBy { ASC, DESC }