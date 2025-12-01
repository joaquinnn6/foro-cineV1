package com.example.foro_cinev1.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.example.foro_cinev1.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    alIrARegistro: () -> Unit,
    alLoguearseExitoso: () -> Unit
) {
    val contexto = LocalContext.current
    val sessionManager = remember { SessionManager(contexto) }
    val authViewModel = remember { AuthViewModel() }
    val scope = rememberCoroutineScope()

    var correo by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var contraseñaVisible by remember { mutableStateOf(false) }

    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorContraseña by remember { mutableStateOf<String?>(null) }
    var mensajeErrorGlobal by remember { mutableStateOf<String?>(null) }

    var estaCargando by remember { mutableStateOf(false) }

    val administradorFoco = LocalFocusManager.current

    fun validarCorreo(correo: String): String? = when {
        correo.isBlank() -> "El correo es requerido"
        !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() ->
            "Formato de correo inválido"
        else -> null
    }

    fun validarContraseña(contraseña: String): String? = when {
        contraseña.isBlank() -> "La contraseña es requerida"
        contraseña.length < 8 -> "Debe tener al menos 8 caracteres"
        else -> null
    }

    fun manejarLogin() {
        errorCorreo = validarCorreo(correo)
        errorContraseña = validarContraseña(contraseña)
        mensajeErrorGlobal = null

        if (errorCorreo == null && errorContraseña == null) {
            estaCargando = true

            authViewModel.iniciarSesion(correo, contraseña) { user ->
                scope.launch {
                    if (user != null) {
                        // ✅ Guardamos también el rol que viene del backend
                        sessionManager.guardarSesion(
                            id = user.id,
                            nombre = user.nombre,
                            correo = user.correo,
                            rol = user.role
                        )

                        // ✅ Guardar foto de perfil si viene
                        user.profileImageUrl
                            ?.takeIf { it.isNotBlank() }
                            ?.let { sessionManager.guardarFotoPerfil(it) }

                        alLoguearseExitoso()
                    } else {
                        mensajeErrorGlobal =
                            "Correo o contraseña incorrectos o error de conexión."
                    }
                    estaCargando = false
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CineVerse",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    errorCorreo = null
                },
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
                value = contraseña,
                onValueChange = {
                    contraseña = it
                    errorContraseña = null
                },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { contraseñaVisible = !contraseñaVisible }) {
                        Icon(
                            imageVector = if (contraseñaVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = if (contraseñaVisible)
                                "Ocultar contraseña"
                            else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (contraseñaVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                isError = errorContraseña != null,
                supportingText = {
                    AnimatedVisibility(visible = errorContraseña != null) {
                        Text(errorContraseña ?: "")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        administradorFoco.clearFocus()
                        manejarLogin()
                    }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { manejarLogin() },
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
                    Text("Iniciar Sesión")
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

            TextButton(onClick = alIrARegistro) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}
