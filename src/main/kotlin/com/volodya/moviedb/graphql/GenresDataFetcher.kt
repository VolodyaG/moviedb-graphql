package com.volodya.moviedb.graphql

import com.netflix.graphql.dgs.*
import com.volodya.moviedb.graphql.dataloaders.GenreMoviesDataLoader
import com.volodya.moviedb.graphql.graphs.Genre
import com.volodya.moviedb.graphql.graphs.Movie
import com.volodya.moviedb.graphql.graphs.toGenre
import com.volodya.moviedb.graphql.typings.SortedBy
import com.volodya.moviedb.movies.GenreDao
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

@DgsComponent
class GenresDataFetcher {
    @DgsQuery
    fun genres(): List<Genre> = transaction {
        GenreDao.all().toList().map { it.toGenre() }
    }

    @DgsQuery
    fun genre(@InputArgument id: Int): Genre = transaction {
        requireNotNull(GenreDao.findById(id)) { "Movie with id $id not found" }.toGenre()
    }

    @DgsData(parentType = "Genre", field = "movies")
    fun genreMovies(
        @InputArgument limit: Int = 10,
        @InputArgument sortedBy: SortedBy?,
        dfe: DgsDataFetchingEnvironment
    ): CompletableFuture<List<Movie>> {
        val genre = dfe.getSource<Genre>()
        val dataLoader = dfe.getDataLoader<Int, List<Movie>>(GenreMoviesDataLoader::class.java)
        return dataLoader.load(genre.id, GenreMoviesDataLoader.Context(limit, sortedBy))
    }
}