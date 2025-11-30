package com.example.foro_cinev1.domain.models

import org.junit.Assert.*
import org.junit.Test

class ModelTests {

    // ---------------------------------------------------------------------------------------------
    // USER TESTS
    // ---------------------------------------------------------------------------------------------
    @Test
    fun `User se crea correctamente con valores por defecto`() {
        // Given
        val nombre = "Test User"
        val correo = "test@example.com"
        val contrasena = "123456"

        // When
        val user = User(
            nombre = nombre,
            correo = correo,
            contrasena = contrasena
        )

        // Then
        assertEquals(0, user.id) // Default
        assertEquals("USER", user.role) // Default
        assertNull(user.ubicacion) // Default
        assertNull(user.profileImageUrl) // Default
        assertEquals(nombre, user.nombre)
        assertEquals(correo, user.correo)
    }

    @Test
    fun `User copy modifica los campos correctamente`() {
        // Given
        val user = User(
            id = 1,
            nombre = "Original",
            correo = "original@test.com",
            contrasena = "pass",
            role = "USER"
        )

        // When
        val updatedUser = user.copy(nombre = "Copia", role = "ADMIN")

        // Then
        assertEquals(1, updatedUser.id) // Mantiene id
        assertEquals("Copia", updatedUser.nombre) // Cambia nombre
        assertEquals("ADMIN", updatedUser.role) // Cambia rol
        assertEquals("original@test.com", updatedUser.correo) // Mantiene correo
    }

    // ---------------------------------------------------------------------------------------------
    // POST TESTS
    // ---------------------------------------------------------------------------------------------
    @Test
    fun `Post se crea correctamente con valores por defecto`() {
        // Given
        val titulo = "Mi Post"
        val contenido = "Contenido..."
        val autor = "Autor"
        val fecha = "2025-01-01"

        // When
        val post = Post(
            titulo = titulo,
            contenido = contenido,
            autor = autor,
            fecha = fecha
        )

        // Then
        assertEquals(0, post.id) // Default
        assertEquals(0, post.userId) // Default
        assertEquals(0, post.likes) // Default
        assertEquals(0, post.dislikes) // Default
        assertEquals(titulo, post.titulo)
    }

    @Test
    fun `Post copy funciona para actualizar likes`() {
        // Given
        val post = Post(
            titulo = "Titulo",
            contenido = "Contenido",
            autor = "Autor",
            fecha = "Fecha",
            likes = 10
        )

        // When
        val likedPost = post.copy(likes = post.likes + 1)

        // Then
        assertEquals(11, likedPost.likes)
        assertEquals(post.titulo, likedPost.titulo)
    }

    // ---------------------------------------------------------------------------------------------
    // COMMENT TESTS
    // ---------------------------------------------------------------------------------------------
    @Test
    fun `Comment se instancia correctamente`() {
        // Given
        val postId = 100
        val autor = "Comentarista"
        val contenido = "Buen post"
        val fecha = "Hoy"

        // When
        val comment = Comment(
            postId = postId,
            autor = autor,
            contenido = contenido,
            fecha = fecha
        )

        // Then
        assertEquals(0, comment.id) // Default
        assertEquals(postId, comment.postId)
        assertEquals(autor, comment.autor)
        assertEquals(contenido, comment.contenido)
    }
}
