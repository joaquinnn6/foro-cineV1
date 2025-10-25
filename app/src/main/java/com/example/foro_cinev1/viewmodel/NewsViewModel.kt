package com.example.foro_cinev1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foro_cinev1.data.database.DatabaseHelper
import com.example.foro_cinev1.domain.models.News
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NewsViewModel(private val context: Context) : ViewModel() {

    private val db = DatabaseHelper(context)

    private val _noticias = MutableStateFlow<List<News>>(emptyList())
    val noticias: StateFlow<List<News>> = _noticias

    private val _noticiaSeleccionada = MutableStateFlow<News?>(null)
    val noticiaSeleccionada: StateFlow<News?> = _noticiaSeleccionada

    fun cargarNoticias() {
        viewModelScope.launch {
            _noticias.value = db.getAllNews()
        }
    }

    fun cargarNoticiaPorId(id: Int) {
        viewModelScope.launch {
            _noticiaSeleccionada.value = db.getNewsById(id)
        }
    }

    // Para pruebas o datos iniciales
    fun agregarNoticiaDemo() {
        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val demo = News(
            titulo = "Nuevo estreno en cines",
            resumen = "Una de las películas más esperadas del año llega finalmente a la pantalla grande.",
            contenido = "La nueva película ha generado gran expectativa y promete romper récords de taquilla...",
            autor = "Admin CineVerse",
            fecha = fecha
        )
        db.insertNews(demo)
    }
}
