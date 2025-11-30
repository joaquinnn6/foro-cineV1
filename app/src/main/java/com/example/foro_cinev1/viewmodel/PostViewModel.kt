package com.example.foro_cinev1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foro_cinev1.data.repository.PostRepository
import com.example.foro_cinev1.domain.models.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel(
    private val repository: PostRepository = PostRepository()
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    fun cargarPosts() {
        viewModelScope.launch {
            _posts.value = repository.getPosts()
        }
    }

    fun agregarPost(post: Post) {
        viewModelScope.launch {
            val exito = repository.createPost(post)
            if (exito) {
                cargarPosts()
            }
        }
    }

    private val _deleteError = MutableStateFlow(false)
    val deleteError: StateFlow<Boolean> = _deleteError
    fun eliminarPost(id: Int, userId: Int) {
        viewModelScope.launch {
            val exito = repository.deletePost(id, userId)
            if (exito) {
                cargarPosts()
            } else {
                _deleteError.value = true
            }
        }
    }

    fun clearDeleteError() {
        _deleteError.value = false
    }

    fun votarPost(postId: Int, userId: Int, like: Boolean) {
        viewModelScope.launch {
            val postActualizado = repository.votePost(postId, userId, like)
            if (postActualizado != null) {
                // Reemplazamos solo ese post en la lista
                _posts.value = _posts.value.map { post ->
                    if (post.id == postActualizado.id.toInt()) {
                        postActualizado
                    } else {
                        post
                    }
                }
            } else {
                // Si quieres, aquí podrías manejar error de voto (snackbar, etc)
            }
        }
    }
}
