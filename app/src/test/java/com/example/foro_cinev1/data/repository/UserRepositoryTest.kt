package com.example.foro_cinev1.data.repository

import com.example.foro_cinev1.data.remote.api.LoginRequest
import com.example.foro_cinev1.data.remote.api.LoginResponse
import com.example.foro_cinev1.data.remote.api.RegisterRequest
import com.example.foro_cinev1.data.remote.api.UpdateUserRequest
import com.example.foro_cinev1.data.remote.api.UserApi
import com.example.foro_cinev1.domain.models.User
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import retrofit2.Response

class UserRepositoryTest {

    @Mock
    private lateinit var mockApi: UserApi

    private lateinit var repository: UserRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        // Inyectamos el mockApi en el repositorio
        repository = UserRepository(mockApi)
    }

    @Test
    fun `registerUser devuelve true cuando el registro es exitoso`() {
        runBlocking {
            // Given
            val user = User(
                id = 0,
                nombre = "Test User",
                correo = "test@example.com",
                contrasena = "Password123",
                ubicacion = "Test City"
            )

            // El objeto request debe coincidir con el que crea el repositorio internamente
            val registerRequest = RegisterRequest(
                nombre = user.nombre,
                correo = user.correo,
                contrasena = user.contrasena,
                ubicacion = user.ubicacion,
                profileImageUrl = user.profileImageUrl // null
            )

            // Usamos el objeto real en lugar de any()
            `when`(mockApi.register(registerRequest))
                .thenReturn(Response.success(user))

            // When
            val result = repository.registerUser(user)

            // Then
            assertTrue(result)
            // Verificamos con el objeto real
            verify(mockApi).register(registerRequest)
        }
    }

    @Test
    fun `registerUser devuelve false cuando falla el registro`() {
        runBlocking {
            // Given
            val user = User(
                id = 0,
                nombre = "Test User",
                correo = "test@example.com",
                contrasena = "Password123"
            )

            val registerRequest = RegisterRequest(
                nombre = user.nombre,
                correo = user.correo,
                contrasena = user.contrasena,
                ubicacion = user.ubicacion,
                profileImageUrl = user.profileImageUrl
            )

            // Usamos el objeto real en lugar de any()
            `when`(mockApi.register(registerRequest))
                .thenReturn(Response.error(400, mock(ResponseBody::class.java)))

            // When
            val result = repository.registerUser(user)

            // Then
            assertFalse(result)
        }
    }

    @Test
    fun `loginUser devuelve User cuando las credenciales son correctas`() {
        runBlocking {
            // Given
            val correo = "test@example.com"
            val contrasena = "Password123"
            val loginResponse = LoginResponse(
                id = 1L,
                nombre = "Test User",
                correo = correo,
                ubicacion = "Test City",
                profileImageUrl = null,
                role = "USER"
            )

            `when`(mockApi.login(LoginRequest(correo, contrasena)))
                .thenReturn(Response.success(loginResponse))

            // When
            val result = repository.loginUser(correo, contrasena)

            // Then
            assertNotNull(result)
            assertEquals(correo, result?.correo)
            assertEquals("Test User", result?.nombre)
        }
    }

    @Test
    fun `loginUser devuelve null cuando las credenciales son incorrectas`() {
        runBlocking {
            // Given
            val correo = "wrong@example.com"
            val contrasena = "WrongPass"

            `when`(mockApi.login(LoginRequest(correo, contrasena)))
                .thenReturn(Response.error(401, mock(ResponseBody::class.java)))

            // When
            val result = repository.loginUser(correo, contrasena)

            // Then
            assertNull(result)
        }
    }

    @Test
    fun `updateUserProfile actualiza correctamente`() {
        runBlocking {
            // Given
            val userId = 1L
            val updatedUser = User(
                id = userId.toInt(),
                nombre = "Updated Name",
                correo = "test@example.com",
                contrasena = "pass",
                ubicacion = "New City"
            )

            val updateRequest = UpdateUserRequest(
                nombre = "Updated Name",
                ubicacion = "New City",
                profileImageUrl = null
            )

            `when`(mockApi.updateUser(userId, updateRequest))
                .thenReturn(Response.success(updatedUser))

            // When
            val result = repository.updateUserProfile(
                userId,
                "Updated Name",
                "New City",
                null
            )

            // Then
            assertNotNull(result)
            assertEquals("Updated Name", result?.nombre)
            assertEquals("New City", result?.ubicacion)
        }
    }
}
