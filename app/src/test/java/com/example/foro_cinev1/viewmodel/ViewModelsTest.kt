package com.example.foro_cinev1.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.foro_cinev1.data.repository.PostRepository
import com.example.foro_cinev1.domain.models.Post
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
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelsTest {

    // Regla para ejecutar tareas de LiveData/Architecture Components sincrónicamente
    // Aunque uses StateFlow, es buena práctica tenerla si usas algo de LiveData por ahí.
    // Si no tienes la dependencia 'androidx.arch.core:core-testing', esto podría fallar.
    // Como usas StateFlow principalmente, lo más importante es el TestDispatcher.

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockPostRepository: PostRepository

    private lateinit var viewModel: PostViewModel

    private val samplePost = Post(
        id = 1,
        titulo = "Test",
        contenido = "Contenido",
        autor = "Autor",
        fecha = "2025-01-01"
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = PostViewModel(mockPostRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarPosts actualiza el estado con la lista de posts`() = runTest(testDispatcher) {
        // Given
        val posts = listOf(samplePost, samplePost.copy(id = 2))
        `when`(mockPostRepository.getPosts()).thenReturn(posts)

        // When
        viewModel.cargarPosts()
        advanceUntilIdle() // Espera a que terminen las corutinas

        // Then
        assertEquals(2, viewModel.posts.value.size)
        assertEquals(1, viewModel.posts.value[0].id)
    }

    @Test
    fun `agregarPost llama al repositorio y recarga posts si es exitoso`() = runTest(testDispatcher) {
        // Given
        `when`(mockPostRepository.createPost(samplePost)).thenReturn(true)
        // Cuando se recargue, devolvemos una lista con el nuevo post
        `when`(mockPostRepository.getPosts()).thenReturn(listOf(samplePost))

        // When
        viewModel.agregarPost(samplePost)
        advanceUntilIdle()

        // Then
        verify(mockPostRepository).createPost(samplePost)
        verify(mockPostRepository).getPosts() // Verifica que se llamó a recargar
        assertEquals(1, viewModel.posts.value.size)
    }

    @Test
    fun `eliminarPost actualiza deleteError si falla`() = runTest(testDispatcher) {
        // Given
        val postId = 1
        val userId = 100
        `when`(mockPostRepository.deletePost(postId, userId)).thenReturn(false)

        // When
        viewModel.eliminarPost(postId, userId)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.deleteError.value)
        verify(mockPostRepository).deletePost(postId, userId)
    }

    @Test
    fun `votarPost actualiza el post en la lista localmente`() = runTest(testDispatcher) {
        // Given
        // Primero cargamos la lista inicial
        val initialPosts = listOf(samplePost)
        `when`(mockPostRepository.getPosts()).thenReturn(initialPosts)
        viewModel.cargarPosts()
        advanceUntilIdle()

        // Simulamos que el repo devuelve el post actualizado con un like
        val likedPost = samplePost.copy(likes = 1)
        `when`(mockPostRepository.votePost(1, 100, true)).thenReturn(likedPost)

        // When
        viewModel.votarPost(1, 100, true)
        advanceUntilIdle()

        // Then
        val postEnLista = viewModel.posts.value.find { it.id == 1 }
        assertEquals(1, postEnLista?.likes)
    }
}
