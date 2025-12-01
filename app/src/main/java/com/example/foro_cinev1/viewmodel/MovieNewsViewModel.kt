package com.example.foro_cinev1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foro_cinev1.data.repository.MovieRepository
import com.example.foro_cinev1.domain.models.MovieNewsItem
import com.example.foro_cinev1.domain.models.NewsType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MovieNewsViewModel : ViewModel() {

    private val repository = MovieRepository()

    private val _noticias = MutableStateFlow<List<MovieNewsItem>>(emptyList())
    val noticias: StateFlow<List<MovieNewsItem>> = _noticias

    private val _noticiaSeleccionada = MutableStateFlow<MovieNewsItem?>(null)
    val noticiaSeleccionada: StateFlow<MovieNewsItem?> = _noticiaSeleccionada

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        cargarNoticiasReales()
    }

    fun cargarNoticiasReales() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val allNews = mutableListOf<MovieNewsItem>()

            try {
                // 1. Películas en cines (NEW_RELEASE)
                repository.getNowPlayingMovies().onSuccess { nowPlaying ->
                    nowPlaying.take(3).forEach { movie ->
                        allNews.add(
                            MovieNewsItem(
                                id = "now_${movie.id}",
                                type = NewsType.NEW_RELEASE,
                                title = "¡${movie.title} ya está en cines!",
                                summary = "La esperada película llega a las salas. ${movie.overview.take(100)}...",
                                date = formatearFecha(movie.releaseDate),
                                movie = movie,
                                imageUrl = movie.getBackdropUrl(),
                                category = "Estrenos"
                            )
                        )
                    }
                }

                // 2. Películas populares (TRENDING)
                repository.getPopularMovies().onSuccess { popular ->
                    popular.take(3).forEach { movie ->
                        allNews.add(
                            MovieNewsItem(
                                id = "popular_${movie.id}",
                                type = NewsType.TRENDING,
                                title = "${movie.title} arrasa en taquilla",
                                summary = "La película se posiciona como la más vista. Con ${movie.voteCount} valoraciones y un puntaje de ${String.format("%.1f", movie.voteAverage)}/10.",
                                date = obtenerFechaReciente(),
                                movie = movie,
                                imageUrl = movie.getBackdropUrl(),
                                category = "Tendencias"
                            )
                        )
                    }
                }

                // 3. Mejor valoradas (TOP_RATED)
                repository.getTopRatedMovies().onSuccess { topRated ->
                    topRated.take(2).forEach { movie ->
                        allNews.add(
                            MovieNewsItem(
                                id = "top_${movie.id}",
                                type = NewsType.TOP_RATED,
                                title = "Crítica: ${movie.title} alcanza ${String.format("%.1f", movie.voteAverage)} de 10",
                                summary = "Los espectadores están encantados. ${movie.overview.take(120)}...",
                                date = obtenerFechaReciente(-1),
                                movie = movie,
                                imageUrl = movie.getPosterUrl(),
                                category = "Críticas"
                            )
                        )
                    }
                }

                // 4. Recomendaciones (múltiples películas)
                repository.getPopularMovies().onSuccess { movies ->
                    allNews.add(
                        MovieNewsItem(
                            id = "rec_001",
                            type = NewsType.RECOMMENDATION,
                            title = "Top 5: Las películas imperdibles de esta semana",
                            summary = "Nuestra selección de las mejores películas que no te puedes perder.",
                            date = obtenerFechaReciente(-2),
                            movies = movies.take(5),
                            imageUrl = movies.firstOrNull()?.getBackdropUrl(),
                            category = "Recomendaciones"
                        )
                    )
                }

                // Ordenar por fecha (más recientes primero)
                _noticias.value = allNews.sortedByDescending {
                    parseFecha(it.date)
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar noticias: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cargarNoticiaPorId(id: String) {
        println("🔍 DEBUG cargarNoticiaPorId: Buscando ID='$id'")
        println("🔍 DEBUG: Total noticias en memoria: ${_noticias.value.size}")

        val noticia = _noticias.value.find { it.id == id }

        if (noticia != null) {
            println("✅ DEBUG: Noticia encontrada: '${noticia.title}'")
            _noticiaSeleccionada.value = noticia
        } else {
            println("❌ DEBUG: Noticia NO encontrada con ID='$id'")
            println("❌ DEBUG: IDs disponibles: ${_noticias.value.map { it.id }}")

            // Intenta recargar las noticias si la lista está vacía
            if (_noticias.value.isEmpty()) {
                println("⚠️ DEBUG: Lista vacía, recargando noticias...")
                cargarNoticiasReales()
                // Espera un momento y vuelve a intentar
                viewModelScope.launch {
                    kotlinx.coroutines.delay(1000)
                    _noticiaSeleccionada.value = _noticias.value.find { it.id == id }
                }
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Helpers
    private fun formatearFecha(tmdbDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
            val date = inputFormat.parse(tmdbDate)
            date?.let { outputFormat.format(it) } ?: obtenerFechaReciente()
        } catch (e: Exception) {
            obtenerFechaReciente()
        }
    }

    private fun obtenerFechaReciente(diasAtras: Int = 0): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -diasAtras)
        val format = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
        return format.format(calendar.time)
    }

    private fun parseFecha(fecha: String): Long {
        return try {
            val format = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
            format.parse(fecha)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}