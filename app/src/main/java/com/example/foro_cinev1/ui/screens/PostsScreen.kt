package com.example.foro_cinev1.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foro_cinev1.domain.models.Post
import com.example.foro_cinev1.viewmodel.PostViewModel
import java.text.SimpleDateFormat
import java.util.*

// Esta pantalla permite crear y mostrar publicaciones usando SQLite
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen() {
    val context = LocalContext.current
    val viewModel = remember { PostViewModel(context) }

    // Estados para los campos del formulario
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }

    val posts by viewModel.posts.collectAsState()

    // Cargar publicaciones al iniciar la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarPosts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Foro CineVerse 🎥", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🧾 Campos de texto
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = contenido,
                onValueChange = { contenido = it },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = autor,
                onValueChange = { autor = it },
                label = { Text("Autor") },
                modifier = Modifier.fillMaxWidth()
            )

            // 🎬 Botón para guardar
            Button(
                onClick = {
                    if (titulo.isNotBlank() && contenido.isNotBlank() && autor.isNotBlank()) {
                        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        val post = Post(titulo = titulo, contenido = contenido, autor = autor, fecha = fecha)
                        viewModel.agregarPost(post)

                        // Limpiar campos después de guardar
                        titulo = ""
                        contenido = ""
                        autor = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar")
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // 📜 Lista de publicaciones
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(posts) { post ->
                    PostCard(post = post, context = context, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun PostCard(post: Post, context: Context, viewModel: PostViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(post.titulo, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(post.contenido, fontSize = 16.sp)
            Text("Autor: ${post.autor}", fontSize = 14.sp)
            Text("Fecha: ${post.fecha}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)

            Spacer(Modifier.height(8.dp))

            // ❌ Botón para eliminar publicación
            Button(
                onClick = { viewModel.eliminarPost(post.id) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar", color = MaterialTheme.colorScheme.onError)
            }
        }
    }
}
