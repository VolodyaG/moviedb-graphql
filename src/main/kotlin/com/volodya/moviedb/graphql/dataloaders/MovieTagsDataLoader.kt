package com.volodya.moviedb.graphql.dataloaders

import com.netflix.graphql.dgs.DgsDataLoader
import com.volodya.moviedb.common.DB_IN_CLAUSE_SIZE_LIMIT
import com.volodya.moviedb.graphql.graphs.Tag
import com.volodya.moviedb.graphql.graphs.toTag
import com.volodya.moviedb.movies.MovieTagsTable
import com.volodya.moviedb.movies.TagDao
import com.volodya.moviedb.movies.TagsTable
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

        movieIds.associateWithOrDefaultEmpty { foundMovieTags[it] }
    }
}