package com.volodya.moviedb.movies.characters

import com.volodya.moviedb.movies.MovieDao
import com.volodya.moviedb.movies.MoviesTable
import com.volodya.moviedb.people.PeopleTable
import com.volodya.moviedb.people.PersonDao
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

// Ideally to have Characters table inherits People table , but Exposed not yet supports it
object CharactersTable : IntIdTable("movie_characters") {
    val movieId = reference("movie_id", MoviesTable)
    val personId = reference("person_id", PeopleTable)
    val playedCharacter = text("played_character")
    val priorityOrder = integer("priority_order")
}

class CharacterDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CharacterDao>(CharactersTable)

    var movie by MovieDao referencedOn CharactersTable.movieId
    var person by PersonDao referencedOn CharactersTable.personId
    var playedCharacter by CharactersTable.playedCharacter
    var priorityOrder by CharactersTable.priorityOrder
}