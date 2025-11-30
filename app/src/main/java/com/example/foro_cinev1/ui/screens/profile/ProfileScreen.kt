package com.example.foro_cinev1.ui.screens.profile

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.example.foro_cinev1.data.datastore.SessionManager
import com.example.foro_cinev1.data.repository.UserRepository
import com.example.foro_cinev1.utils.LocationHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    alVolverAtras: () -> Unit,
    alCerrarSesion: () -> Unit,
    // Inyección de dependencias para tests
    sessionManager: SessionManager = SessionManager(LocalContext.current),
    locationHelper: LocationHelper = LocationHelper(LocalContext.current),
    userRepository: UserRepository = UserRepository()
) {
    val userId = sessionManager.obtenerId() ?: -1

    var nombre by remember { mutableStateOf(sessionManager.obtenerNombre() ?: "Usuario") }
    var correo by remember { mutableStateOf(sessionManager.obtenerCorreo() ?: "correo@ejemplo.com") }
    var ubicacion by remember { mutableStateOf("Cargando ubicación…") }
    var fotoUri by remember { mutableStateOf(sessionManager.obtenerFotoPerfil()?.toUri()) }

    var modoEdicion by remember { mutableStateOf(false) }
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }

    val estadoScroll = rememberScrollState()
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }
    var guardando by remember { mutableStateOf(false) }

    // ✅ Permisos de ubicación
    val launcherPermisoUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val permisoConcedido =
            permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        scope.launch {
            if (permisoConcedido) {
                ubicacion = "Cargando ubicación…"
                val ciudad = locationHelper.obtenerCiudadActual()
                ubicacion = ciudad ?: "Ubicación desconocida"
            } else {
                ubicacion = "Permiso denegado"
            }
        }
    }

    // 🔄 Pedir permisos al entrar
    LaunchedEffect(Unit) {
        launcherPermisoUbicacion.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // 🖼️ Selector de imagen desde galería
    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            fotoUri = it
        }
    }

    // 👉 Función para guardar cambios (backend + SessionManager)
    fun guardarCambios() {
        if (userId <= 0) {
            scope.launch {
                snackbarHostState.showSnackbar("No se pudo identificar al usuario.")
            }
            return
        }

        scope.launch {
            guardando = true
            try {
                val fotoString = fotoUri?.toString()

                // No mandamos ubicaciones "fake"
                val ubicacionLimpia =
                    if (ubicacion.isBlank() ||
                        ubicacion == "Cargando ubicación…" ||
                        ubicacion == "Permiso denegado"
                    ) null
                    else ubicacion

                // Si no hay nada que cambiar, avisamos
                if (
                    (nombre == (sessionManager.obtenerNombre() ?: "Usuario")) &&
                    (ubicacionLimpia == null) &&
                    (fotoString == sessionManager.obtenerFotoPerfil())
                ) {
                    snackbarHostState.showSnackbar("No hay cambios para guardar")
                    guardando = false
                    return@launch
                }

                val actualizado = userRepository.updateUserProfile(
                    id = userId.toLong(),
                    nombre = nombre,
                    ubicacion = ubicacionLimpia,
                    profileImageUrl = fotoString
                )

                if (actualizado != null) {
                    // 👇 Recuperamos el rol actual o usamos el que venga del backend
                    val rolActual = sessionManager.obtenerRol() ?: actualizado.role

                    sessionManager.guardarSesion(
                        id = actualizado.id,
                        nombre = actualizado.nombre,
                        correo = actualizado.correo,
                        rol = rolActual
                    )

                    sessionManager.guardarFotoPerfil(
                        actualizado.profileImageUrl ?: fotoString.orEmpty()
                    )

                    snackbarHostState.showSnackbar("Perfil actualizado correctamente")
                    modoEdicion = false
                } else {
                    snackbarHostState.showSnackbar("No se pudo actualizar el perfil")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                snackbarHostState.showSnackbar("Error al actualizar el perfil")
            } finally {
                guardando = false
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = alVolverAtras) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (modoEdicion) {
                        TextButton(
                            onClick = { if (!guardando) guardarCambios() },
                            enabled = !guardando
                        ) {
                            if (guardando) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Guardar", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        IconButton(onClick = { modoEdicion = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
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
        ) {
            // 🧍 Foto de perfil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        if (fotoUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(fotoUri),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    if (modoEdicion) {
                        TextButton(onClick = { launcherGaleria.launch("image/*") }) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cambiar foto")
                        }
                    }
                }
            }

            Divider()

            // 📋 Información del perfil
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Información Personal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (modoEdicion) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    ItemPerfil(Icons.Default.Person, "Nombre", nombre)
                }

                ItemPerfil(Icons.Default.Email, "Correo electrónico", correo)
                ItemPerfil(Icons.Default.LocationOn, "Ubicación", ubicacion)

                Divider()

                TarjetaEstadistica("12", "Publicaciones", Icons.Default.Forum)
                TarjetaEstadistica("45", "Comentarios", Icons.Default.Comment)

                OutlinedButton(
                    onClick = { mostrarDialogoCerrarSesion = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión")
                }
            }
        }
    }

    if (mostrarDialogoCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrarSesion = false },
            icon = { Icon(Icons.Default.Logout, contentDescription = null) },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("¿Seguro que deseas cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch { sessionManager.cerrarSesion() }
                        mostrarDialogoCerrarSesion = false
                        alCerrarSesion()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCerrarSesion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ItemPerfil(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    valor: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column {
                Text(titulo, style = MaterialTheme.typography.labelMedium)
                Text(valor, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun TarjetaEstadistica(
    numero: String,
    etiqueta: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icono, contentDescription = null)
            Text("$numero $etiqueta")
        }
    }
}
