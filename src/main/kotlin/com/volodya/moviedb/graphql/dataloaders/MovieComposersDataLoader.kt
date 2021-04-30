package com.volodya.moviedb.graphql.dataloaders

import com.netflix.graphql.dgs.DgsDataLoader
import com.volodya.moviedb.common.DB_IN_CLAUSE_SIZE_LIMIT
import com.volodya.moviedb.graphql.graphs.Person
import com.volodya.moviedb.graphql.graphs.toPerson
import com.volodya.moviedb.movies.MovieComposerTable
import com.volodya.moviedb.people.PeopleTable
import com.volodya.moviedb.people.PersonDao
import org.dataloader.MappedBatchLoader
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

@DgsDataLoader(name = "movieComposers", maxBatchSize = DB_IN_CLAUSE_SIZE_LIMIT)
class MovieComposersDataLoader : MappedBatchLoader<Int, List<Person>> {
    override fun load(keys: Set<Int>) = CompletableFuture.supplyAsync { composersByMovie(keys) }!!

    private fun composersByMovie(movieIds: Set<Int>): Map<Int, List<Person>> = transaction {
        val foundPersons = MovieComposerTable
            .innerJoin(PeopleTable)
            .select { MovieComposerTable.movieId.inList(movieIds) }
            .toList()
            .groupBy(keySelector = { it[MovieComposerTable.movieId].value }) { PersonDao.wrapRow(it).toPerson() }

        movieIds.associateWithOrDefaultEmpty { foundPersons[it] }
    }
}