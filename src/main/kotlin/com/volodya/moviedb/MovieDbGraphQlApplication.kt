package com.volodya.moviedb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MovieDbGraphQlApplication

fun main(args: Array<String>) {
    runApplication<MovieDbGraphQlApplication>(*args)
}
