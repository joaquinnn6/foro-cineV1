package com.example.foro_cinev1.data.remote

import com.example.foro_cinev1.data.remote.api.CommentApi
import com.example.foro_cinev1.data.remote.api.PostApi
import com.example.foro_cinev1.data.remote.api.UserApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "https://foro-cine-backend.onrender.com/api/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userApi: UserApi = retrofit.create(UserApi::class.java)
    val postApi: PostApi = retrofit.create(PostApi::class.java)
    val commentApi: CommentApi = retrofit.create(CommentApi::class.java)
}
