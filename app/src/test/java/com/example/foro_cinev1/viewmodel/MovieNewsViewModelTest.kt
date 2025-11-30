package com.example.foro_cinev1.viewmodel

import com.example.foro_cinev1.data.api.models.Movie
import com.example.foro_cinev1.data.repository.MovieRepository
import com.example.foro_cinev1.domain.models.NewsType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class MovieNewsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockRepository: MovieRepository

    private lateinit var viewModel: MovieNewsViewModel

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
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarNoticiasReales genera lista correctamente combinando fuentes`() = runTest(testDispatcher) {
        // Given
        val movies = listOf(sampleMovie)
        `when`(mockRepository.getNowPlayingMovies()).thenReturn(Result.success(movies))
        `when`(mockRepository.getPopularMovies()).thenReturn(Result.success(movies))
        `when`(mockRepository.getTopRatedMovies()).thenReturn(Result.success(movies))

        // When
        // Instanciamos el ViewModel aquí para que el init se ejecute con los mocks ya configurados
        viewModel = MovieNewsViewModel(mockRepository)
        advanceUntilIdle()

        // Then
        val noticias = viewModel.noticias.value
        assertFalse(noticias.isEmpty())
        
        // Verificamos que haya noticias de diferentes tipos
        // 1 NowPlaying + 1 Popular + 1 TopRated + 1 Recommendation = 4 items esperados (si take(3) etc lo permiten)
        // Con 1 movie en mocks:
        // - NowPlaying: take(3) -> 1
        // - Popular: take(3) -> 1
        // - TopRated: take(2) -> 1
        // - Recommendation: 1 (usa popular movies)
        // Total: 4
        assertEquals(4, noticias.size)
        
        assertTrue(noticias.any { it.type == NewsType.NEW_RELEASE })
        assertTrue(noticias.any { it.type == NewsType.TRENDING })
        assertTrue(noticias.any { it.type == NewsType.TOP_RATED })
        assertTrue(noticias.any { it.type == NewsType.RECOMMENDATION })
    }

    @Test
    fun `cargarNoticiasReales maneja errores sin crashear`() = runTest(testDispatcher) {
        // Given
        `when`(mockRepository.getNowPlayingMovies()).thenReturn(Result.failure(Exception("Error")))
        // Los otros pueden fallar o no, probemos que falla todo
        `when`(mockRepository.getPopularMovies()).thenReturn(Result.failure(Exception("Error")))
        `when`(mockRepository.getTopRatedMovies()).thenReturn(Result.failure(Exception("Error")))

        // When
        viewModel = MovieNewsViewModel(mockRepository)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.noticias.value.isEmpty())
        // El mensaje de error se setea en el catch
        // Pero ojo: las llamadas son secuenciales en el código original?
        // En el código original son llamadas secuenciales repository.getX().onSuccess
        // Si una falla y no lanza excepción (Result.failure), el código sigue.
        // Pero el catch envuelve todo. 
        // Si getNowPlayingMovies retorna Result.failure, onSuccess no se ejecuta, pero no lanza excepcion fuera del Result.
        // Así que el bloque catch(e: Exception) del ViewModel solo captura excepciones reales de código,
        // no Result.failure si no se hace .getOrThrow().
        
        // Verificamos si se setea error message o simplemente lista vacía
        // En tu implementación: _errorMessage.value = "Error..." está en el catch.
        // Las llamadas a repository devuelven Result, que atrapa excepciones internas.
        // Así que si mock retorna Result.failure, simplemente no entra al onSuccess.
        
        assertTrue(viewModel.noticias.value.isEmpty())
    }

    @Test
    fun `cargarNoticiaPorId selecciona la noticia correcta`() = runTest(testDispatcher) {
        // Given
        val movies = listOf(sampleMovie)
        `when`(mockRepository.getNowPlayingMovies()).thenReturn(Result.success(movies))
        `when`(mockRepository.getPopularMovies()).thenReturn(Result.success(emptyList()))
        `when`(mockRepository.getTopRatedMovies()).thenReturn(Result.success(emptyList()))

        viewModel = MovieNewsViewModel(mockRepository)
        advanceUntilIdle()

        // El ID generado para NowPlaying es "now_${movie.id}" -> "now_1"
        val idBuscado = "now_1"

        // When
        viewModel.cargarNoticiaPorId(idBuscado)

        // Then
        assertNotNull(viewModel.noticiaSeleccionada.value)
        assertEquals(idBuscado, viewModel.noticiaSeleccionada.value?.id)
        assertEquals("Estrenos", viewModel.noticiaSeleccionada.value?.category)
    }
    
    @Test
    fun `cargarNoticiaPorId no selecciona nada si ID no existe`() = runTest(testDispatcher) {
         // Given
        val movies = listOf(sampleMovie)
        `when`(mockRepository.getNowPlayingMovies()).thenReturn(Result.success(movies))
        `when`(mockRepository.getPopularMovies()).thenReturn(Result.success(emptyList()))
        `when`(mockRepository.getTopRatedMovies()).thenReturn(Result.success(emptyList()))

        viewModel = MovieNewsViewModel(mockRepository)
        advanceUntilIdle()

        // When
        viewModel.cargarNoticiaPorId("id_falso")

        // Then
        assertNull(viewModel.noticiaSeleccionada.value)
    }
}
