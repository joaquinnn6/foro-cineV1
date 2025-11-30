package com.example.foro_cinev1.data.repository

import com.example.foro_cinev1.data.remote.api.CommentApi
import com.example.foro_cinev1.data.remote.api.CreateCommentRequest
import com.example.foro_cinev1.domain.models.Comment
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import retrofit2.Response

class CommentRepositoryTest {

    @Mock
    private lateinit var mockApi: CommentApi

    private lateinit var repository: CommentRepository

    private val sampleComment = Comment(
        id = 1,
        postId = 10,
        autor = "Test User",
        contenido = "Este es un comentario de prueba",
        fecha = "01/01/2025"
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = CommentRepository(mockApi)
    }

    @Test
    fun `obtenerComentarios devuelve lista cuando es exitoso`() {
        runBlocking {
            // Given
            val comments = listOf(sampleComment, sampleComment.copy(id = 2, contenido = "Otro comentario"))
            // Usamos valor explícito 10L
            `when`(mockApi.getCommentsByPost(10L))
                .thenReturn(Response.success(comments))

            // When
            val result = repository.obtenerComentarios(10)

            // Then
            assertEquals(2, result.size)
            assertEquals("Test User", result[0].autor)
            verify(mockApi).getCommentsByPost(10L)
        }
    }

    @Test
    fun `obtenerComentarios devuelve lista vacia cuando falla`() {
        runBlocking {
            // Given
            // Usamos valor explícito 10L
            `when`(mockApi.getCommentsByPost(10L))
                .thenReturn(Response.error(404, mock(ResponseBody::class.java)))

            // When
            val result = repository.obtenerComentarios(10)

            // Then
            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `agregarComentario devuelve true cuando es exitoso`() {
        runBlocking {
            // Given
            val expectedRequest = CreateCommentRequest(
                autor = sampleComment.autor,
                contenido = sampleComment.contenido,
                fecha = sampleComment.fecha
            )

            // Usamos valores explícitos: 10L y el objeto request esperado
            `when`(mockApi.addComment(10L, expectedRequest))
                .thenReturn(Response.success(sampleComment))

            // When
            val result = repository.agregarComentario(sampleComment)

            // Then
            assertTrue(result)
            verify(mockApi).addComment(10L, expectedRequest)
        }
    }

    @Test
    fun `agregarComentario devuelve false cuando falla`() {
        runBlocking {
            // Given
            val expectedRequest = CreateCommentRequest(
                autor = sampleComment.autor,
                contenido = sampleComment.contenido,
                fecha = sampleComment.fecha
            )

            // Usamos valores explícitos: 10L y el objeto request esperado
            // Eliminamos anyLong() y eq() para evitar problemas con Kotlin/Mockito
            `when`(mockApi.addComment(10L, expectedRequest))
                .thenReturn(Response.error(500, mock(ResponseBody::class.java)))

            // When
            val result = repository.agregarComentario(sampleComment)

            // Then
            assertFalse(result)
        }
    }

    @Test
    fun `eliminarComentario devuelve true cuando es exitoso`() {
        runBlocking {
            // Given
            `when`(mockApi.deleteComment(1L))
                .thenReturn(Response.success(null))

            // When
            val result = repository.eliminarComentario(1)

            // Then
            assertTrue(result)
            verify(mockApi).deleteComment(1L)
        }
    }

    @Test
    fun `eliminarComentario devuelve false cuando falla`() {
        runBlocking {
            // Given
            // Usamos valor explícito 1L
            `when`(mockApi.deleteComment(1L))
                .thenReturn(Response.error(400, mock(ResponseBody::class.java)))

            // When
            val result = repository.eliminarComentario(1)

            // Then
            assertFalse(result)
        }
    }
}
