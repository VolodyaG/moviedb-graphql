package com.volodya.moviedb.graphql

import com.netflix.graphql.dgs.*
import com.volodya.moviedb.common.ilike
import com.volodya.moviedb.common.optionalOrderBy
import com.volodya.moviedb.common.sortedBy
import com.volodya.moviedb.graphql.dataloaders.*
import com.volodya.moviedb.graphql.graphs.*
import com.volodya.moviedb.graphql.typings.SortedBy
import com.volodya.moviedb.movies.MovieDao
import com.volodya.moviedb.movies.MoviesTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture


@DgsComponent
class MoviesDataFetcher {
    @DgsQuery
    fun movies(
        @InputArgument limit: Int = 20,
        @InputArgument offset: Int = 0,
        @InputArgument searchQuery: String?,
        @InputArgument sortedBy: SortedBy?
    ): List<Movie> {
        return transaction {
            MovieDao.searchMovies(searchQuery)
                .limit(limit, offset.toLong())
                .optionalOrderBy(MoviesTable.sortedBy(sortedBy))
                .toList().map { it.toMovie() }
        }
    }

    @DgsQuery
    fun movie(@InputArgument id: Int): Movie {
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

    @DgsData(parentType = "Movie", field = "directors")
    fun movieDirectors(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Person>> {
        val movie = dfe.getSource<Movie>()
        val dataLoader = dfe.getDataLoader<Int, List<Person>>(MovieDirectorsDataLoader::class.java)
        return dataLoader.load(movie.id)
    }

    @DgsData(parentType = "Movie", field = "composers")
    fun movieComposers(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Person>> {
        val movie = dfe.getSource<Movie>()
        val dataLoader = dfe.getDataLoader<Int, List<Person>>(MovieComposersDataLoader::class.java)
        return dataLoader.load(movie.id)
    }


    @DgsData(parentType = "Movie", field = "characters")
    fun movieCharacters(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Character>> {
        val movie = dfe.getSource<Movie>()
        val dataLoader = dfe.getDataLoader<Int, List<Character>>(MovieCharactersDataLoader::class.java)
        return dataLoader.load(movie.id)
    }

    private fun MovieDao.Companion.searchMovies(searchQuery: String?): SizedIterable<MovieDao> {
        val op = if (searchQuery != null) {
            Op.build {
                val like = "%$searchQuery%"
                MoviesTable.description.ilike(like).or(MoviesTable.title.ilike(searchQuery))
            }
        } else Op.TRUE

        return find(op)
    }
}