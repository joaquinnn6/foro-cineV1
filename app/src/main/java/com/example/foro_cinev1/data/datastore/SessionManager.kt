package com.example.foro_cinev1.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

// DataStore para almacenar datos de sesión
private val Context.dataStore by preferencesDataStore(name = "user_session")

class SessionManager(private val contexto: Context) {

    companion object {
        private val ID_USUARIO = intPreferencesKey("id_usuario")
        private val NOMBRE_USUARIO = stringPreferencesKey("nombre_usuario")
        private val CORREO_USUARIO = stringPreferencesKey("correo_usuario")
        private val ROL_USUARIO = stringPreferencesKey("rol_usuario")      // 👈 NUEVO
        private val FOTO_PERFIL = stringPreferencesKey("fotoPerfil")       // opcional, lo reutilizamos abajo
    }

    /**
     * Guarda los datos del usuario logueado (id, nombre, correo, rol)
     */
    fun guardarSesion(id: Int, nombre: String, correo: String, rol: String) {
        runBlocking {
            contexto.dataStore.edit { preferencias ->
                preferencias[ID_USUARIO] = id
                preferencias[NOMBRE_USUARIO] = nombre
                preferencias[CORREO_USUARIO] = correo
                preferencias[ROL_USUARIO] = rol             // 👈 GUARDAMOS ROL
            }
        }
    }

    /**
     * Obtiene el ID del usuario logueado
     */
    fun obtenerId(): Int? = runBlocking {
        val prefs = contexto.dataStore.data.first()
        prefs[ID_USUARIO]
    }

    /**
     * Obtiene el nombre del usuario logueado
     */
    fun obtenerNombre(): String? = runBlocking {
        val prefs = contexto.dataStore.data.first()
        prefs[NOMBRE_USUARIO]
    }

    /**
     * Obtiene el correo del usuario logueado
     */
    fun obtenerCorreo(): String? = runBlocking {
        val prefs = contexto.dataStore.data.first()
        prefs[CORREO_USUARIO]
    }

    /**
     * Obtiene el rol del usuario logueado (USER / ADMIN)
     */
    fun obtenerRol(): String? = runBlocking {
        val prefs = contexto.dataStore.data.first()
        prefs[ROL_USUARIO]                                   // 👈 AQUÍ LO LEEMOS
    }

    /**
     * Elimina la sesión guardada
     */
    fun cerrarSesion() {
        runBlocking {
            contexto.dataStore.edit { preferencias ->
                preferencias.clear()
            }
        }
    }

    /**
     * Verifica si existe una sesión activa
     */
    fun haySesionActiva(): Boolean = runBlocking {
        val prefs = contexto.dataStore.data.first()
        prefs[NOMBRE_USUARIO] != null && prefs[CORREO_USUARIO] != null
    }

    /**
     * Guarda la URI de la foto de perfil
     */
    suspend fun guardarFotoPerfil(uri: String) {
        contexto.dataStore.edit { preferencias ->
            preferencias[FOTO_PERFIL] = uri
        }
    }

    /**
     * Obtiene la URI de la foto de perfil
     */
    fun obtenerFotoPerfil(): String? {
        val prefs = runBlocking { contexto.dataStore.data.first() }
        return prefs[FOTO_PERFIL]
    }
}
