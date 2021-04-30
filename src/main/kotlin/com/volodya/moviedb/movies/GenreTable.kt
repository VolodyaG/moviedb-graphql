package com.volodya.moviedb.movies

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object GenresTable : IntIdTable("genres") {
    val name = text("name")
}

class GenreDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GenreDao>(GenresTable)

    var name by GenresTable.name
}

object MovieGenresTable : Table("movie_genres") {
    val movieId = reference("movie_id", MoviesTable)
    val genreId = reference("genre_id", GenresTable)
}