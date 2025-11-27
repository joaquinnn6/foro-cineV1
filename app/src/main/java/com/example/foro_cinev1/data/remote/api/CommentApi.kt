package com.example.foro_cinev1.data.remote.api

import com.example.foro_cinev1.domain.models.Comment
import retrofit2.Response
import retrofit2.http.*

data class CreateCommentRequest(
    val autor: String,
    val contenido: String,
    val fecha: String
)

interface CommentApi {

    @GET("posts/{postId}/comments")
    suspend fun getCommentsByPost(
        @Path("postId") postId: Long
    ): Response<List<Comment>>

    @POST("posts/{postId}/comments")
    suspend fun addComment(
        @Path("postId") postId: Long,
        @Body request: CreateCommentRequest
    ): Response<Comment>

    @DELETE("comments/{id}")
    suspend fun deleteComment(
        @Path("id") id: Long
    ): Response<Void>
}
