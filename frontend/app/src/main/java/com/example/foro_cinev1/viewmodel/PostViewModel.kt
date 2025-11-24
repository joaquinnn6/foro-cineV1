package com.example.foro_cinev1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foro_cinev1.data.database.DatabaseHelper
import com.example.foro_cinev1.domain.models.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PostViewModel(context: Context) : ViewModel() {

    private val db = DatabaseHelper(context)

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _comentarios = MutableStateFlow<List<Map<String, String>>>(emptyList())
    val comentarios: StateFlow<List<Map<String, String>>> = _comentarios

    /** Cargar todas las publicaciones desde SQLite */
    fun cargarPosts() {
        viewModelScope.launch {
            _posts.value = db.getAllPosts()
        }
    }

    /** Agregar un nuevo post */
    fun agregarPost(post: Post) {
        viewModelScope.launch {
            db.insertPost(post)
            cargarPosts()
        }
    }

    /** Eliminar un post por id */
    fun eliminarPost(id: Int) {
        viewModelScope.launch {
            db.deletePost(id)
            cargarPosts()
        }
    }

    /**
     * 🔥 Dar like o dislike con restricción por usuario
     * @param postId ID del post
     * @param userId ID del usuario (de SessionManager)
     * @param like true = like, false = dislike
     */
    fun votarPost(postId: Int, userId: Int, like: Boolean) {
        viewModelScope.launch {
            val voto = if (like) 1 else -1
            val exito = db.votarPost(userId, postId, voto)
            if (exito) {
                cargarPosts() // refresca la lista con los contadores actualizados
            }
        }
    }

    /** Agregar comentario a un post */
    fun agregarComentario(postId: Int, autor: String, contenido: String) {
        viewModelScope.launch {
            val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            db.insertComment(postId, autor, contenido, fecha)
            cargarComentarios(postId)
        }
    }

    /** Cargar comentarios de un post */
    fun cargarComentarios(postId: Int) {
        viewModelScope.launch {
            _comentarios.value = db.getCommentsByPost(postId)
        }
    }
}
