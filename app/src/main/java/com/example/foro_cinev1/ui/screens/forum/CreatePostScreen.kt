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
import com.example.foro_cinev1.data.datastore.SessionManager
import com.example.foro_cinev1.domain.models.Post
import com.example.foro_cinev1.viewmodel.PostViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    alVolverAtras: () -> Unit
) {
    val contexto = LocalContext.current
    val viewModel = remember { PostViewModel(contexto) }
    val sessionManager = remember { SessionManager(contexto) }

    var autor by remember { mutableStateOf("") }
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val estadoScroll = rememberScrollState()

    LaunchedEffect(Unit) {
        autor = sessionManager.obtenerNombre() ?: "Anónimo"
    }

    fun validarYPublicar() {
        var error = ""
        if (autor.isBlank()) error = "El nombre del autor es requerido"
        if (contenido.isBlank()) error = "El contenido es requerido"
        if (titulo.isBlank()) error = "El título es requerido"

        if (error.isNotEmpty()) {
            scope.launch {
                snackbarHostState.showSnackbar(error)
            }
        } else {
            val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val nuevaPublicacion = Post(
                titulo = titulo,
                contenido = contenido,
                autor = autor,
                fecha = fecha
            )
            viewModel.agregarPost(nuevaPublicacion)
            mostrarConfirmacion = true
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
            // ... (El resto del contenido de la columna se mantiene igual)
            OutlinedTextField(
                value = autor,
                onValueChange = { autor = it },
                label = { Text("Tu nombre") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título de la publicación") },
                leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                placeholder = { Text("Ej: ¿Qué opinan de la nueva película de...?") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = contenido,
                onValueChange = { contenido = it },
                label = { Text("Contenido") },
                placeholder = { Text("Escribe tu opinión, análisis o pregunta...") },
                minLines = 8,
                maxLines = 15,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "${contenido.length} caracteres",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End)
            )
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

    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacion = false },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Publicación creada") },
            text = { Text("Tu publicación se ha creado con éxito 🎉") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarConfirmacion = false
                        alVolverAtras()
                    }
                ) { Text("Aceptar") }
            }
        )
    }
}