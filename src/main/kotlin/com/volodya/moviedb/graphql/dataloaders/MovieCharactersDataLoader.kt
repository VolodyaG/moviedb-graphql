package com.volodya.moviedb.graphql.dataloaders

import com.netflix.graphql.dgs.DgsDataLoader
import com.volodya.moviedb.common.DB_IN_CLAUSE_SIZE_LIMIT
import com.volodya.moviedb.graphql.graphs.Character
import com.volodya.moviedb.graphql.graphs.toPerson
import com.volodya.moviedb.movies.CharacterDao
import com.volodya.moviedb.movies.CharactersTable
import org.dataloader.MappedBatchLoader
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

@DgsDataLoader(name = "movieCharacters", maxBatchSize = DB_IN_CLAUSE_SIZE_LIMIT)
class MovieCharactersDataLoader : MappedBatchLoader<Int, List<Character>> {
    override fun load(keys: Set<Int>) = CompletableFuture.supplyAsync { actorsByMovie(keys) }!!

    private fun actorsByMovie(movieIds: Set<Int>): Map<Int, List<Character>> = transaction {
        val foundCharacters = CharacterDao
            .find { CharactersTable.movieId.inList(movieIds) }
            .orderBy(CharactersTable.priorityOrder to SortOrder.ASC)
            .with(CharacterDao::person)
            .toList()
            .groupBy(keySelector = { it.movie.id.value }) { it.toCharacter() }

        movieIds.associateWithOrDefaultEmpty { foundCharacters[it] }
    }

    fun CharacterDao.toCharacter(): Character {
        return Character(this.id.value, this.playedCharacter, this.priorityOrder, this.person.toPerson(), movie = null)
    }
}