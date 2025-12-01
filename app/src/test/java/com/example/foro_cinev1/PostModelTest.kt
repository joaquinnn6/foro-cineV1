package com.example.foro_cinev1

import com.example.foro_cinev1.domain.models.Post
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PostModelTest {

    @Test
    fun `post se crea con valores por defecto coherentes`() {
        val post = Post(
            titulo = "Mi primer post",
            contenido = "Contenido de prueba",
            autor = "Martin",
            userId = 10,
            fecha = "30/11/2025"
        )

        // id y contadores empiezan en 0
        assertEquals(0, post.id)
        assertEquals(0, post.likes)
        assertEquals(0, post.dislikes)

        // datos básicos
        assertEquals("Mi primer post", post.titulo)
        assertEquals("Contenido de prueba", post.contenido)
        assertEquals("Martin", post.autor)
        assertEquals(10, post.userId)
        assertEquals("30/11/2025", post.fecha)
    }

    @Test
    fun `copy permite actualizar likes sin mutar el post original`() {
        val original = Post(
            id = 1,
            titulo = "Post",
            contenido = "Contenido",
            autor = "Martin",
            userId = 10,
            fecha = "30/11/2025",
            likes = 0,
            dislikes = 0
        )

        val conLike = original.copy(likes = original.likes + 1)

        // El original sigue igual
        assertEquals(0, original.likes)

        // El nuevo tiene 1 like
        assertEquals(1, conLike.likes)

        // El resto de campos no cambia
        assertEquals(original.id, conLike.id)
        assertEquals(original.titulo, conLike.titulo)
        assertEquals(original.contenido, conLike.contenido)
        assertEquals(original.autor, conLike.autor)
        assertEquals(original.userId, conLike.userId)
        assertEquals(original.fecha, conLike.fecha)
    }

    @Test
    fun `copy permite actualizar dislikes sin mutar el post original`() {
        val original = Post(
            id = 2,
            titulo = "Otro post",
            contenido = "Texto",
            autor = "Usuario",
            userId = 20,
            fecha = "01/12/2025",
            likes = 3,
            dislikes = 0
        )

        val conDislike = original.copy(dislikes = original.dislikes + 1)

        assertEquals(0, original.dislikes)
        assertEquals(1, conDislike.dislikes)

        // Verificamos que el objeto nuevo no es exactamente el mismo
        assertNotEquals(original, conDislike)
    }
}
