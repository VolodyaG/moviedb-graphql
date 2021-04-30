package com.volodya.moviedb.graphql.dataloaders

import com.netflix.graphql.dgs.DgsDataLoader
import com.volodya.moviedb.common.DB_IN_CLAUSE_SIZE_LIMIT
import com.volodya.moviedb.graphql.graphs.Character
import com.volodya.moviedb.graphql.graphs.toMovie
import com.volodya.moviedb.movies.CharacterDao
import com.volodya.moviedb.movies.CharactersTable
import org.dataloader.MappedBatchLoader
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.CompletableFuture

@DgsDataLoader(name = "characterMovies", maxBatchSize = DB_IN_CLAUSE_SIZE_LIMIT)
class PersonCharactersDataLoader : MappedBatchLoader<Int, List<Character>> {
    override fun load(keys: Set<Int>) = CompletableFuture.supplyAsync { charactersByPerson(keys) }!!

    private fun charactersByPerson(personIds: Set<Int>): Map<Int, List<Character>> = transaction {
        val foundCharacters = CharacterDao
            .find { CharactersTable.personId.inList(personIds) }
            .with(CharacterDao::movie, CharacterDao::person)
            .toList()
            .groupBy(keySelector = { it.person.id.value }) { it.toCharacter() }

        personIds.associateWithOrDefaultEmpty { foundCharacters[it] }
    }

    fun CharacterDao.toCharacter(): Character {
        return Character(
            this.id.value,
            this.playedCharacter,
            this.priorityOrder,
            movie = this.movie.toMovie(),
            actor = null
        )
    }
}