package com.example.foro_cinev1.data.repository

import android.content.ContentValues
import android.content.Context
import com.example.foro_cinev1.data.database.DatabaseHelper
import com.example.foro_cinev1.domain.models.Post

// Esta clase maneja las operaciones de la base de datos (CRUD)
class PostRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // inserta un nuevo post en la base de datos
    fun insertarPost(post: Post) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("titulo", post.titulo)
            put("contenido", post.contenido)
            put("autor", post.autor)
            put("fecha", post.fecha)
        }
        db.insert("posts", null, values)
        db.close()
    }

    // Obtener todos los posts de la base de datos
    fun obtenerPosts(): List<Post> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM posts ORDER BY id DESC", null)
        val lista = mutableListOf<Post>()

        if (cursor.moveToFirst()) {
            do {
                val post = Post(
                    id = cursor.getInt(0),
                    titulo = cursor.getString(1),
                    contenido = cursor.getString(2),
                    autor = cursor.getString(3),
                    fecha = cursor.getString(4)
                )
                lista.add(post)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    // Eliminar un post por su ID
    fun eliminarPost(id: Int) {
        val db = dbHelper.writableDatabase
        db.delete("posts", "id = ?", arrayOf(id.toString()))
        db.close()
    }
}
