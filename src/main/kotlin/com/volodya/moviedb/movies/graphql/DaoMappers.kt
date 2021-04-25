package com.volodya.moviedb.movies.graphql

import com.volodya.moviedb.movies.Movie
import com.volodya.moviedb.movies.MovieDao
import com.volodya.moviedb.movies.genres.Genre
import com.volodya.moviedb.movies.genres.GenreDao
import com.volodya.moviedb.movies.tags.Tag
import com.volodya.moviedb.movies.tags.TagDao

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

fun TagDao.toTag() = Tag(id.value, name)
fun GenreDao.toGenre() = Genre(id.value, name)