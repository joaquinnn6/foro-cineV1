package com.example.foro_cinev1.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Esta clase crea y administra la base de datos local para el foro
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "foro_cine.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Se ejecuta solo la primera vez que la app crea la base de datos
        db.execSQL(
            """
            CREATE TABLE posts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT NOT NULL,
                contenido TEXT NOT NULL,
                autor TEXT NOT NULL,
                fecha TEXT NOT NULL
            )
            """.trimIndent()
        )

        // agregar las tablas de usuarios, comentarios y noticias (si es que hay)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Si la estructura cambia, borra la tabla anterior y la vuelve a crear
        db.execSQL("DROP TABLE IF EXISTS posts")
        onCreate(db)
    }
}
