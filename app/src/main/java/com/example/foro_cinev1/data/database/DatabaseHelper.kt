package com.example.foro_cinev1.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.foro_cinev1.domain.models.News
import com.example.foro_cinev1.domain.models.Post
import com.example.foro_cinev1.domain.models.User

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "foro_cine.db"
        private const val DATABASE_VERSION = 4
        private const val TABLE_POSTS = "posts"

        private const val COLUMN_ID = "id"
        private const val COLUMN_TITULO = "titulo"
        private const val COLUMN_CONTENIDO = "contenido"
        private const val COLUMN_AUTOR = "autor"
        private const val COLUMN_FECHA = "fecha"
        private const val COLUMN_LIKES = "likes"
        private const val COLUMN_DISLIKES = "dislikes"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tabla de publicaciones
        db.execSQL("""
            CREATE TABLE $TABLE_POSTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITULO TEXT,
                $COLUMN_CONTENIDO TEXT,
                $COLUMN_AUTOR TEXT,
                $COLUMN_FECHA TEXT,
                $COLUMN_LIKES INTEGER DEFAULT 0,
                $COLUMN_DISLIKES INTEGER DEFAULT 0
            )
        """)

        // Tabla de usuarios
        db.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                correo TEXT UNIQUE NOT NULL,
                contraseña TEXT NOT NULL,
                ubicacion TEXT
            )
        """)

        // Tabla de comentarios
        db.execSQL("""
            CREATE TABLE comments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                post_id INTEGER,
                autor TEXT,
                contenido TEXT,
                fecha TEXT,
                FOREIGN KEY (post_id) REFERENCES $TABLE_POSTS(id) ON DELETE CASCADE
            )
        """)

        // Tabla de noticias
        db.execSQL("""
            CREATE TABLE news (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo TEXT,
                resumen TEXT,
                contenido TEXT,
                autor TEXT,
                fecha TEXT
            )
        """)

        // 🔥 NUEVA TABLA: votos de usuarios
        db.execSQL("""
            CREATE TABLE user_votes (
                user_id INTEGER,
                post_id INTEGER,
                vote INTEGER, -- 1 = like, -1 = dislike
                PRIMARY KEY (user_id, post_id)
            )
        """)

        // Noticias demo
        db.execSQL("""
            INSERT INTO news (titulo, resumen, contenido, autor, fecha)
            VALUES
            (
                'Nueva película rompe récords de taquilla',
                'El estreno más exitoso del año sorprende al público y a la crítica.',
                'La película ha superado todas las expectativas, recaudando más de 500 millones de dólares...',
                'CineVerse',
                '15/10/2025'
            ),
            (
                'Festival de Cine de Cannes anuncia sus nominados',
                'Directores y actores de todo el mundo compiten por la Palma de Oro.',
                'El jurado del Festival de Cannes ha revelado la lista completa de nominados...',
                'Redacción CineVerse',
                '12/10/2025'
            ),
            (
                'Marvel Studios confirma nueva fase del UCM',
                'Se anuncian las próximas películas y series que conformarán la Fase 6.',
                'Durante el evento D23, Kevin Feige reveló los proyectos que darán forma a la próxima fase...',
                'CineVerse Noticias',
                '10/10/2025'
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS comments")
        db.execSQL("DROP TABLE IF EXISTS posts")
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS news")
        db.execSQL("DROP TABLE IF EXISTS user_votes")
        onCreate(db)
    }

    // ---------- 🧱 POSTS ----------
    fun insertPost(post: Post): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITULO, post.titulo)
            put(COLUMN_CONTENIDO, post.contenido)
            put(COLUMN_AUTOR, post.autor)
            put(COLUMN_FECHA, post.fecha)
            put(COLUMN_LIKES, post.likes)
            put(COLUMN_DISLIKES, post.dislikes)
        }
        val result = db.insert(TABLE_POSTS, null, values)
        db.close()
        return result
    }

    fun getAllPosts(): List<Post> {
        val posts = mutableListOf<Post>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_POSTS ORDER BY $COLUMN_ID DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val post = Post(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITULO)),
                    contenido = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENIDO)),
                    autor = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTOR)),
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA)),
                    likes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIKES)),
                    dislikes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DISLIKES))
                )
                posts.add(post)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return posts
    }

    fun deletePost(id: Int): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_POSTS, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
        return result
    }

    // ---------- 🗳️ VOTAR (LIKE / DISLIKE) ----------
    fun votarPost(userId: Int, postId: Int, voto: Int): Boolean {
        val db = writableDatabase

        val cursor = db.rawQuery(
            "SELECT vote FROM user_votes WHERE user_id=? AND post_id=?",
            arrayOf(userId.toString(), postId.toString())
        )

        return try {
            if (cursor.moveToFirst()) {
                val votoAnterior = cursor.getInt(0)

                if (votoAnterior == voto) {
                    // 👎 Si repite el mismo voto, se elimina
                    db.delete("user_votes", "user_id=? AND post_id=?", arrayOf(userId.toString(), postId.toString()))
                    val columna = if (voto == 1) COLUMN_LIKES else COLUMN_DISLIKES
                    db.execSQL("UPDATE $TABLE_POSTS SET $columna = $columna - 1 WHERE id=?", arrayOf(postId))
                } else {
                    // 🔄 Si cambia de like a dislike o viceversa
                    db.execSQL("UPDATE user_votes SET vote=? WHERE user_id=? AND post_id=?", arrayOf(voto, userId, postId))
                    val (columnaSuma, columnaResta) = if (voto == 1) COLUMN_LIKES to COLUMN_DISLIKES else COLUMN_DISLIKES to COLUMN_LIKES
                    db.execSQL("UPDATE $TABLE_POSTS SET $columnaSuma = $columnaSuma + 1, $columnaResta = $columnaResta - 1 WHERE id=?", arrayOf(postId))
                }
            } else {
                // 👍 Nuevo voto
                db.execSQL("INSERT INTO user_votes (user_id, post_id, vote) VALUES (?, ?, ?)", arrayOf(userId, postId, voto))
                val columna = if (voto == 1) COLUMN_LIKES else COLUMN_DISLIKES
                db.execSQL("UPDATE $TABLE_POSTS SET $columna = $columna + 1 WHERE id=?", arrayOf(postId))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            cursor.close()
            db.close()
        }
    }

    // ---------- 👤 USERS ----------
    fun addUser(user: User): Boolean {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE correo = ?", arrayOf(user.correo))
        val existe = cursor.moveToFirst()
        cursor.close()
        if (existe) {
            db.close()
            return false
        }

        val values = ContentValues().apply {
            put("nombre", user.nombre)
            put("correo", user.correo)
            put("contrasena", user.contrasena)
            put("ubicacion", user.ubicacion)
        }

        val resultado = db.insert("users", null, values)
        db.close()
        return resultado != -1L
    }

    fun loginUser(correo: String, contraseña: String): User? {
        val db = readableDatabase
        var user: User? = null
        try {
            val cursor = db.rawQuery(
                "SELECT * FROM users WHERE correo = ? AND contraseña = ?",
                arrayOf(correo, contraseña)
            )
            if (cursor.moveToFirst()) {
                user = User(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                    contrasena = cursor.getString(cursor.getColumnIndexOrThrow("contraseña")),
                    ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion"))
                )
            }
            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        return user
    }

    // ---------- 💬 COMMENTS ----------
    fun insertComment(postId: Int, autor: String, contenido: String, fecha: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("post_id", postId)
            put("autor", autor)
            put("contenido", contenido)
            put("fecha", fecha)
        }
        val result = db.insert("comments", null, values)
        db.close()
        return result
    }

    fun getCommentsByPost(postId: Int): List<Map<String, String>> {
        val comentarios = mutableListOf<Map<String, String>>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT autor, contenido, fecha FROM comments WHERE post_id = ? ORDER BY id DESC",
            arrayOf(postId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val autor = cursor.getString(cursor.getColumnIndexOrThrow("autor"))
                val contenido = cursor.getString(cursor.getColumnIndexOrThrow("contenido"))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
                comentarios.add(mapOf("autor" to autor, "contenido" to contenido, "fecha" to fecha))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return comentarios
    }

    // ---------- 📰 NEWS ----------
    fun insertNews(news: News): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("titulo", news.titulo)
            put("resumen", news.resumen)
            put("contenido", news.contenido)
            put("autor", news.autor)
            put("fecha", news.fecha)
        }
        val result = db.insert("news", null, values)
        db.close()
        return result
    }

    fun getAllNews(): List<News> {
        val lista = mutableListOf<News>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM news ORDER BY id DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val news = News(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                    resumen = cursor.getString(cursor.getColumnIndexOrThrow("resumen")),
                    contenido = cursor.getString(cursor.getColumnIndexOrThrow("contenido")),
                    autor = cursor.getString(cursor.getColumnIndexOrThrow("autor")),
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
                )
                lista.add(news)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    fun getNewsById(id: Int): News? {
        val db = readableDatabase
        var news: News? = null
        val cursor = db.rawQuery("SELECT * FROM news WHERE id = ?", arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            news = News(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo")),
                resumen = cursor.getString(cursor.getColumnIndexOrThrow("resumen")),
                contenido = cursor.getString(cursor.getColumnIndexOrThrow("contenido")),
                autor = cursor.getString(cursor.getColumnIndexOrThrow("autor")),
                fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
            )
        }
        cursor.close()
        db.close()
        return news
    }
}
