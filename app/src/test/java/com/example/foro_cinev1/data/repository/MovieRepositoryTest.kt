package com.example.foro_cinev1.data.repository

import com.example.foro_cinev1.data.api.TmdbApiService
import com.example.foro_cinev1.data.api.models.Movie
import com.example.foro_cinev1.data.api.models.MovieDetail
import com.example.foro_cinev1.data.api.models.MovieResponse
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import retrofit2.Response

class MovieRepositoryTest {

    @Mock
    private lateinit var mockApiService: TmdbApiService

    private lateinit var repository: MovieRepository

    private val sampleMovie = Movie(
        id = 1,
        title = "Test Movie",
        overview = "Test overview",
        posterPath = "/test.jpg",
        backdropPath = "/backdrop.jpg",
        releaseDate = "2025-11-30",
        voteAverage = 8.5,
        voteCount = 1000,
        popularity = 100.0
    )

    // --- HELPERS para evitar NPE con Mockito en Kotlin ---
    // Como en este caso usamos valores String/Int primitivos en el API Service,
    // anyString() de Mockito devuelve NULL y explota en Kotlin.
    // Lo mejor es usar any() casteado o helpers específicos que devuelvan valores no nulos por defecto.
    
    private fun anyString(): String = Mockito.anyString() ?: ""
    private fun anyInt(): Int = Mockito.anyInt()

    // O MEJOR AÚN: Usaremos valores explícitos en los tests para evitar líos con matchers.
    // El repositorio usa internamente una API KEY constante.
    // Pero como no sabemos su valor exacto desde aquí (es private val en repo), usaremos any() con cuidado.
    // El repositorio hace: apiService.getPopularMovies(apiKey)
    // apiKey es String no nulo.
    
    private fun <T> any(type: Class<T>): T = Mockito.any(type)

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = MovieRepository(mockApiService)
    }

    @Test
    fun `getPopularMovies devuelve lista exitosamente`() {
        runBlocking {
            // Given
            val movieResponse = MovieResponse(
                page = 1,
                results = listOf(sampleMovie),
                totalPages = 1,
                totalResults = 1
            )

            // Usamos matchers de Mockito pero asegurando que no retornen null para Kotlin.
            // getPopularMovies(apiKey, language, page)
            // apiKey es String, language es String, page es Int.
            `when`(mockApiService.getPopularMovies(anyString(), anyString(), anyInt()))
                .thenReturn(Response.success(movieResponse))

            // When
            val result = repository.getPopularMovies()

            // Then
            assertTrue(result.isSuccess)
            assertEquals(1, result.getOrNull()?.size)
            assertEquals("Test Movie", result.getOrNull()?.first()?.title)
        }
    }

    @Test
    fun `getPopularMovies maneja error de respuesta`() {
        runBlocking {
            // Given
            `when`(mockApiService.getPopularMovies(anyString(), anyString(), anyInt()))
                .thenReturn(Response.error(404, mock(ResponseBody::class.java)))

            // When
            val result = repository.getPopularMovies()

            // Then
            assertTrue(result.isFailure)
        }
    }

    @Test
    fun `getNowPlayingMovies devuelve lista exitosamente`() {
        runBlocking {
            // Given
            val movieResponse = MovieResponse(
                page = 1,
                results = listOf(sampleMovie),
                totalPages = 1,
                totalResults = 1
            )

            `when`(mockApiService.getNowPlayingMovies(anyString(), anyString(), anyInt()))
                .thenReturn(Response.success(movieResponse))

            // When
            val result = repository.getNowPlayingMovies()

            // Then
            assertTrue(result.isSuccess)
            assertEquals(1, result.getOrNull()?.size)
        }
    }

    @Test
    fun `getTopRatedMovies devuelve lista exitosamente`() {
        runBlocking {
            // Given
            val movieResponse = MovieResponse(
                page = 1,
                results = listOf(sampleMovie, sampleMovie.copy(id = 2, title = "Movie 2")),
                totalPages = 1,
                totalResults = 2
            )

            `when`(mockApiService.getTopRatedMovies(anyString(), anyString(), anyInt()))
                .thenReturn(Response.success(movieResponse))

            // When
            val result = repository.getTopRatedMovies()

            // Then
            assertTrue(result.isSuccess)
            assertEquals(2, result.getOrNull()?.size)
        }
    }

    @Test
    fun `searchMovies devuelve resultados correctos`() {
        runBlocking {
            // Given
            val query = "Test"
            val movieResponse = MovieResponse(
                page = 1,
                results = listOf(sampleMovie),
                totalPages = 1,
                totalResults = 1
            )

            // searchMovies toma: apiKey, query, language, page
            `when`(mockApiService.searchMovies(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(Response.success(movieResponse))

            // When
            val result = repository.searchMovies(query)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(1, result.getOrNull()?.size)
            assertEquals("Test Movie", result.getOrNull()?.first()?.title)
        }
    }

    @Test
    fun `getMovieDetail devuelve detalles exitosamente`() {
        runBlocking {
            // Given
            val movieId = 1
            val movieDetail = MovieDetail(
                id = movieId,
                title = "Test Movie",
                overview = "Detailed overview",
                posterPath = "/poster.jpg",
                backdropPath = "/back.jpg",
                releaseDate = "2025-01-01",
                runtime = 120,
                genres = emptyList(),
                voteAverage = 9.0,
                budget = 1000000L,
                revenue = 5000000L,
                tagline = "Awesome movie"
            )

            // getMovieDetail toma: movieId (Int), apiKey (String), language (String)
            // Es importante usar anyInt() para el ID porque viene como argumento, 
            // pero anyString() para la API Key y Language.
            `when`(mockApiService.getMovieDetail(anyInt(), anyString(), anyString()))
                .thenReturn(Response.success(movieDetail))

            // When
            val result = repository.getMovieDetail(movieId)

            // Then
            assertTrue(result.isSuccess)
            val detail = result.getOrNull()
            assertNotNull(detail)
            assertEquals("Test Movie", detail?.title)
            assertEquals(120, detail?.runtime)
        }
    }
}
