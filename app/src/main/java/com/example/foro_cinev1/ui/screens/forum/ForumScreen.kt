package com.example.foro_cinev1.ui.screens.forum

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foro_cinev1.data.datastore.SessionManager
import com.example.foro_cinev1.domain.models.Post
import com.example.foro_cinev1.ui.navigation.Screen
import com.example.foro_cinev1.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
    alIrACrearPublicacion: () -> Unit,
    alVolverAtras: () -> Unit,
    navController: NavController
) {
    val contexto = LocalContext.current
    val viewModel = remember { PostViewModel(contexto) }
    val sessionManager = remember { SessionManager(contexto) }
    val publicaciones by viewModel.posts.collectAsState()

    // Datos de usuario
    var nombreUsuario by remember { mutableStateOf<String?>(null) }
    var userId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        nombreUsuario = sessionManager.obtenerNombre()
        userId = sessionManager.obtenerId()
        viewModel.cargarPosts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Foro de Cine 🎬", fontWeight = FontWeight.Bold)
                        if (!nombreUsuario.isNullOrEmpty()) {
                            Text(
                                text = "Bienvenido, ${nombreUsuario!!.split(" ")[0]} 👋",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = alVolverAtras) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        sessionManager.cerrarSesion()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = alIrACrearPublicacion,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva publicación")
            }
        }
    ) { padding ->
        if (publicaciones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Forum,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "No hay publicaciones aún",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Button(onClick = alIrACrearPublicacion) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear la primera")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(publicaciones) { publicacion ->
                    TarjetaPublicacion(
                        publicacion = publicacion,
                        viewModel = viewModel,
                        sessionManager = sessionManager,
                        userId = userId
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaPublicacion(
    publicacion: Post,
    viewModel: PostViewModel,
    sessionManager: SessionManager,
    userId: Int?
) {
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var mostrarComentarios by remember { mutableStateOf(false) }

    // Estado local para colorear los botones según el voto actual
    var votoUsuario by remember { mutableStateOf(0) } // 0 = ninguno, 1 = like, -1 = dislike

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = publicacion.autor,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = publicacion.fecha,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                IconButton(onClick = { mostrarDialogoEliminar = true }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Opciones",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = publicacion.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = publicacion.contenido,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Acciones
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 👍 LIKE
                TextButton(onClick = {
                    userId?.let {
                        viewModel.votarPost(publicacion.id, it, true)
                        votoUsuario = if (votoUsuario == 1) 0 else 1
                    }
                }) {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = "Me gusta",
                        tint = if (votoUsuario == 1) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("(${publicacion.likes})")
                }

                // 👎 DISLIKE
                TextButton(onClick = {
                    userId?.let {
                        viewModel.votarPost(publicacion.id, it, false)
                        votoUsuario = if (votoUsuario == -1) 0 else -1
                    }
                }) {
                    Icon(
                        Icons.Default.ThumbDown,
                        contentDescription = "No me gusta",
                        tint = if (votoUsuario == -1) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("(${publicacion.dislikes})")
                }

                // 💬 COMENTAR
                TextButton(onClick = { mostrarComentarios = true }) {
                    Icon(
                        Icons.AutoMirrored.Filled.Comment,
                        contentDescription = "Comentar",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Comentar")
                }
            }
        }
    }

    // 💬 Diálogo de comentarios
    if (mostrarComentarios) {
        var nuevoComentario by remember { mutableStateOf("") }
        val autor = sessionManager.obtenerNombre() ?: "Anónimo"
        val listaComentarios by viewModel.comentarios.collectAsState()

        AlertDialog(
            onDismissRequest = { mostrarComentarios = false },
            title = { Text("Comentarios de ${publicacion.titulo}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (listaComentarios.isEmpty()) {
                        Text("No hay comentarios aún.")
                    } else {
                        listaComentarios.forEach { comentario ->
                            Text(
                                text = "💬 ${comentario["autor"]}: ${comentario["contenido"]} (${comentario["fecha"]})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Divider(Modifier.padding(vertical = 8.dp))

                    OutlinedTextField(
                        value = nuevoComentario,
                        onValueChange = { nuevoComentario = it },
                        label = { Text("Escribe un comentario...") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevoComentario.isNotBlank()) {
                            viewModel.agregarComentario(publicacion.id, autor, nuevoComentario)
                            nuevoComentario = ""
                        }
                    }
                ) { Text("Enviar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarComentarios = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    // 🗑️ Diálogo de eliminación
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEliminar = false },
            icon = { Icon(Icons.Default.Delete, contentDescription = null) },
            title = { Text("¿Eliminar publicación?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarPost(publicacion.id)
                        mostrarDialogoEliminar = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
