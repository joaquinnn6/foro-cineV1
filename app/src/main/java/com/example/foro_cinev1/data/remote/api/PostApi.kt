package com.example.foro_cinev1.data.remote.api

import com.example.foro_cinev1.domain.models.Post
import retrofit2.Response
import retrofit2.http.*

data class CreatePostRequest(
    val titulo: String,
    val contenido: String,
    val autor: String,
    val userId: Int,
    val fecha: String
)

interface PostApi {

    @GET("posts")
    suspend fun getPosts(): Response<List<Post>>

    @POST("posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<Post>

    @DELETE("posts/{id}")
    suspend fun deletePost(
        @Path("id") id: Long,
        @Query("userId") userId: Long
    ): Response<Void>

    // vote = 1 (like), -1 (dislike), 0 (quitar voto)
    @POST("posts/{id}/vote")
    suspend fun votePost(
        @Path("id") postId: Long,
        @Query("userId") userId: Long,
        @Query("vote") vote: Int
    ): Response<Post>
}
