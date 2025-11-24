package com.example.foro_cinev1.data.repository

import android.content.Context
import com.example.foro_cinev1.data.database.DatabaseHelper
import com.example.foro_cinev1.domain.models.Post

/**
 * Repositorio que actúa como capa intermedia entre la base de datos (SQLite)
 * y el ViewModel, aislando el acceso directo a DatabaseHelper.
 */
class PostRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // === POSTS ===
    fun obtenerPosts(): List<Post> {
        return dbHelper.getAllPosts()
    }

    fun agregarPost(post: Post) {
        dbHelper.insertPost(post)
    }

    fun eliminarPost(id: Int) {
        dbHelper.deletePost(id)
    }

    /**
     * 🔥 Sistema de votación (like/dislike)
     * @param userId ID del usuario (obtenido del SessionManager)
     * @param postId ID de la publicación
     * @param esLike true para like, false para dislike
     */
    fun votarPost(userId: Int, postId: Int, esLike: Boolean): Boolean {
        val voto = if (esLike) 1 else -1
        return dbHelper.votarPost(userId, postId, voto)
    }

    // === COMENTARIOS ===
    fun agregarComentario(postId: Int, autor: String, contenido: String, fecha: String) {
        dbHelper.insertComment(postId, autor, contenido, fecha)
    }

    fun obtenerComentarios(postId: Int): List<Map<String, String>> {
        return dbHelper.getCommentsByPost(postId)
    }
}
