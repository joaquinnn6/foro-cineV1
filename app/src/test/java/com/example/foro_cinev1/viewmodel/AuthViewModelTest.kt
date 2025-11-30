package com.example.foro_cinev1.viewmodel

import com.example.foro_cinev1.data.repository.UserRepository
import com.example.foro_cinev1.domain.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockUserRepository: UserRepository

    private lateinit var viewModel: AuthViewModel

    private val sampleUser = User(
        id = 1,
        nombre = "Test User",
        correo = "test@example.com",
        contrasena = "Password123",
        ubicacion = "Test City"
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(mockUserRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `registrarUsuario invoca callback con true si es exitoso`() = runTest(testDispatcher) {
        // Given
        `when`(mockUserRepository.registerUser(sampleUser)).thenReturn(true)
        var callbackResult = false

        // When
        viewModel.registrarUsuario(sampleUser) { exito ->
            callbackResult = exito
        }
        advanceUntilIdle()

        // Then
        assertTrue(callbackResult)
        verify(mockUserRepository).registerUser(sampleUser)
    }

    @Test
    fun `registrarUsuario invoca callback con false si falla`() = runTest(testDispatcher) {
        // Given
        `when`(mockUserRepository.registerUser(sampleUser)).thenReturn(false)
        // Inicializamos en true para verificar que cambie a false (o se mantenga si la lógica fuera otra)
        // Mejor: usamos un booleano nulo o por defecto que sepamos que cambia.
        var callbackResult: Boolean? = null

        // When
        viewModel.registrarUsuario(sampleUser) { exito ->
            callbackResult = exito
        }
        advanceUntilIdle()

        // Then
        assertNotNull(callbackResult)
        assertFalse(callbackResult!!)
    }

    @Test
    fun `iniciarSesion devuelve usuario si las credenciales son correctas`() = runTest(testDispatcher) {
        // Given
        val correo = "test@example.com"
        val pass = "1234"
        `when`(mockUserRepository.loginUser(correo, pass)).thenReturn(sampleUser)
        var userResult: User? = null

        // When
        viewModel.iniciarSesion(correo, pass) { user ->
            userResult = user
        }
        advanceUntilIdle()

        // Then
        assertNotNull(userResult)
        assertEquals(sampleUser.id, userResult?.id)
        assertEquals(sampleUser.correo, userResult?.correo)
    }

    @Test
    fun `iniciarSesion devuelve null si las credenciales son incorrectas`() = runTest(testDispatcher) {
        // Given
        val correo = "wrong@example.com"
        val pass = "wrong"
        `when`(mockUserRepository.loginUser(correo, pass)).thenReturn(null)
        
        // Usamos una bandera para saber si se llamó
        var callbackCalled = false
        var userResult: User? = sampleUser // Valor inicial distinto de null para verificar cambio

        // When
        viewModel.iniciarSesion(correo, pass) { user ->
            callbackCalled = true
            userResult = user
        }
        advanceUntilIdle()

        // Then
        assertTrue(callbackCalled)
        assertNull(userResult)
    }
}
