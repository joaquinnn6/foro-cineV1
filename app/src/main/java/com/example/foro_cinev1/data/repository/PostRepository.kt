package com.example.foro_cinev1.data.repository

import com.example.foro_cinev1.data.remote.ApiClient
import com.example.foro_cinev1.data.remote.api.CreatePostRequest
import com.example.foro_cinev1.domain.models.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepository {

    private val api = ApiClient.postApi

    suspend fun getPosts(): List<Post> = withContext(Dispatchers.IO) {
        try {
            val response = api.getPosts()
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

    suspend fun createPost(post: Post): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = CreatePostRequest(
                titulo = post.titulo,
                contenido = post.contenido,
                autor = post.autor,
                userId = post.userId,   // 👈 AHORA SÍ LO MANDAMOS
                fecha = post.fecha
            )
            val response = api.createPost(request)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deletePost(id: Int, userId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = api.deletePost(id.toLong(), userId.toLong())
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun votePost(postId: Int, userId: Int, like: Boolean): Post? =
        withContext(Dispatchers.IO) {
            try {
                val vote = if (like) 1 else -1
                val response = api.votePost(
                    postId = postId.toLong(),
                    userId = userId.toLong(),
                    vote = vote
                )

                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
}
