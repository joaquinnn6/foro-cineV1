package com.example.foro_cinev1.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foro_cinev1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    alIrANoticias: () -> Unit,
    alIrAForo: () -> Unit,
    alIrAPerfil: () -> Unit
) {
    var tabSeleccionada by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = alIrAPerfil) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tabSeleccionada == 0,
                    onClick = { tabSeleccionada = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = tabSeleccionada == 1,
                    onClick = {
                        tabSeleccionada = 1
                        alIrANoticias()
                    },
                    icon = { Icon(Icons.Default.Article, contentDescription = "Noticias") },
                    label = { Text("Noticias") }
                )
                NavigationBarItem(
                    selected = tabSeleccionada == 2,
                    onClick = {
                        tabSeleccionada = 2
                        alIrAForo()
                    },
                    icon = { Icon(Icons.Default.Forum, contentDescription = "Foro") },
                    label = { Text("Foro") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 🏠 Sección de bienvenida
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "¡Bienvenido a CineVerse!",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Explora noticias, comparte opiniones y disfruta del mundo del cine.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // 📰 Noticias destacadas
            item {
                Text(
                    text = "Noticias Destacadas 📰",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(5) { indice ->
                        TarjetaNoticia(
                            titulo = "Noticia ${indice + 1}",
                            resumen = "Descripción breve o titular de ejemplo...",
                            alHacerClick = alIrANoticias
                        )
                    }
                }
            }

            // 💬 Publicaciones recientes
            item {
                Text(
                    text = "Publicaciones Recientes 💬",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(3) { indice ->
                TarjetaPublicacionForo(
                    titulo = "Publicación ${indice + 1}",
                    autor = "Usuario ${indice + 1}",
                    alHacerClick = alIrAForo
                )
            }

            // 🎭 Categorías populares
            item {
                Text(
                    text = "Categorías Populares 🎭",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChipCategoria("🎬 Estrenos", Modifier.weight(1f))
                    ChipCategoria("⭐ Reseñas", Modifier.weight(1f))
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ChipCategoria("🎭 Teorías", Modifier.weight(1f))
                    ChipCategoria("🍿 Recomendaciones", Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun TarjetaNoticia(
    titulo: String,
    resumen: String,
    alHacerClick: () -> Unit
) {
    Card(
        onClick = alHacerClick,
        modifier = Modifier
            .width(220.dp)
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                Icons.Default.Article,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = titulo,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = resumen,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TarjetaPublicacionForo(
    titulo: String,
    autor: String,
    alHacerClick: () -> Unit
) {
    Card(
        onClick = alHacerClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Forum,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "Por $autor",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChipCategoria(texto: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(56.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
