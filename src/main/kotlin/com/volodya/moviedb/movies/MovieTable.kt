package com.volodya.moviedb.movies

import com.volodya.moviedb.people.PersonDao
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.`java-time`.date

object MoviesTable : IntIdTable("movies") {
    val title = text("title")
    val originalTitle = text("original_title")
    val url = text("url").nullable()
    val tagline = text("tagline").nullable()
    val description = text("description")
    val releaseDate = date("release_date").nullable()
    val budgedUsd = long("budged_usd").nullable()
    val revenueUsd = long("revenue_usd").nullable()
    val rating = double("rating").nullable()
    val votesCount = integer("votes_count").nullable()
}

class MovieDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MovieDao>(MoviesTable)

    var title by MoviesTable.title
    var originalTitle by MoviesTable.originalTitle
    var url by MoviesTable.url
    var tagline by MoviesTable.tagline
    var description by MoviesTable.description
    var releaseDate by MoviesTable.releaseDate
    var budgedUsd by MoviesTable.budgedUsd
    var revenueUsd by MoviesTable.revenueUsd
    var rating by MoviesTable.rating
    var votesCount by MoviesTable.votesCount

    var tags by TagDao via MovieTagsTable
    var genres by GenreDao via MovieGenresTable

    val characters by CharacterDao referrersOn CharactersTable.movieId
    var composers by PersonDao via MovieComposerTable
    var directors by PersonDao via MovieDirectorTable
}