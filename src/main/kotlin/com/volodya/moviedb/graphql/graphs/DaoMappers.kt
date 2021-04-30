package com.volodya.moviedb.graphql.graphs

import com.volodya.moviedb.movies.GenreDao
import com.volodya.moviedb.movies.MovieDao
import com.volodya.moviedb.movies.TagDao
import com.volodya.moviedb.people.PersonDao

fun MovieDao.toMovie(): Movie {
    return Movie(
        id = this.id.value,
        title = this.title,
        originalTitle = this.originalTitle,
        url = this.url,
        tagline = this.tagline,
        description = this.description,
        releaseDate = this.releaseDate,
        budgedUsd = this.budgedUsd,
        revenueUsd = this.revenueUsd,
        rating = this.rating,
        votesCount = this.votesCount,
    )
}

fun PersonDao.toPerson(): Person {
    return Person(this.id.value, this.name, this.gender)
}

fun TagDao.toTag() = Tag(id.value, name)
fun GenreDao.toGenre() = Genre(id.value, name)