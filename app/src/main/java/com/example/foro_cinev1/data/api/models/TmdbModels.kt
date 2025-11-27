package com.example.foro_cinev1.data.api.models

import com.google.gson.annotations.SerializedName

// Respuesta de lista de películas
data class MovieResponse(
    val page: Int,
    val results: List<Movie>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

// Modelo de película
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    val popularity: Double
) {
    fun getPosterUrl() = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
    fun getBackdropUrl() = backdropPath?.let { "https://image.tmdb.org/t/p/w1280$it" }
}

// Detalle completo de película
data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    val runtime: Int?,
    val genres: List<Genre>,
    @SerializedName("vote_average")
    val voteAverage: Double,
    val budget: Long,
    val revenue: Long,
    val tagline: String?
) {
    fun getPosterUrl() = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
    fun getBackdropUrl() = backdropPath?.let { "https://image.tmdb.org/t/p/w1280$it" }
    fun getFormattedRuntime() = runtime?.let { "${it / 60}h ${it % 60}min" } ?: "N/A"
}

data class Genre(
    val id: Int,
    val name: String
)