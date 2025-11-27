package com.example.foro_cinev1.ui.screens.forum

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foro_cinev1.data.datastore.SessionManager
import com.example.foro_cinev1.domain.models.Post
import com.example.foro_cinev1.viewmodel.PostViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    alVolverAtras: () -> Unit
) {
    val contexto = LocalContext.current
    val sessionManager = remember { SessionManager(contexto) }

    // ✅ ViewModel que ahora habla con el backend (PostRepository + Retrofit)
    val viewModel: PostViewModel = viewModel()

    var autor by remember { mutableStateOf("") }
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    var userId by remember { mutableStateOf<Int?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val estadoScroll = rememberScrollState()

    // Cargar nombre e ID del usuario logueado
    LaunchedEffect(Unit) {
        autor = sessionManager.obtenerNombre() ?: "Anónimo"
        userId = sessionManager.obtenerId()
    }

    fun validarYPublicar() {
        var error: String? = null

        when {
            userId == null || userId!! <= 0 ->
                error = "No se pudo identificar al usuario. Inicia sesión nuevamente."
            autor.isBlank() -> error = "El nombre del autor es requerido"
            titulo.isBlank() -> error = "El título es requerido"
            contenido.isBlank() -> error = "El contenido es requerido"
        }

        if (error != null) {
            scope.launch {
                snackbarHostState.showSnackbar(error!!)
            }
        } else {
            val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val nuevaPublicacion = Post(
                // id se lo asigna el backend
                titulo = titulo,
                contenido = contenido,
                autor = autor,
                userId = userId ?: 0,   // 👈 AQUÍ ENVIAMOS EL ID REAL DEL USUARIO
                fecha = fecha
                // likes, dislikes usan sus valores por defecto del data class
            )

            // 🔥 Llama al backend a través del ViewModel
            viewModel.agregarPost(nuevaPublicacion)

            // Muestra diálogo de éxito
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
}
