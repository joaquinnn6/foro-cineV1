package com.example.foro_cinev1.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.foro_cinev1.data.datastore.SessionManager
import com.example.foro_cinev1.domain.models.User
import com.example.foro_cinev1.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    alVolverAtras: () -> Unit,
    alRegistrarseExitoso: () -> Unit,
    // Inyección de dependencias para tests
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    sessionManager: SessionManager = SessionManager(LocalContext.current)
) {
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }

    var contrasenaVisible by remember { mutableStateOf(false) }
    var confirmarContrasenaVisible by remember { mutableStateOf(false) }

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorContrasena by remember { mutableStateOf<String?>(null) }
    var errorConfirmarContrasena by remember { mutableStateOf<String?>(null) }

    var mensajeErrorGlobal by remember { mutableStateOf<String?>(null) }
    var estaCargando by remember { mutableStateOf(false) }

    val administradorFoco = LocalFocusManager.current
    val estadoScroll = rememberScrollState()

    fun validarNombre(nombre: String): String? = when {
        nombre.isBlank() -> "El nombre es requerido"
        nombre.length < 3 -> "Debe tener al menos 3 caracteres"
        else -> null
    }

    fun validarCorreo(correo: String): String? = when {
        correo.isBlank() -> "El correo es requerido"
        !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() ->
            "Formato de correo inválido"
        else -> null
    }

    fun validarContrasena(contrasena: String): String? = when {
        contrasena.isBlank() -> "La contraseña es requerida"
        contrasena.length < 8 -> "Debe tener al menos 8 caracteres"
        !contrasena.any { it.isDigit() } -> "Debe contener al menos un número"
        !contrasena.any { it.isLowerCase() } -> "Debe contener al menos una minúscula"
        !contrasena.any { it.isUpperCase() } -> "Debe contener al menos una mayúscula"
        else -> null
    }

    fun validarConfirmarContraseña(contrasena: String, confirmar: String): String? = when {
        confirmar.isBlank() -> "Confirma tu contraseña"
        contrasena != confirmar -> "Las contraseñas no coinciden"
        else -> null
    }

    fun manejarRegistro() {
        errorNombre = validarNombre(nombre)
        errorCorreo = validarCorreo(correo)
        errorContrasena = validarContrasena(contrasena)
        errorConfirmarContrasena = validarConfirmarContraseña(contrasena, confirmarContrasena)

        if (errorNombre == null && errorCorreo == null &&
            errorContrasena == null && errorConfirmarContrasena == null
        ) {
            estaCargando = true
            mensajeErrorGlobal = null

            val nuevoUsuario = User(
                id = 0,
                nombre = nombre,
                correo = correo,
                contrasena = contrasena,
                ubicacion = ubicacion,
                profileImageUrl = null
                // role se va con el valor por defecto "USER" del data class
            )

            viewModel.registrarUsuario(nuevoUsuario) { exito ->
                if (exito) {
                    // Login automático
                    viewModel.iniciarSesion(correo, contrasena) { user ->
                        scope.launch {
                            if (user != null) {
                                // ✅ Guardamos también el rol
                                sessionManager.guardarSesion(
                                    id = user.id,
                                    nombre = user.nombre,
                                    correo = user.correo,
                                    rol = user.role
                                )

                                user.profileImageUrl
                                    ?.takeIf { it.isNotBlank() }
                                    ?.let { sessionManager.guardarFotoPerfil(it) }

                                alRegistrarseExitoso()
                            } else {
                                mensajeErrorGlobal = "Registrado, pero falló al iniciar sesión."
                            }
                            estaCargando = false
                        }
                    }
                } else {
                    mensajeErrorGlobal = "El correo ya está registrado o hubo un error de red."
                    estaCargando = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = {
                    IconButton(onClick = alVolverAtras) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Únete a CineVerse",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; errorNombre = null },
                label = { Text("Nombre completo") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = errorNombre != null,
                supportingText = {
                    AnimatedVisibility(visible = errorNombre != null) {
                        Text(errorNombre ?: "")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { administradorFoco.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it; errorCorreo = null },
                label = { Text("Correo electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                isError = errorCorreo != null,
                supportingText = {
                    AnimatedVisibility(visible = errorCorreo != null) {
                        Text(errorCorreo ?: "")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { administradorFoco.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación (opcional)") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { administradorFoco.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it; errorContrasena = null },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                        Icon(
                            imageVector = if (contrasenaVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = "Mostrar/Ocultar"
                        )
                    }
                },
                visualTransformation = if (contrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = errorContrasena != null,
                supportingText = {
                    AnimatedVisibility(visible = errorContrasena != null) {
                        Text(errorContrasena ?: "")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { administradorFoco.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmarContrasena,
                onValueChange = { confirmarContrasena = it; errorConfirmarContrasena = null },
                label = { Text("Confirmar contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { confirmarContrasenaVisible = !confirmarContrasenaVisible }) {
                        Icon(
                            imageVector = if (confirmarContrasenaVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = "Mostrar/Ocultar"
                        )
                    }
                },
                visualTransformation = if (confirmarContrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = errorConfirmarContrasena != null,
                supportingText = {
                    AnimatedVisibility(visible = errorConfirmarContrasena != null) {
                        Text(errorConfirmarContrasena ?: "")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { manejarRegistro() }),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { manejarRegistro() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !estaCargando
            ) {
                if (estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Crear Cuenta")
                }
            }

            AnimatedVisibility(visible = mensajeErrorGlobal != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = mensajeErrorGlobal ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = alVolverAtras) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}
