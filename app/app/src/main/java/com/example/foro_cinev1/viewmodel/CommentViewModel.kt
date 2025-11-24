package com.example.foro_cinev1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foro_cinev1.data.repository.CommentRepository
import com.example.foro_cinev1.domain.models.Comment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CommentViewModel(context: Context) : ViewModel() {
    private val repository = CommentRepository(context)

    private val _comentarios = MutableStateFlow<List<Comment>>(emptyList())
    val comentarios: StateFlow<List<Comment>> = _comentarios

    fun cargarComentarios(postId: Int) {
        viewModelScope.launch {
            _comentarios.value = repository.obtenerComentarios(postId)
        }
    }

    fun agregarComentario(comment: Comment) {
        viewModelScope.launch {
            repository.agregarComentario(comment)
            cargarComentarios(comment.postId)
        }
    }

    fun eliminarComentario(id: Int, postId: Int) {
        viewModelScope.launch {
            repository.eliminarComentario(id)
            cargarComentarios(postId)
        }
    }
}
