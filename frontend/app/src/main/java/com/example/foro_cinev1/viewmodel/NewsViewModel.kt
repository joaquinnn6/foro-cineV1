package com.example.foro_cinev1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {

    private val _noticias = MutableStateFlow<List<NewsItem>>(emptyList())
    val noticias: StateFlow<List<NewsItem>> = _noticias

    // Estado para la noticia individual que se ve en la pantalla de detalle
    private val _noticiaSeleccionada = MutableStateFlow<NewsItem?>(null)
    val noticiaSeleccionada: StateFlow<NewsItem?> = _noticiaSeleccionada

    private val noticiasDeEjemplo = listOf(
        NewsItem(1, "Nuevo tráiler de Dune 3", "Denis Villeneuve confirma...", "23 Oct 2025", "Estrenos"),
        NewsItem(2, "Oscar 2026: Nominaciones", "La ceremonia se llevará a cabo...", "22 Oct 2025", "Premios"),
        NewsItem(3, "Entrevista con Christopher Nolan", "El director habla sobre su próximo...", "21 Oct 2025", "Entrevistas"),
        NewsItem(4, "Top 10 películas del mes", "Las más vistas en streaming...", "20 Oct 2025", "Rankings"),
        NewsItem(5, "Marvel anuncia Fase 6", "Nuevos títulos confirmados para...", "19 Oct 2025", "Estrenos")
    )

    init {
        // Cargamos la lista principal una vez, cuando el ViewModel se crea
        cargarNoticias()
    }

    private fun cargarNoticias() {
        viewModelScope.launch {
            if (_noticias.value.isEmpty()) {
                _noticias.value = noticiasDeEjemplo
            }
        }
    }

    // Función para que la pantalla de detalle pida una noticia por su ID
    fun cargarNoticiaPorId(id: Int) {
        _noticiaSeleccionada.value = _noticias.value.find { it.id == id }
    }
    
    fun toggleFavorite(newsId: Int) {
        val currentList = _noticias.value.map {
            if (it.id == newsId) {
                it.copy(isFavorite = !it.isFavorite)
            } else {
                it
            }
        }
        _noticias.value = currentList
        // Actualizamos también la noticia seleccionada si es la que cambió
        if (_noticiaSeleccionada.value?.id == newsId) {
            _noticiaSeleccionada.value = _noticiaSeleccionada.value?.copy(isFavorite = !_noticiaSeleccionada.value!!.isFavorite)
        }
    }
}