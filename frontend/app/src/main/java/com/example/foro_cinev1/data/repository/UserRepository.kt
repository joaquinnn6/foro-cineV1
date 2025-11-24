package com.example.foro_cinev1.data.repository

import android.content.Context
import com.example.foro_cinev1.data.database.DatabaseHelper
import com.example.foro_cinev1.domain.models.User

class UserRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun registerUser(user: User): Boolean {
        return dbHelper.addUser(user)
    }

    fun loginUser(correo: String, contraseña: String): User? {
        return dbHelper.loginUser(correo, contraseña)
    }
}
