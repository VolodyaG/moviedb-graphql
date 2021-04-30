package com.volodya.moviedb.graphql.dataloaders

import com.netflix.graphql.dgs.DgsDataLoader
import com.volodya.moviedb.common.DB_IN_CLAUSE_SIZE_LIMIT
import com.volodya.moviedb.common.optionalOrderBy
import com.volodya.moviedb.common.sortedBy
import com.volodya.moviedb.graphql.graphs.Movie
import com.volodya.moviedb.graphql.graphs.toMovie
import com.volodya.moviedb.graphql.typings.SortedBy
import com.volodya.moviedb.movies.MovieDao
import com.volodya.moviedb.movies.MovieGenresTable
import com.volodya.moviedb.movies.MoviesTable
import org.dataloader.BatchLoaderEnvironment
import org.dataloader.MappedBatchLoaderWithContext
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture


@DgsDataLoader(name = "genreMovies", maxBatchSize = DB_IN_CLAUSE_SIZE_LIMIT)
class GenreMoviesDataLoader : MappedBatchLoaderWithContext<Int, List<Movie>> {
    override fun load(keys: Set<Int>, env: BatchLoaderEnvironment): CompletableFuture<Map<Int, List<Movie>>> {
        return CompletableFuture.supplyAsync { moviesByGenre(keys, parseContext(env)) }!!
    }

    private fun moviesByGenre(genreIds: Set<Int>, context: Context): Map<Int, List<Movie>> = transaction {
        val foundMoviesByGenre = MovieGenresTable
            .innerJoin(MoviesTable)
            .select { MovieGenresTable.genreId.inList(genreIds) }
            .optionalOrderBy(MoviesTable.sortedBy(context.sortedBy))
            .toList()
            .groupBy(keySelector = { it[MovieGenresTable.genreId].value }) { MovieDao.wrapRow(it).toMovie() }
            .mapValues { it.value.take(context.limit) } // Todo Do filtration on sql side

        genreIds.associateWithOrDefaultEmpty { foundMoviesByGenre[it] }
    }

    private fun parseContext(env: BatchLoaderEnvironment): Context {
        // Todo Find out how properly provide query parameters to dataloader
        val contexts = env.keyContextsList.distinct()
        require(contexts.size == 1) { "DataLoader should have single context" }
        return contexts.first() as Context
    }

    data class Context(
        val limit: Int,
        val sortedBy: SortedBy?,
    )
}