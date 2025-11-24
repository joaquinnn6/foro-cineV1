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
import com.example.foro_cinev1.data.database.DatabaseHelper
import com.example.foro_cinev1.data.datastore.SessionManager
import com.example.foro_cinev1.domain.models.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    alVolverAtras: () -> Unit,
    alRegistrarseExitoso: () -> Unit
) {
    val contexto = LocalContext.current
    val dbHelper = remember { DatabaseHelper(contexto) }
    val sessionManager = remember { SessionManager(contexto) }
    val coroutineScope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var confirmarContraseña by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }

    var contraseñaVisible by remember { mutableStateOf(false) }
    var confirmarContraseñaVisible by remember { mutableStateOf(false) }

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorContraseña by remember { mutableStateOf<String?>(null) }
    var errorConfirmarContraseña by remember { mutableStateOf<String?>(null) }

    var mensajeErrorGlobal by remember { mutableStateOf<String?>(null) }
    var estaCargando by remember { mutableStateOf(false) }

    val administradorFoco = LocalFocusManager.current
    val estadoScroll = rememberScrollState()

    // Funciones de validación
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

    fun validarContraseña(contraseña: String): String? = when {
        contraseña.isBlank() -> "La contraseña es requerida"
        contraseña.length < 8 -> "Debe tener al menos 8 caracteres"
        !contraseña.any { it.isDigit() } -> "Debe contener al menos un número"
        !contraseña.any { it.isLowerCase() } -> "Debe contener al menos una minúscula"
        !contraseña.any { it.isUpperCase() } -> "Debe contener al menos una mayúscula"
        else -> null
    }

    fun validarConfirmarContraseña(contraseña: String, confirmar: String): String? = when {
        confirmar.isBlank() -> "Confirma tu contraseña"
        contraseña != confirmar -> "Las contraseñas no coinciden"
        else -> null
    }

    fun manejarRegistro() {
        errorNombre = validarNombre(nombre)
        errorCorreo = validarCorreo(correo)
        errorContraseña = validarContraseña(contraseña)
        errorConfirmarContraseña = validarConfirmarContraseña(contraseña, confirmarContraseña)

        if (errorNombre == null && errorCorreo == null &&
            errorContraseña == null && errorConfirmarContraseña == null
        ) {
            estaCargando = true
            mensajeErrorGlobal = null

            coroutineScope.launch {
                val nuevoUsuario = User(
                    id = 0,
                    nombre = nombre,
                    correo = correo,
                    contraseña = contraseña,
                    ubicacion = ubicacion
                )

                val exito = dbHelper.addUser(nuevoUsuario)
                if (exito) {
                    val usuario = dbHelper.loginUser(correo, contraseña)
                    usuario?.let {
                        sessionManager.guardarSesion(it.id, it.nombre, it.correo)
                    }
                    alRegistrarseExitoso()
                } else {
                    mensajeErrorGlobal = "El correo ya está registrado"
                }
                estaCargando = false
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

            // --- Campo Nombre ---
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

            // --- Campo Correo ---
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

            // --- Campo Ubicación (opcional) ---
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

            // --- Contraseña ---
            OutlinedTextField(
                value = contraseña,
                onValueChange = { contraseña = it; errorContraseña = null },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { contraseñaVisible = !contraseñaVisible }) {
                        Icon(
                            imageVector = if (contraseñaVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = "Mostrar/Ocultar"
                        )
                    }
                },
                visualTransformation = if (contraseñaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = errorContraseña != null,
                supportingText = {
                    AnimatedVisibility(visible = errorContraseña != null) {
                        Text(errorContraseña ?: "")
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

            // --- Confirmar Contraseña ---
            OutlinedTextField(
                value = confirmarContraseña,
                onValueChange = { confirmarContraseña = it; errorConfirmarContraseña = null },
                label = { Text("Confirmar contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { confirmarContraseñaVisible = !confirmarContraseñaVisible }) {
                        Icon(
                            imageVector = if (confirmarContraseñaVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = "Mostrar/Ocultar"
                        )
                    }
                },
                visualTransformation = if (confirmarContraseñaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = errorConfirmarContraseña != null,
                supportingText = {
                    AnimatedVisibility(visible = errorConfirmarContraseña != null) {
                        Text(errorConfirmarContraseña ?: "")
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

            // --- Botón de registro ---
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

            // Mensaje global de error
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
