package com.volodya.moviedb.movies

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object TagsTable : IntIdTable("tags") {
    val name = text("name")
}

class TagDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TagDao>(TagsTable)

    var name by TagsTable.name
}

object MovieTagsTable : Table("movie_tags") {
    val movieId = reference("movie_id", MoviesTable)
    val tagId = reference("tag_id", TagsTable)
}