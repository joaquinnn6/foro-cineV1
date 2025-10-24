package com.example.foro_cinev1.ui.screens.forum

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foro_cinev1.domain.models.Post
import com.example.foro_cinev1.viewmodel.PostViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    alVolverAtras: () -> Unit
) {
    val contexto = LocalContext.current
    val viewModel = remember { PostViewModel(contexto) }

    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var mostrarError by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }

    val estadoScroll = rememberScrollState()

    fun validarYPublicar() {
        when {
            titulo.isBlank() -> {
                mensajeError = "El título es requerido"
                mostrarError = true
            }
            contenido.isBlank() -> {
                mensajeError = "El contenido es requerido"
                mostrarError = true
            }
            autor.isBlank() -> {
                mensajeError = "El nombre del autor es requerido"
                mostrarError = true
            }
            else -> {
                val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val nuevaPublicacion = Post(
                    titulo = titulo,
                    contenido = contenido,
                    autor = autor,
                    fecha = fecha
                )
                viewModel.agregarPost(nuevaPublicacion)
                alVolverAtras()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Publicación") },
                navigationIcon = {
                    IconButton(onClick = alVolverAtras) {
                        Icon(Icons.Default.Close, contentDescription = "Cancelar")
                    }
                },
                actions = {
                    TextButton(onClick = { validarYPublicar() }) {
                        Text(
                            "Publicar",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(estadoScroll)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Comparte tus pensamientos sobre películas, series o el mundo del cine",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Campo de autor
            OutlinedTextField(
                value = autor,
                onValueChange = { autor = it },
                label = { Text("Tu nombre") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                placeholder = { Text("Ej: Juan Pérez") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            // Campo de título
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la publicación") },
                leadingIcon = {
                    Icon(Icons.Default.Title, contentDescription = null)
                },
                placeholder = { Text("Ej: ¿Qué opinan de la nueva película de...?") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de contenido
            OutlinedTextField(
                value = contenido,
                onValueChange = { contenido = it },
                label = { Text("Contenido") },
                placeholder = { Text("Escribe tu opinión, análisis o pregunta...") },
                minLines = 8,
                maxLines = 15,
                modifier = Modifier.fillMaxWidth()
            )

            // Contador de caracteres
            Text(
                text = "${contenido.length} caracteres",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botón de publicar (alternativo al del TopBar)
            Button(
                onClick = { validarYPublicar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publicar")
            }
        }
    }

    // Snackbar de error
    if (mostrarError) {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { mostrarError = false }) {
                    Text("OK")
                }
            }
        ) {
            Text(mensajeError)
        }
    }
}