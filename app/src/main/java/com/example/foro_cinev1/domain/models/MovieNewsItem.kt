package com.example.foro_cinev1.domain.models

import com.example.foro_cinev1.data.api.models.Movie

data class MovieNewsItem(
    val id: String,
    val type: NewsType,
    val title: String,
    val summary: String,
    val date: String,
    val movie: Movie? = null,
    val movies: List<Movie>? = null,
    val imageUrl: String? = null,
    val category: String
)

enum class NewsType {
    NEW_RELEASE,      // Película recién estrenada
    UPCOMING,         // Próximo estreno
    TRENDING,         // Tendencia del momento
    TOP_RATED,        // Mejor valorada recientemente
    RECOMMENDATION    // Recomendación personalizada
}