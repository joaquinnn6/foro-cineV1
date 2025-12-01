package com.example.foro_cinev1

import com.example.foro_cinev1.domain.models.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserModelTest {

    @Test
    fun `user se crea con rol USER por defecto`() {
        val user = User(
            id = 1,
            nombre = "Martin",
            correo = "martin@example.com",
            contrasena = "Pass1234"
        )

        assertEquals(1, user.id)
        assertEquals("Martin", user.nombre)
        assertEquals("martin@example.com", user.correo)
        assertEquals("Pass1234", user.contrasena)
        assertEquals("USER", user.role)   // 👈 auth: rol por defecto
    }

    @Test
    fun `user puede ser marcado como ADMIN usando copy`() {
        val normal = User(
            id = 2,
            nombre = "AdminTest",
            correo = "admin@example.com",
            contrasena = "Pass1234"
        )

        val admin = normal.copy(role = "ADMIN")

        assertEquals("USER", normal.role)
        assertEquals("ADMIN", admin.role)

        // resto de datos intactos
        assertEquals(normal.id, admin.id)
        assertEquals(normal.nombre, admin.nombre)
        assertEquals(normal.correo, admin.correo)
        assertEquals(normal.contrasena, admin.contrasena)
    }

    @Test
    fun `user puede tener ubicacion y foto opcionales`() {
        val user = User(
            id = 3,
            nombre = "Con Perfil",
            correo = "perfil@example.com",
            contrasena = "Pass1234",
            role = "USER",
            ubicacion = "Viña del Mar",
            profileImageUrl = "https://ejemplo.com/foto.png"
        )

        assertEquals("Viña del Mar", user.ubicacion)
        assertEquals("https://ejemplo.com/foto.png", user.profileImageUrl)
    }
}
