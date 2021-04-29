package com.volodya.moviedb.movies.graphql

import com.volodya.moviedb.movies.Movie
import com.volodya.moviedb.movies.MovieDao
import com.volodya.moviedb.movies.characters.CharacterDao
import com.volodya.moviedb.movies.genres.Genre
import com.volodya.moviedb.movies.genres.GenreDao
import com.volodya.moviedb.movies.graphql.graphs.Character
import com.volodya.moviedb.movies.graphql.graphs.Person
import com.volodya.moviedb.movies.tags.Tag
import com.volodya.moviedb.movies.tags.TagDao
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

fun CharacterDao.toCharacter(): Character {
    return Character(this.id.value, this.playedCharacter, this.priorityOrder, this.person.toPerson())
}

fun TagDao.toTag() = Tag(id.value, name)
fun GenreDao.toGenre() = Genre(id.value, name)