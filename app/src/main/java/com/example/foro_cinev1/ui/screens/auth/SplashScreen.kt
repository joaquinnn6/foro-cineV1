package com.example.foro_cinev1.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.foro_cinev1.data.datastore.SessionManager

@Composable
fun SplashScreen(
    onSesionActiva: () -> Unit,
    onIrLogin: () -> Unit
) {
    val contexto = LocalContext.current
    val sessionManager = remember { SessionManager(contexto) }

    // Estado de carga
    var cargando by remember { mutableStateOf(true) }

    // Efecto que verifica la sesión apenas entra
    LaunchedEffect(Unit) {
        delay(1500) // tiempo de "carga" para mostrar el splash
        cargando = false

        if (sessionManager.haySesionActiva()) {
            onSesionActiva()
        } else {
            onIrLogin()
        }
    }

    // Interfaz visual simple
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "CineVerse 🎬",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary
            )

            if (cargando) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                Text(
                    text = "Iniciando...",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
