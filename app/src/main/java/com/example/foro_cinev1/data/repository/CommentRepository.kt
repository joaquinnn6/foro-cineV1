package com.example.foro_cinev1.data.repository

import android.content.ContentValues
import android.content.Context
import com.example.foro_cinev1.data.database.DatabaseHelper
import com.example.foro_cinev1.domain.models.Comment

class CommentRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun agregarComentario(comment: Comment): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("post_id", comment.postId)
            put("autor", comment.autor)
            put("contenido", comment.contenido)
            put("fecha", comment.fecha)
        }
        val id = db.insert("comments", null, values)
        db.close()
        return id
    }

    fun obtenerComentarios(postId: Int): List<Comment> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM comments WHERE post_id = ? ORDER BY id DESC", arrayOf(postId.toString()))
        val comentarios = mutableListOf<Comment>()
        with(cursor) {
            while (moveToNext()) {
                comentarios.add(
                    Comment(
                        id = getInt(getColumnIndexOrThrow("id")),
                        postId = getInt(getColumnIndexOrThrow("post_id")),
                        autor = getString(getColumnIndexOrThrow("autor")),
                        contenido = getString(getColumnIndexOrThrow("contenido")),
                        fecha = getString(getColumnIndexOrThrow("fecha"))
                    )
                )
            }
        }
        cursor.close()
        db.close()
        return comentarios
    }

    fun eliminarComentario(id: Int): Int {
        val db = dbHelper.writableDatabase
        val filas = db.delete("comments", "id = ?", arrayOf(id.toString()))
        db.close()
        return filas
    }
}
