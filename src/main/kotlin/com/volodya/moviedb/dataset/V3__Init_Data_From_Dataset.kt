package com.volodya.moviedb.dataset

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.volodya.moviedb.movies.*
import com.volodya.moviedb.people.Gender
import com.volodya.moviedb.people.PeopleTable
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component

@Component
@DependsOn("springTransactionManager") // make sure that Exposed is initialized
@Suppress("ClassName")
class V3__Init_Data_From_Dataset : BaseJavaMigration() {
    private val csvMapper = CsvMapper().apply {
        this.findAndRegisterModules()
        this.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE
    }

    override fun migrate(context: Context) {
        transaction {
            val creditsDataset = readCsvFile<DatasetCreditRow>("/dataset/tmdb_5000_credits.csv")
                .filter { row -> row.crew.any { it.department == "Directing" && it.job == "Director" } } // Ignore movies without director
                .associateBy { it.movie_id }

            val moviesDataset = readCsvFile<DatasetMovieRow>("/dataset/tmdb_5000_movies.csv")
                .filter { creditsDataset.containsKey(it.id) && it.overview != null }
                .associateBy { it.id }

            parseAndCreateData(creditsDataset, moviesDataset)
        }
    }

    private fun parseAndCreateData(
        creditsDataset: Map<MovieId, DatasetCreditRow>,
        moviesDataset: Map<MovieId, DatasetMovieRow>
    ) {
        parseTags(moviesDataset).also { exposedLogger.info("Created tags") }
        parseGenres(moviesDataset).also { exposedLogger.info("Created Genres") }
        parsePeople(creditsDataset).also { exposedLogger.info("Created People") }
        parseMovies(moviesDataset).also { exposedLogger.info("Created Movies") }

        mapMovieDirectors(creditsDataset).also { exposedLogger.info("Mapped Directors") }
        mapMovieComposers(creditsDataset).also { exposedLogger.info("Mapped Composers") }
        mapMovieCharacters(creditsDataset).also { exposedLogger.info("Mapped Characters") }
        mapTags(moviesDataset).also { exposedLogger.info("Mapped Tags") }
        mapGenres(moviesDataset).also { exposedLogger.info("Mapped Genres") }
    }

    private fun mapGenres(moviesDataset: Map<MovieId, DatasetMovieRow>) {
        val movieGenres = moviesDataset.values.map { row ->
            row.genres.map { Pair(row.id, it.id) }
        }.flatten()

        MovieGenresTable.batchInsert(data = movieGenres) {
            this[MovieGenresTable.movieId] = it.first
            this[MovieGenresTable.genreId] = it.second
        }
    }

    private fun mapTags(moviesDataset: Map<MovieId, DatasetMovieRow>) {
        val movieTags = moviesDataset.values.map { row ->
            row.keywords.map { Pair(row.id, it.id) }
        }.flatten()

        MovieTagsTable.batchInsert(data = movieTags) {
            this[MovieTagsTable.movieId] = it.first
            this[MovieTagsTable.tagId] = it.second
        }
    }

    private fun mapMovieCharacters(creditsDataset: Map<MovieId, DatasetCreditRow>) {
        val characters = creditsDataset.values.map { row ->
            row.cast
                .sortedBy { it.order }.take(13) // For simplicity we took only main characters
                .map { Pair(row.movie_id, it) }
        }.flatten()

        CharactersTable.batchInsert(data = characters) {
            this[CharactersTable.movieId] = it.first
            this[CharactersTable.personId] = it.second.id
            this[CharactersTable.playedCharacter] = it.second.character
            this[CharactersTable.priorityOrder] = it.second.order
        }
    }

    private fun parseMovies(moviesDataset: Map<MovieId, DatasetMovieRow>) {
        MoviesTable.batchInsert(data = moviesDataset.values) {
            this[MoviesTable.id] = it.id
            this[MoviesTable.title] = it.title
            this[MoviesTable.originalTitle] = it.originalTitle
            this[MoviesTable.url] = it.homepage.ifBlank { null }?.split(" ")?.last() // Some films has 2 urls
            this[MoviesTable.tagline] = it.tagline.ifBlank { null }
            this[MoviesTable.description] = it.overview!!
            this[MoviesTable.releaseDate] = it.releaseDate
            this[MoviesTable.budgedUsd] = it.budget.takeIf { it > 0 }
            this[MoviesTable.revenueUsd] = it.revenue.takeIf { it > 0 }

            if (it.voteCount > 666) { // Just for realistic dataset we ignore rating if there is not enough votes
                this[MoviesTable.votesCount] = it.voteCount
                this[MoviesTable.rating] = it.voteAverage
            }
        }
    }

    private fun mapMovieComposers(creditsDataset: Map<Int, DatasetCreditRow>) {
        val composers = creditsDataset.values.map { row ->
            row.crew
                .filter { it.job == "Original Music Composer" }
                .ifEmpty { row.crew.filter { it.job == "Music" && it.department == "Sound" } }
                .map { Pair<MovieId, PersonId>(row.movie_id, it.id) }
        }.flatten()

        MovieComposerTable.batchInsert(data = composers) {
            this[MovieComposerTable.movieId] = it.first
            this[MovieComposerTable.personId] = it.second
        }
    }

    private fun mapMovieDirectors(creditsDataset: Map<Int, DatasetCreditRow>) {
        val directors = creditsDataset.values.map { row ->
            row.crew.filter { it.department == "Directing" && it.job == "Director" }
                .map { Pair<MovieId, PersonId>(row.movie_id, it.id) }
        }.flatten()
        MovieDirectorTable.batchInsert(data = directors) {
            this[MovieDirectorTable.movieId] = it.first
            this[MovieDirectorTable.personId] = it.second
        }
    }

    private fun parsePeople(creditsDataset: Map<PersonId, DatasetCreditRow>) {
        val users = creditsDataset.values.map { row ->
            val crew = row.crew.map { ParsedPerson(it.id, it.name, it.gender.toGender()) }
            val cast = row.cast.map { ParsedPerson(it.id, it.name, it.gender.toGender()) }
            crew + cast
        }.flatten().distinctBy { it.id }

        PeopleTable.batchInsert(data = users) {
            this[PeopleTable.id] = it.id
            this[PeopleTable.name] = it.name
            this[PeopleTable.gender] = it.gender
        }
    }

    private fun parseGenres(moviesDataset: Map<Id, DatasetMovieRow>) {
        val genres = moviesDataset.values.map { it.genres }
            .flatten()
            .distinctBy { it.id }
        GenresTable.batchInsert(data = genres) {
            this[GenresTable.id] = it.id
            this[GenresTable.name] = it.name
        }
    }

    private fun parseTags(moviesDataset: Map<Id, DatasetMovieRow>) {
        val tags = moviesDataset.values.map { it.keywords }
            .flatten()
            .distinctBy { it.id }

        TagsTable.batchInsert(data = tags) {
            this[TagsTable.id] = it.id
            this[TagsTable.name] = it.name
        }
    }

    private inline fun <reified T> readCsvFile(fileName: String): List<T> {
        this.javaClass.getResourceAsStream(fileName)!!.use {
            return csvMapper
                .readerFor(T::class.java)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues<T>(it)
                .readAll()
                .toList()
        }
    }

    private fun Int.toGender(): Gender? {
        return when (this) {
            0 -> null
            1 -> Gender.FEMALE
            2 -> Gender.MALE
            else -> error("Something unexpected")
        }
    }
}

private typealias Id = Int
private typealias MovieId = Int
private typealias PersonId = Int