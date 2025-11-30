package com.example.foro_cinev1.viewmodel

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

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: NewsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = NewsViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init carga noticias de ejemplo`() = runTest(testDispatcher) {
        // El init ya se ejecutó al crear el viewModel, pero necesitamos dar tiempo a la corutina
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.noticias.value.isEmpty())
        assertEquals(5, viewModel.noticias.value.size)
        assertEquals("Nuevo tráiler de Dune 3", viewModel.noticias.value[0].title)
    }

    @Test
    fun `cargarNoticiaPorId selecciona la noticia correcta`() = runTest(testDispatcher) {
        // Given
        advanceUntilIdle() // Asegurar que hay noticias cargadas
        val idBuscado = 2

        // When
        viewModel.cargarNoticiaPorId(idBuscado)

        // Then
        assertNotNull(viewModel.noticiaSeleccionada.value)
        assertEquals(idBuscado, viewModel.noticiaSeleccionada.value?.id)
        assertEquals("Oscar 2026: Nominaciones", viewModel.noticiaSeleccionada.value?.title)
    }

    @Test
    fun `cargarNoticiaPorId devuelve null si no existe`() = runTest(testDispatcher) {
        // Given
        advanceUntilIdle()
        val idInexistente = 999

        // When
        viewModel.cargarNoticiaPorId(idInexistente)

        // Then
        assertNull(viewModel.noticiaSeleccionada.value)
    }

    @Test
    fun `toggleFavorite actualiza el estado de favorito en la lista`() = runTest(testDispatcher) {
        // Given
        advanceUntilIdle()
        val idFavorito = 1
        val valorInicial = viewModel.noticias.value.find { it.id == idFavorito }!!.isFavorite

        // When
        viewModel.toggleFavorite(idFavorito)
        advanceUntilIdle()

        // Then
        val noticiaActualizada = viewModel.noticias.value.find { it.id == idFavorito }
        assertNotEquals(valorInicial, noticiaActualizada?.isFavorite)
        assertTrue(noticiaActualizada!!.isFavorite) // Asumiendo que empieza en false
    }

    @Test
    fun `toggleFavorite actualiza noticia seleccionada si coincide`() = runTest(testDispatcher) {
        // Given
        advanceUntilIdle()
        val id = 1
        viewModel.cargarNoticiaPorId(id)
        
        // When
        viewModel.toggleFavorite(id)
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.noticiaSeleccionada.value!!.isFavorite)
    }
}
