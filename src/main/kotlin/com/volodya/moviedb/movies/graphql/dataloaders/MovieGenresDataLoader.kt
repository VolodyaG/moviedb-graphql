package com.volodya.moviedb.movies.graphql.dataloaders

import com.netflix.graphql.dgs.DgsDataLoader
import com.volodya.moviedb.common.DB_IN_CLAUSE_SIZE_LIMIT
import com.volodya.moviedb.movies.genres.Genre
import com.volodya.moviedb.movies.genres.GenreDao
import com.volodya.moviedb.movies.genres.GenresTable
import com.volodya.moviedb.movies.genres.MovieGenresTable
import com.volodya.moviedb.movies.graphql.toGenre
import org.dataloader.MappedBatchLoader
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

@DgsDataLoader(name = "movieGenres", maxBatchSize = DB_IN_CLAUSE_SIZE_LIMIT)
class MovieGenresDataLoader : MappedBatchLoader<Int, List<Genre>> {
    override fun load(keys: Set<Int>) = CompletableFuture.supplyAsync { tagsByMovie(keys) }!!

    private fun tagsByMovie(movieIds: Set<Int>): Map<Int, List<Genre>> = transaction {
        val foundMovieGenres = MovieGenresTable
            .innerJoin(GenresTable)
            .select { MovieGenresTable.movieId.inList(movieIds) }
            .toList()
            .groupBy(keySelector = { it[MovieGenresTable.movieId].value }) { GenreDao.wrapRow(it).toGenre() }

        movieIds.associateWithOrDefaultEmpty { foundMovieGenres[it] }
    }
}