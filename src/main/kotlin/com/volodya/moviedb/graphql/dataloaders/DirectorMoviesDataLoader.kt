package com.volodya.moviedb.graphql.dataloaders

import com.netflix.graphql.dgs.DgsDataLoader
import com.volodya.moviedb.common.DB_IN_CLAUSE_SIZE_LIMIT
import com.volodya.moviedb.graphql.graphs.Movie
import com.volodya.moviedb.graphql.graphs.toMovie
import com.volodya.moviedb.movies.MovieDao
import com.volodya.moviedb.movies.MovieDirectorTable
import com.volodya.moviedb.movies.MoviesTable
import org.dataloader.MappedBatchLoader
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

@DgsDataLoader(name = "directorMovies", maxBatchSize = DB_IN_CLAUSE_SIZE_LIMIT)
class DirectorMoviesDataLoader : MappedBatchLoader<Int, List<Movie>> {
    override fun load(keys: Set<Int>) = CompletableFuture.supplyAsync { directorMovies(keys) }!!

    private fun directorMovies(personIds: Set<Int>): Map<Int, List<Movie>> = transaction {
        val foundMovies = MovieDirectorTable
            .innerJoin(MoviesTable)
            .select { MovieDirectorTable.personId.inList(personIds) }
            .toList()
            .groupBy(keySelector = { it[MovieDirectorTable.personId].value }) { MovieDao.wrapRow(it).toMovie() }

        personIds.associateWithOrDefaultEmpty { foundMovies[it] }
    }
}