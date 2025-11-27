package com.example.foro_cinev1.data.remote.api

import com.example.foro_cinev1.domain.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class RegisterRequest(
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val ubicacion: String? = null,
    val profileImageUrl: String? = null,
    val role: String = "USUARIO"
)

data class LoginRequest(
    val correo: String,
    // 👇 CAMBIA ESTO: antes era "contraseña"
    val contrasena: String
)

data class LoginResponse(
    val id: Long,
    val nombre: String,
    val correo: String,
    val ubicacion: String?,
    val profileImageUrl: String?,
    val role: String
)

// DTO para update
data class UpdateUserRequest(
    val nombre: String? = null,
    val ubicacion: String? = null,
    val profileImageUrl: String? = null
)

interface UserApi {

    @POST("users")
    suspend fun register(@Body request: RegisterRequest): Response<User>

    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Long): Response<User>

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body body: UpdateUserRequest
    ): Response<User>
}
