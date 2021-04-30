package com.volodya.moviedb.graphql.dataloaders

import com.netflix.graphql.dgs.DgsDataLoader
import com.volodya.moviedb.common.DB_IN_CLAUSE_SIZE_LIMIT
import com.volodya.moviedb.graphql.graphs.Movie
import com.volodya.moviedb.graphql.graphs.toMovie
import com.volodya.moviedb.movies.MovieComposerTable
import com.volodya.moviedb.movies.MovieDao
import com.volodya.moviedb.movies.MoviesTable
import org.dataloader.MappedBatchLoader
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

@DgsDataLoader(name = "composerMovies", maxBatchSize = DB_IN_CLAUSE_SIZE_LIMIT)
class ComposerMoviesDataLoader : MappedBatchLoader<Int, List<Movie>> {
    override fun load(keys: Set<Int>) = CompletableFuture.supplyAsync { composerMovies(keys) }!!

    private fun composerMovies(personIds: Set<Int>): Map<Int, List<Movie>> = transaction {
        val foundMovies = MovieComposerTable
            .innerJoin(MoviesTable)
            .select { MovieComposerTable.personId.inList(personIds) }
            .toList()
            .groupBy(keySelector = { it[MovieComposerTable.personId].value }) { MovieDao.wrapRow(it).toMovie() }

        personIds.associateWithOrDefaultEmpty { foundMovies[it] }
    }
}