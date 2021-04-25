package com.volodya.moviedb.dataset

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.volodya.moviedb.people.Gender
import java.time.LocalDate

class DatasetCreditRow(
    val movie_id: Int,
    val title: String,
    @JsonDeserialize(using = ListDatasetCastItemDeserializer::class)
    val cast: List<DatasetCastItem>,
    @JsonDeserialize(using = ListDatasetCrewItemDeserializer::class)
    val crew: List<DatasetCrewItem>,
)

class DatasetMovieRow(
    val id: Int,
    val budget: Long,
    @JsonDeserialize(using = ListDatasetDictItemDeserializer::class)
    val genres: List<DatasetDictItem>,
    val homepage: String,
    @JsonDeserialize(using = ListDatasetDictItemDeserializer::class)
    val keywords: List<DatasetDictItem>,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String?,
    val popularity: String,
    val productionCompanies: String,
    val productionCountries: String,
    val releaseDate: LocalDate?,
    val revenue: Long,
    val runtime: String,
    val spokenLanguages: String,
    val status: String,
    val tagline: String,
    val title: String,
    val voteAverage: Double,
    val voteCount: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
class DatasetCastItem(
    val id: Int,
    val name: String,
    val castId: Int,
    val character: String,
    val gender: Int,
    val order: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
class DatasetCrewItem(
    val id: Int,
    val name: String,
    val department: String,
    val gender: Int,
    val job: String,
)

data class DatasetDictItem(
    val id: Int,
    val name: String,
)

data class ParsedPerson(
    val id: Int,
    val name: String,
    val gender: Gender?
)
