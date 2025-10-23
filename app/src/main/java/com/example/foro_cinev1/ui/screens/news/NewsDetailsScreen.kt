package com.example.foro_cinev1.ui.screens.news

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    newsId: Int,
    alVolverAtras: () -> Unit
) {
    // Datos temporales (tu compañero los traerá del backend usando newsId)
    val noticia = remember {
        Noticia(
            id = newsId,
            titulo = "Nuevo tráiler de Dune 3",
            resumen = "Denis Villeneuve confirma que la producción comenzará en 2026",
            fecha = "23 Oct 2025",
            categoria = "Estrenos",
            esFavorita = false
        )
    }

    val contenidoCompleto = """
        Denis Villeneuve ha confirmado oficialmente que Dune: Parte Tres comenzará su producción a principios de 2026. El director canadiense compartió la noticia durante una entrevista exclusiva en el Festival de Cine de Venecia.
        
        La tercera entrega adaptará el segundo libro de Frank Herbert, "Dune Messiah", y continuará la historia de Paul Atreides 12 años después de los eventos de Dune: Parte Dos.
        
        Villeneuve mencionó: "Dune Messiah es el libro que siempre quise adaptar. Completa el arco de personaje de Paul de una manera profunda y significativa. No es una secuela tradicional, es una meditación sobre el poder y sus consecuencias."
        
        Se espera que Timothée Chalamet y Zendaya regresen en sus roles principales, junto con nuevas incorporaciones al elenco que aún no han sido anunciadas.
        
        El presupuesto estimado de la producción es de 190 millones de dólares, y Warner Bros. ha confirmado una fecha tentativa de estreno para octubre de 2028.
    """.trimIndent()

    var esFavorita by remember { mutableStateOf(noticia.esFavorita) }
    val estadoScroll = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Noticia") },
                navigationIcon = {
                    IconButton(onClick = alVolverAtras) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { esFavorita = !esFavorita }) {
                        Icon(
                            imageVector = if (esFavorita) Icons.Default.Favorite
                            else Icons.Default.FavoriteBorder,
                            contentDescription = if (esFavorita) "Quitar de favoritos"
                            else "Agregar a favoritos",
                            tint = if (esFavorita) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { /* Compartir */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
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
                .padding(16.dp)
        ) {
            // Categoría
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = noticia.categoria,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título
            Text(
                text = noticia.titulo,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Fecha y autor
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = noticia.fecha,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Redacción CineVerse",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Imagen placeholder
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contenido
            Text(
                text = contenidoCompleto,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Ver más noticias similares */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Article, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Similares")
                }

                Button(
                    onClick = { /* Comentar */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Comment, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Comentar")
                }
            }
        }
    }
}