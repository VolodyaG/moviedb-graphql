package com.volodya.moviedb.graphql.dataloaders

import com.netflix.graphql.dgs.DgsDataLoader
import com.volodya.moviedb.common.DB_IN_CLAUSE_SIZE_LIMIT
import com.volodya.moviedb.graphql.graphs.Person
import com.volodya.moviedb.graphql.graphs.toPerson
import com.volodya.moviedb.movies.MovieDirectorTable
import com.volodya.moviedb.people.PeopleTable
import com.volodya.moviedb.people.PersonDao
import org.dataloader.MappedBatchLoader
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

@DgsDataLoader(name = "movieDirectors", maxBatchSize = DB_IN_CLAUSE_SIZE_LIMIT)
class MovieDirectorsDataLoader : MappedBatchLoader<Int, List<Person>> {
    override fun load(keys: Set<Int>) = CompletableFuture.supplyAsync { directorsByMovie(keys) }!!

    private fun directorsByMovie(movieIds: Set<Int>): Map<Int, List<Person>> = transaction {
        val foundPersons = MovieDirectorTable
            .innerJoin(PeopleTable)
            .select { MovieDirectorTable.movieId.inList(movieIds) }
            .toList()
            .groupBy(keySelector = { it[MovieDirectorTable.movieId].value }) { PersonDao.wrapRow(it).toPerson() }

        movieIds.associateWithOrDefaultEmpty { foundPersons[it] }
    }
}