package com.volodya.moviedb.movies.graphql

import com.netflix.graphql.dgs.*
import com.volodya.moviedb.common.SortedBy
import com.volodya.moviedb.common.optionalOrderBy
import com.volodya.moviedb.common.sortedBy
import com.volodya.moviedb.movies.Movie
import com.volodya.moviedb.movies.MovieDao
import com.volodya.moviedb.movies.MoviesTable
import com.volodya.moviedb.movies.genres.Genre
import com.volodya.moviedb.movies.tags.Tag
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

@DgsComponent
class MoviesDataFetcher {
    @DgsQuery
    fun movies(
        @InputArgument limit: Int = 20,
        @InputArgument offset: Int = 0,
        sortedBy: SortedBy?
    ): List<Movie> {
        return transaction {
            MovieDao.all()
                .limit(limit, offset.toLong())
                .optionalOrderBy(MoviesTable.sortedBy(sortedBy))
                .toList().map { it.toMovie() }
        }
    }

    @DgsQuery
    fun movie(@InputArgument id: Int, dfe: DgsDataFetchingEnvironment): Movie {
        return transaction {
            requireNotNull(MovieDao.findById(id)) { "Movie with id $id not found" }
                .toMovie()
        }
    }

    @DgsData(parentType = "Movie", field = "tags")
    fun movieTags(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Tag>> {
        val movie = dfe.getSource<Movie>()
        val dataLoader = dfe.getDataLoader<Int, List<Tag>>(MovieTagsDataLoader::class.java)
        return dataLoader.load(movie.id)
    }

    @DgsData(parentType = "Movie", field = "genres")
    fun movieGenres(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Genre>> {
        val movie = dfe.getSource<Movie>()
        val dataLoader = dfe.getDataLoader<Int, List<Genre>>(MovieGenresDataLoader::class.java)
        return dataLoader.load(movie.id)
    }
}