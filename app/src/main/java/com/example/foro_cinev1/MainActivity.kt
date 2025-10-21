package com.example.foro_cinev1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.foro_cinev1.ui.theme.CineVerseTheme
import com.example.foro_cinev1.ui.screens.HomeScreen
import com.example.foro_cinev1.ui.screens.PostsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PostsScreen()
            }
        }
    }
}
