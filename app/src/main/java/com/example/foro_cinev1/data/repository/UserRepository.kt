package com.example.foro_cinev1.data.repository

import com.example.foro_cinev1.data.remote.ApiClient
import com.example.foro_cinev1.data.remote.api.LoginRequest
import com.example.foro_cinev1.data.remote.api.RegisterRequest
import com.example.foro_cinev1.data.remote.api.UpdateUserRequest
import com.example.foro_cinev1.data.remote.api.UserApi
import com.example.foro_cinev1.domain.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val api: UserApi = ApiClient.userApi) {

    // -----------------------------------------------------------------------------------------
    // REGISTRAR USUARIO
    // -----------------------------------------------------------------------------------------
    suspend fun registerUser(user: User): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = RegisterRequest(
                nombre = user.nombre,
                correo = user.correo,
                contrasena = user.contrasena,
                ubicacion = user.ubicacion,
                profileImageUrl = user.profileImageUrl
            )

            val response = api.register(request)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // -----------------------------------------------------------------------------------------
    // LOGIN
    // -----------------------------------------------------------------------------------------
    suspend fun loginUser(correo: String, contrasena: String): User? = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = api.login(LoginRequest(correo, contrasena))
            if (!response.isSuccessful) return@withContext null

            val body = response.body() ?: return@withContext null

            User(
                id = body.id.toInt(),
                nombre = body.nombre,
                correo = body.correo,
                contrasena = contrasena,
                ubicacion = body.ubicacion,
                profileImageUrl = body.profileImageUrl
            )

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // -----------------------------------------------------------------------------------------
    // ACTUALIZAR PERFIL
    // -----------------------------------------------------------------------------------------
    suspend fun updateUserProfile(
        id: Long,
        nombre: String?,
        ubicacion: String?,
        profileImageUrl: String?
    ): User? = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = UpdateUserRequest(
                nombre = nombre,
                ubicacion = ubicacion,
                profileImageUrl = profileImageUrl
            )

            val response = api.updateUser(id, request)
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
