package com.example.foro_cinev1

import com.example.foro_cinev1.domain.models.Comment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CommentModelTest {

    @Test
    fun `comment se vincula correctamente a un post`() {
        val comment = Comment(
            id = 0,
            postId = 5,
            autor = "Martin",
            contenido = "Me encantó esta película",
            fecha = "30/11/2025"
        )

        assertEquals(5, comment.postId)
        assertEquals("Martin", comment.autor)
        assertEquals("Me encantó esta película", comment.contenido)
        assertEquals("30/11/2025", comment.fecha)
    }

    @Test
    fun `copy permite editar el contenido del comentario sin cambiar el postId`() {
        val original = Comment(
            id = 10,
            postId = 7,
            autor = "Usuario",
            contenido = "Texto original",
            fecha = "29/11/2025"
        )

        val editado = original.copy(
            contenido = "Texto editado",
            fecha = "30/11/2025"
        )

        // El comentario original no cambia
        assertEquals("Texto original", original.contenido)
        assertEquals("29/11/2025", original.fecha)

        // El nuevo comentario refleja los cambios
        assertEquals("Texto editado", editado.contenido)
        assertEquals("30/11/2025", editado.fecha)

        // El postId se mantiene
        assertEquals(original.postId, editado.postId)

        // Los objetos son diferentes
        assertNotEquals(original, editado)
    }
}
