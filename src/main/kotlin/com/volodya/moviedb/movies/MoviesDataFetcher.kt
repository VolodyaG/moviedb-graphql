package com.volodya.moviedb.movies

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.jetbrains.exposed.sql.transactions.transaction

@DgsComponent
class MoviesDataFetcher {
    @DgsQuery
    fun movies(@InputArgument limit: Int = 20, @InputArgument offset: Int = 0): List<Movie> {
        return transaction {
            MovieDao.all().limit(limit, offset.toLong()).toList().map { it.toMovie() }
        }
    }

    @DgsQuery
    fun movie(@InputArgument id: Int): Movie {
        return transaction {
            requireNotNull(MovieDao.findById(id)) { "Movie with id $id not found" }
                .toMovie()
        }
    }

    private fun MovieDao.toMovie(): Movie {
        return Movie(
            id = this.id.value,
            title = this.title,
            originalTitle = this.originalTitle,
            url = this.url,
            tagline = this.tagline,
            description = this.description,
            releaseDate = this.releaseDate,
            budgedUsd = this.budgedUsd,
            revenueUsd = this.revenueUsd,
            rating = this.rating,
            votesCount = this.votesCount,
        )
    }
}