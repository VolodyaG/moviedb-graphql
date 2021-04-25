package com.volodya.moviedb.people

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object PeopleTable : IntIdTable("people") {
    val name = text("name")
    val gender = enumerationByName("gender", 20, Gender::class).nullable()
}

class PersonDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PersonDao>(PeopleTable)

    var name by PeopleTable.name
    var gender by PeopleTable.gender
}