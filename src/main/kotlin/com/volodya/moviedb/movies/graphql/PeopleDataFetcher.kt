package com.volodya.moviedb.movies.graphql

import com.netflix.graphql.dgs.*
import com.volodya.moviedb.movies.Movie
import com.volodya.moviedb.movies.characters.CharactersTable
import com.volodya.moviedb.movies.graphql.dataloaders.ComposerMoviesDataLoader
import com.volodya.moviedb.movies.graphql.dataloaders.DirectorMoviesDataLoader
import com.volodya.moviedb.movies.graphql.dataloaders.PersonCharactersDataLoader
import com.volodya.moviedb.movies.graphql.graphs.Character
import com.volodya.moviedb.movies.graphql.graphs.Person
import com.volodya.moviedb.people.Gender
import com.volodya.moviedb.people.PeopleTable
import com.volodya.moviedb.people.PersonDao
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

@DgsComponent
class PeopleDataFetcher {
    @DgsQuery
    fun person(@InputArgument id: Int) = transaction {
        requireNotNull(PersonDao.findById(id)) { "Person with id $id not found" }.toPerson()
    }

    @DgsQuery
    fun actors(
        @InputArgument limit: Int = 20,
        @InputArgument offset: Int = 0,
        @InputArgument gender: Gender?,
    ) = transaction {
        PersonDao.findActors(gender)
            .limit(limit, offset.toLong())
            .toList().map { it.toPerson() }
    }

    @DgsData(parentType = "Person", field = "directorOfMovies")
    fun directorOfMovies(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Movie>> {
        val person = dfe.getSource<Person>()
        val dataLoader = dfe.getDataLoader<Int, List<Movie>>(DirectorMoviesDataLoader::class.java)
        return dataLoader.load(person.id)
    }

    @DgsData(parentType = "Person", field = "compositorOfMovies")
    fun compositorOfMovies(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Movie>> {
        val person = dfe.getSource<Person>()
        val dataLoader = dfe.getDataLoader<Int, List<Movie>>(ComposerMoviesDataLoader::class.java)
        return dataLoader.load(person.id)
    }


    @DgsData(parentType = "Person", field = "playedCharacters")
    fun playedCharacters(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Character>> {
        val person = dfe.getSource<Person>()
        val dataLoader = dfe.getDataLoader<Int, List<Character>>(PersonCharactersDataLoader::class.java)
        return dataLoader.load(person.id)
    }

    private fun PersonDao.Companion.findActors(gender: Gender?): SizedIterable<PersonDao> {
        val op = if (gender != null) Op.build { PeopleTable.gender.eq(gender) } else Op.TRUE

        val filter = Op.build {
            PeopleTable.id.inSubQuery(CharactersTable.slice(CharactersTable.personId).selectAll())
        }

        return find(op.and(filter))
    }
}


