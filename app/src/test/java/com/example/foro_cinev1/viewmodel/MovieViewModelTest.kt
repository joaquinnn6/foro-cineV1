package com.example.foro_cinev1.viewmodel

import com.example.foro_cinev1.data.api.models.Movie
import com.example.foro_cinev1.data.api.models.MovieDetail
import com.example.foro_cinev1.data.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class MovieViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockRepository: MovieRepository

    private lateinit var viewModel: MovieViewModel

    private val sampleMovie = Movie(
        id = 1,
        title = "Test Movie",
        overview = "Overview",
        posterPath = "/poster.jpg",
        backdropPath = "/back.jpg",
        releaseDate = "2025-01-01",
        voteAverage = 8.0,
        voteCount = 100,
        popularity = 50.0
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        // Definimos el comportamiento por defecto para la llamada del init.
        // Usamos runBlocking para asegurar que el stubbing de la suspen function se registre correctamente
        // antes de instanciar el ViewModel.
        runBlocking {
            `when`(mockRepository.getPopularMovies()).thenReturn(Result.success(emptyList()))
        }
        
        viewModel = MovieViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- POPULAR MOVIES ---

    @Test
    fun `loadPopularMovies actualiza estado con peliculas`() = runTest(testDispatcher) {
        // Given
        val movies = listOf(sampleMovie)
        `when`(mockRepository.getPopularMovies()).thenReturn(Result.success(movies))

        // When
        viewModel.loadPopularMovies()
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.popularMovies.value.size)
        assertEquals("Test Movie", viewModel.popularMovies.value.first().title)
        assertFalse(viewModel.isLoading.value)
        // Verificamos que se llamó 2 veces: una por el init y otra por la llamada explícita
        verify(mockRepository, times(2)).getPopularMovies()
    }

    @Test
    fun `loadPopularMovies maneja error`() = runTest(testDispatcher) {
        // Given
        `when`(mockRepository.getPopularMovies()).thenReturn(Result.failure(Exception("Error de red")))

        // When
        viewModel.loadPopularMovies()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.popularMovies.value.isEmpty())
        assertEquals("Error de red", viewModel.errorMessage.value)
        assertFalse(viewModel.isLoading.value)
    }

    // --- NOW PLAYING MOVIES (Nuevos Tests) ---

    @Test
    fun `loadNowPlayingMovies actualiza estado con peliculas`() = runTest(testDispatcher) {
        // Given
        val movies = listOf(sampleMovie.copy(id = 2, title = "Now Playing"))
        `when`(mockRepository.getNowPlayingMovies()).thenReturn(Result.success(movies))

        // When
        viewModel.loadNowPlayingMovies()
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.nowPlayingMovies.value.size)
        assertEquals("Now Playing", viewModel.nowPlayingMovies.value.first().title)
        verify(mockRepository).getNowPlayingMovies()
    }

    @Test
    fun `loadNowPlayingMovies maneja error`() = runTest(testDispatcher) {
        // Given
        `when`(mockRepository.getNowPlayingMovies()).thenReturn(Result.failure(Exception("Error loading now playing")))

        // When
        viewModel.loadNowPlayingMovies()
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.nowPlayingMovies.value.isEmpty())
        assertEquals("Error loading now playing", viewModel.errorMessage.value)
    }

    // --- TOP RATED MOVIES (Nuevos Tests) ---

    @Test
    fun `loadTopRatedMovies actualiza estado con peliculas`() = runTest(testDispatcher) {
        // Given
        val movies = listOf(sampleMovie.copy(id = 3, title = "Top Rated"))
        `when`(mockRepository.getTopRatedMovies()).thenReturn(Result.success(movies))

        // When
        viewModel.loadTopRatedMovies()
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.topRatedMovies.value.size)
        assertEquals("Top Rated", viewModel.topRatedMovies.value.first().title)
        verify(mockRepository).getTopRatedMovies()
    }

    // --- SEARCH MOVIES ---

    @Test
    fun `searchMovies actualiza resultados de busqueda`() = runTest(testDispatcher) {
        // Given
        val query = "Test"
        val movies = listOf(sampleMovie)
        `when`(mockRepository.searchMovies(query)).thenReturn(Result.success(movies))

        // When
        viewModel.searchMovies(query)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.searchResults.value.size)
        assertEquals("Test Movie", viewModel.searchResults.value.first().title)
        verify(mockRepository).searchMovies(query)
    }

    @Test
    fun `searchMovies con query vacio limpia resultados`() = runTest(testDispatcher) {
        // When
        viewModel.searchMovies("")
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.searchResults.value.isEmpty())
    }

    // --- MOVIE DETAIL ---

    @Test
    fun `loadMovieDetail actualiza estado con detalle`() = runTest(testDispatcher) {
        // Given
        val detail = MovieDetail(
            id = 1,
            title = "Detail Title",
            overview = "Overview",
            posterPath = null,
            backdropPath = null,
            releaseDate = "2025",
            runtime = 120,
            genres = emptyList(),
            voteAverage = 9.0,
            budget = 1000,
            revenue = 2000,
            tagline = "Tagline"
        )
        `when`(mockRepository.getMovieDetail(1)).thenReturn(Result.success(detail))

        // When
        viewModel.loadMovieDetail(1)
        advanceUntilIdle()

        // Then
        assertNotNull(viewModel.movieDetail.value)
        assertEquals("Detail Title", viewModel.movieDetail.value?.title)
        verify(mockRepository).getMovieDetail(1)
    }

    @Test
    fun `loadMovieDetail maneja error`() = runTest(testDispatcher) {
        // Given
        `when`(mockRepository.getMovieDetail(1)).thenReturn(Result.failure(Exception("Detail Error")))

        // When
        viewModel.loadMovieDetail(1)
        advanceUntilIdle()

        // Then
        // El detalle no debería cambiar (o ser null si era el inicial)
        assertNull(viewModel.movieDetail.value)
        assertEquals("Detail Error", viewModel.errorMessage.value)
    }
}
