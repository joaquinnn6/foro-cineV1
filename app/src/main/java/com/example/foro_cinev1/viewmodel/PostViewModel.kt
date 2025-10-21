package com.example.foro_cinev1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foro_cinev1.data.repository.PostRepository
import com.example.foro_cinev1.domain.models.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel conecta la base de datos con la interfaz y mantiene los datos actualizados
class PostViewModel(context: Context) : ViewModel() {

    private val repo = PostRepository(context)

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    // Cargar todas las publicaciones
    fun cargarPosts() {
        viewModelScope.launch {
            _posts.value = repo.obtenerPosts()
        }
    }

    // Agregar un nuevo post
    fun agregarPost(post: Post) {
        viewModelScope.launch {
            repo.insertarPost(post)
            cargarPosts()
        }
    }

    // Eliminar un post por ID
    fun eliminarPost(id: Int) {
        viewModelScope.launch {
            repo.eliminarPost(id)
            cargarPosts()
        }
    }
}
