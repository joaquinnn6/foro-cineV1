package com.example.foro_cinev1

import com.example.foro_cinev1.data.api.models.Movie
import com.example.foro_cinev1.data.api.models.MovieDetail
import com.example.foro_cinev1.data.api.models.Genre
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

class TmdbModelsTest {

    @Test
    fun movie_getPosterUrl_devuelveUrlCompleta_cuandoPosterNoEsNulo() {
        val movie = Movie(
            id = 1,
            title = "Película de prueba",
            overview = "Overview",
            posterPath = "/imagePoster.jpg",
            backdropPath = null,
            releaseDate = "2024-01-01",
            voteAverage = 8.5,
            voteCount = 100,
            popularity = 10.0
        )

        val url = movie.getPosterUrl()

        assertEquals("https://image.tmdb.org/t/p/w500/imagePoster.jpg", url)
    }

    @Test
    fun movie_getPosterUrl_devuelveNull_cuandoPosterEsNulo() {
        val movie = Movie(
            id = 1,
            title = "Película de prueba",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            releaseDate = "2024-01-01",
            voteAverage = 8.5,
            voteCount = 100,
            popularity = 10.0
        )

        val url = movie.getPosterUrl()

        assertNull(url)
    }

    @Test
    fun movie_getBackdropUrl_devuelveUrlCompleta_cuandoBackdropNoEsNulo() {
        val movie = Movie(
            id = 1,
            title = "Película de prueba",
            overview = "Overview",
            posterPath = null,
            backdropPath = "/imageBackdrop.jpg",
            releaseDate = "2024-01-01",
            voteAverage = 8.5,
            voteCount = 100,
            popularity = 10.0
        )

        val url = movie.getBackdropUrl()

        assertEquals("https://image.tmdb.org/t/p/w1280/imageBackdrop.jpg", url)
    }

    @Test
    fun movieDetail_getFormattedRuntime_formateaMinutosA_HorasYMinutos() {
        val detail = MovieDetail(
            id = 1,
            title = "Película de prueba",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            releaseDate = "2024-01-01",
            runtime = 125,
            genres = listOf(Genre(1, "Acción")),
            voteAverage = 8.5,
            budget = 1_000_000,
            revenue = 2_000_000,
            tagline = null
        )

        val formatted = detail.getFormattedRuntime()

        assertEquals("2h 5min", formatted)
    }

    @Test
    fun movieDetail_getFormattedRuntime_devuelveNA_cuandoRuntimeEsNulo() {
        val detail = MovieDetail(
            id = 1,
            title = "Película de prueba",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            releaseDate = "2024-01-01",
            runtime = null,
            genres = emptyList(),
            voteAverage = 8.5,
            budget = 1_000_000,
            revenue = 2_000_000,
            tagline = null
        )

        val formatted = detail.getFormattedRuntime()

        assertEquals("N/A", formatted)
    }
}
