package com.example.foro_cinev1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.foro_cinev1.domain.models.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostViewModel(private val context: Context) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private var nextId = 1

    init {
        cargarPostsEjemplo()
    }

    private fun cargarPostsEjemplo() {
        _posts.value = listOf(
            Post(
                id = nextId++,
                titulo = "¿Qué opinan de Dune 2?",
                contenido = "Acabo de ver Dune 2 y quedé impresionado. La cinematografía es increíble y la actuación de Timothée Chalamet es fenomenal. ¿Qué les pareció a ustedes?",
                autor = "CinéfiloUno",
                fecha = "23/10/2025"
            ),
            Post(
                id = nextId++,
                titulo = "Recomendaciones de películas de terror",
                contenido = "Busco películas de terror que realmente den miedo, no las típicas de jump scares. ¿Alguna sugerencia?",
                autor = "TerrorFan",
                fecha = "22/10/2025"
            ),
            Post(
                id = nextId++,
                titulo = "Análisis: El uso del color en Wes Anderson",
                contenido = "Me fascina cómo Wes Anderson usa paletas de colores específicas para cada película. En 'The Grand Budapest Hotel' domina el rosa pastel, mientras que en 'Moonrise Kingdom' prevalecen los tonos tierra. ¿Alguien más ha notado estos patrones?",
                autor = "AnalistaVisual",
                fecha = "21/10/2025"
            )
        )
    }

    fun cargarPosts() {
        // Método para cargar posts desde el backend
    }

    fun agregarPost(post: Post) {
        val newPost = post.copy(id = nextId++)
        _posts.value = listOf(newPost) + _posts.value
    }

    fun eliminarPost(postId: Int) {
        _posts.value = _posts.value.filter { it.id != postId }
    }
}