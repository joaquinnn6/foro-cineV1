package com.example.foro_cinev1.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// paleta de colores, voy a probar con una "moderna" tipo netflix/Hbo
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE11D48),
    secondary = Color(0xFF22D3EE),
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onPrimary = Color(0xFFF8FAFC),
    onSecondary = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC)
)

// esto aplica el tema en toda la app o eso deberia hacer
@Composable
fun CineVerseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = DarkColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}
