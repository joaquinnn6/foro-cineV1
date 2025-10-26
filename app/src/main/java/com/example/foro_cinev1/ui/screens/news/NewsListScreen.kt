package com.example.foro_cinev1.ui.screens.news

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.foro_cinev1.viewmodel.NewsViewModel
import com.example.foro_cinev1.domain.models.News

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListScreen(
    alVolverAtras: () -> Unit,
    alIrADetalle: (Int) -> Unit
) {
    val contexto = LocalContext.current
    val viewModel = remember { NewsViewModel(contexto) }
    val noticias by viewModel.noticias.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarNoticias()
        if (noticias.isEmpty()) {
            // Insertamos algunas noticias demo la primera vez
            repeat(3) { viewModel.agregarNoticiaDemo() }
            viewModel.cargarNoticias()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Noticias de Cine 🎥", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = alVolverAtras) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (noticias.isEmpty()) {
                item {
                    Text(
                        "No hay noticias disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                items(noticias) { noticia ->
                    TarjetaNoticia(noticia) { alIrADetalle(noticia.id) }
                }
            }
        }
    }
}

@Composable
fun TarjetaNoticia(noticia: News, alHacerClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = alHacerClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                noticia.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                noticia.resumen,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Por ${noticia.autor} - ${noticia.fecha}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
