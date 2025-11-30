package com.example.foro_cinev1.data.repository

import com.example.foro_cinev1.data.remote.api.CreatePostRequest
import com.example.foro_cinev1.data.remote.api.PostApi
import com.example.foro_cinev1.domain.models.Post
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

class PostRepositoryTest {

    @Mock
    private lateinit var mockApi: PostApi

    private lateinit var repository: PostRepository

    private val samplePost = Post(
        id = 1,
        titulo = "Test Post",
        contenido = "Contenido de prueba",
        autor = "Autor Test",
        userId = 100,
        fecha = "2025-01-01",
        likes = 0,
        dislikes = 0
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        // Inyección del mock
        repository = PostRepository(mockApi)
    }

    @Test
    fun `getPosts devuelve lista de posts cuando es exitoso`() {
        runBlocking {
            // Given
            val posts = listOf(samplePost, samplePost.copy(id = 2, titulo = "Post 2"))
            `when`(mockApi.getPosts()).thenReturn(Response.success(posts))

            // When
            val result = repository.getPosts()

            // Then
            assertEquals(2, result.size)
            assertEquals("Test Post", result[0].titulo)
        }
    }

    @Test
    fun `getPosts devuelve lista vacia cuando falla`() {
        runBlocking {
            // Given
            `when`(mockApi.getPosts()).thenReturn(Response.error(500, mock(ResponseBody::class.java)))

            // When
            val result = repository.getPosts()

            // Then
            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `createPost devuelve true cuando es exitoso`() {
        runBlocking {
            // Given
            val expectedRequest = CreatePostRequest(
                titulo = samplePost.titulo,
                contenido = samplePost.contenido,
                autor = samplePost.autor,
                userId = samplePost.userId,
                fecha = samplePost.fecha
            )

            // Usamos el objeto real en lugar de any() para evitar problemas de nulabilidad en Kotlin
            `when`(mockApi.createPost(expectedRequest))
                .thenReturn(Response.success(samplePost))

            // When
            val result = repository.createPost(samplePost)

            // Then
            assertTrue(result)
            verify(mockApi).createPost(expectedRequest)
        }
    }

    @Test
    fun `createPost devuelve false cuando falla`() {
        runBlocking {
            // Given
            val expectedRequest = CreatePostRequest(
                titulo = samplePost.titulo,
                contenido = samplePost.contenido,
                autor = samplePost.autor,
                userId = samplePost.userId,
                fecha = samplePost.fecha
            )

            `when`(mockApi.createPost(expectedRequest))
                .thenReturn(Response.error(400, mock(ResponseBody::class.java)))

            // When
            val result = repository.createPost(samplePost)

            // Then
            assertFalse(result)
        }
    }

    @Test
    fun `deletePost devuelve true cuando es exitoso`() {
        runBlocking {
            // Given
            val postId = 1
            val userId = 100
            
            // Usamos valores explícitos en lugar de anyLong()
            `when`(mockApi.deletePost(postId.toLong(), userId.toLong()))
                .thenReturn(Response.success(null))

            // When
            val result = repository.deletePost(postId, userId)

            // Then
            assertTrue(result)
            verify(mockApi).deletePost(postId.toLong(), userId.toLong())
        }
    }

    @Test
    fun `votePost devuelve post actualizado cuando es exitoso (like)`() {
        runBlocking {
            // Given
            val postId = 1
            val userId = 100
            val likedPost = samplePost.copy(likes = 1)

            // vote = 1 (like)
            `when`(mockApi.votePost(postId.toLong(), userId.toLong(), 1))
                .thenReturn(Response.success(likedPost))

            // When
            val result = repository.votePost(postId, userId, true)

            // Then
            assertNotNull(result)
            assertEquals(1, result?.likes)
        }
    }

    @Test
    fun `votePost devuelve null cuando falla`() {
        runBlocking {
            // Given
            val postId = 1
            val userId = 100
            // vote = -1 (dislike)
            `when`(mockApi.votePost(postId.toLong(), userId.toLong(), -1))
                .thenReturn(Response.error(500, mock(ResponseBody::class.java)))

            // When
            val result = repository.votePost(postId, userId, false)

            // Then
            assertNull(result)
        }
    }
}
