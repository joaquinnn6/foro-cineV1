package com.example.foro_cinev1.viewmodel

import com.example.foro_cinev1.data.repository.CommentRepository
import com.example.foro_cinev1.domain.models.Comment
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
class CommentViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockRepository: CommentRepository

    private lateinit var viewModel: CommentViewModel

    private val sampleComment = Comment(
        id = 1,
        postId = 10,
        autor = "Test User",
        contenido = "Comentario de prueba",
        fecha = "01/01/2025"
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = CommentViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarComentarios actualiza el estado con la lista de comentarios`() = runTest(testDispatcher) {
        // Given
        val comentarios = listOf(sampleComment)
        `when`(mockRepository.obtenerComentarios(10)).thenReturn(comentarios)

        // When
        viewModel.cargarComentarios(10)
        advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.comentarios.value.size)
        assertEquals("Test User", viewModel.comentarios.value.first().autor)
    }

    @Test
    fun `agregarComentario llama al repositorio y recarga si es exitoso`() = runTest(testDispatcher) {
        // Given
        `when`(mockRepository.agregarComentario(sampleComment)).thenReturn(true)
        // Para verificar que recarga, simulamos que obtenerComentarios devuelve una lista con el nuevo comentario
        `when`(mockRepository.obtenerComentarios(10)).thenReturn(listOf(sampleComment))

        // When
        viewModel.agregarComentario(sampleComment)
        advanceUntilIdle()

        // Then
        verify(mockRepository).agregarComentario(sampleComment)
        verify(mockRepository).obtenerComentarios(10)
        assertEquals(1, viewModel.comentarios.value.size)
    }

    @Test
    fun `eliminarComentario llama al repositorio y recarga si es exitoso`() = runTest(testDispatcher) {
        // Given
        val commentId = 1
        val postId = 10
        `when`(mockRepository.eliminarComentario(commentId)).thenReturn(true)
        // Recarga vacía
        `when`(mockRepository.obtenerComentarios(postId)).thenReturn(emptyList())

        // When
        viewModel.eliminarComentario(commentId, postId)
        advanceUntilIdle()

        // Then
        verify(mockRepository).eliminarComentario(commentId)
        verify(mockRepository).obtenerComentarios(postId)
        assertTrue(viewModel.comentarios.value.isEmpty())
    }
}
