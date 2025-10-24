package com.example.foro_cinev1.ui.screens.news

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// Modelo temporal de noticia (tu compañero creará el modelo real)
data class Noticia(
    val id: Int,
    val titulo: String,
    val resumen: String,
    val fecha: String,
    val categoria: String,
    val esFavorita: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    alVolverAtras: () -> Unit,
    alIrADetalle: (Int) -> Unit
) {
    // Datos temporales (tu compañero los traerá del backend)
    var listaNoticias by remember {
        mutableStateOf(
            listOf(
                Noticia(1, "Nuevo tráiler de Dune 3", "Denis Villeneuve confirma...", "23 Oct 2025", "Estrenos"),
                Noticia(2, "Oscar 2026: Nominaciones", "La ceremonia se llevará a cabo...", "22 Oct 2025", "Premios"),
                Noticia(3, "Entrevista con Christopher Nolan", "El director habla sobre su próximo...", "21 Oct 2025", "Entrevistas"),
                Noticia(4, "Top 10 películas del mes", "Las más vistas en streaming...", "20 Oct 2025", "Rankings"),
                Noticia(5, "Marvel anuncia Fase 6", "Nuevos títulos confirmados para...", "19 Oct 2025", "Estrenos")
            )
        )
    }

    var consultaBusqueda by remember { mutableStateOf("") }
    var mostrarBusqueda by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (mostrarBusqueda) {
                        TextField(
                            value = consultaBusqueda,
                            onValueChange = { consultaBusqueda = it },
                            placeholder = { Text("Buscar noticias...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text("Noticias de Cine")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = alVolverAtras) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarBusqueda = !mostrarBusqueda }) {
                        Icon(
                            if (mostrarBusqueda) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = if (mostrarBusqueda) "Cerrar búsqueda" else "Buscar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        val noticiasFiltradas = if (consultaBusqueda.isBlank()) {
            listaNoticias
        } else {
            listaNoticias.filter {
                it.titulo.contains(consultaBusqueda, ignoreCase = true) ||
                        it.resumen.contains(consultaBusqueda, ignoreCase = true)
            }
        }

        if (noticiasFiltradas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No se encontraron noticias",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
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
                items(noticiasFiltradas) { noticia ->
                    TarjetaNoticia(
                        noticia = noticia,
                        alHacerClick = { alIrADetalle(noticia.id) },
                        alCambiarFavorito = { id ->
                            listaNoticias = listaNoticias.map {
                                if (it.id == id) it.copy(esFavorita = !it.esFavorita)
                                else it
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaNoticia(
    noticia: Noticia,
    alHacerClick: () -> Unit,
    alCambiarFavorito: (Int) -> Unit
) {
    Card(
        onClick = alHacerClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Categoría
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = noticia.categoria,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Título
                    Text(
                        text = noticia.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Resumen
                    Text(
                        text = noticia.resumen,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Botón de favorito
                IconButton(onClick = { alCambiarFavorito(noticia.id) }) {
                    Icon(
                        imageVector = if (noticia.esFavorita) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = if (noticia.esFavorita) "Quitar de favoritos"
                        else "Agregar a favoritos",
                        tint = if (noticia.esFavorita) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fecha
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = noticia.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}