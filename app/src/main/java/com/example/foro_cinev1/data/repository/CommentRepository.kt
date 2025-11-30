package com.example.foro_cinev1.data.repository

import com.example.foro_cinev1.data.remote.ApiClient
import com.example.foro_cinev1.data.remote.api.CommentApi
import com.example.foro_cinev1.data.remote.api.CreateCommentRequest
import com.example.foro_cinev1.domain.models.Comment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CommentRepository(
    private val api: CommentApi = ApiClient.commentApi
) {

    suspend fun obtenerComentarios(postId: Int): List<Comment> = withContext(Dispatchers.IO) {
        try {
            val response = api.getCommentsByPost(postId.toLong())
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun agregarComentario(comment: Comment): Boolean = withContext(Dispatchers.IO) {
        try {
            val fechaFinal = comment.fecha.ifBlank {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            }

            val request = CreateCommentRequest(
                autor = comment.autor,
                contenido = comment.contenido,
                fecha = fechaFinal
            )

            val response = api.addComment(comment.postId.toLong(), request)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun eliminarComentario(id: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteComment(id.toLong())
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
