package com.volodya.moviedb.movies.graphql

import com.netflix.graphql.dgs.DgsDataLoader
import com.volodya.moviedb.common.DB_IN_CLAUSE_SIZE_LIMIT
import com.volodya.moviedb.movies.tags.MovieTagsTable
import com.volodya.moviedb.movies.tags.Tag
import com.volodya.moviedb.movies.tags.TagDao
import com.volodya.moviedb.movies.tags.TagsTable
import org.dataloader.MappedBatchLoader
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

@DgsDataLoader(name = "movieTags", maxBatchSize = DB_IN_CLAUSE_SIZE_LIMIT)
class MovieTagsDataLoader : MappedBatchLoader<Int, List<Tag>> {
    override fun load(keys: Set<Int>) = CompletableFuture.supplyAsync { tagsByMovie(keys) }!!

    private fun tagsByMovie(movieIds: Set<Int>): Map<Int, List<Tag>> = transaction {
        val foundMovieTags = MovieTagsTable
            .innerJoin(TagsTable)
            .select { MovieTagsTable.movieId.inList(movieIds) }
            .toList()
            .groupBy(keySelector = { it[MovieTagsTable.movieId].value }) { TagDao.wrapRow(it).toTag() }

        movieIds.associateWith { foundMovieTags[it].orEmpty() } // Populate default values for provided keys to avoid NPE
    }
}