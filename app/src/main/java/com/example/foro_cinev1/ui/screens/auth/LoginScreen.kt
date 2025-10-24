package com.example.foro_cinev1.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    alIrARegistro: () -> Unit,
    alLoguearseExitoso: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var contraseñaVisible by remember { mutableStateOf(false) }
    var errorCorreo by remember { mutableStateOf<String?>(null) }
    var errorContraseña by remember { mutableStateOf<String?>(null) }
    var estaCargando by remember { mutableStateOf(false) }

    val administradorFoco = LocalFocusManager.current

    fun validarCorreo(correo: String): String? {
        return when {
            correo.isBlank() -> "El correo es requerido"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches() ->
                "Formato de correo inválido"
            else -> null
        }
    }

    fun validarContraseña(contraseña: String): String? {
        return when {
            contraseña.isBlank() -> "La contraseña es requerida"
            contraseña.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            !contraseña.any { it.isDigit() } -> "Debe contener al menos un número"
            !contraseña.any { it.isLowerCase() } -> "Debe contener al menos una minúscula"
            !contraseña.any { it.isUpperCase() } -> "Debe contener al menos una mayúscula"
            else -> null
        }
    }

    fun manejarLogin() {
        errorCorreo = validarCorreo(correo)
        errorContraseña = validarContraseña(contraseña)

        if (errorCorreo == null && errorContraseña == null) {
            estaCargando = true
            // Aquí tu compañero conectará con el backend
            alLoguearseExitoso()
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
            // Logo/Icono
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

            // Campo de correo
            OutlinedTextField(
                value = correo,
                onValueChange = {
                    correo = it
                    errorCorreo = null
                },
                label = { Text("Correo electrónico") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
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

            // Campo de contraseña
            OutlinedTextField(
                value = contraseña,
                onValueChange = {
                    contraseña = it
                    errorContraseña = null
                },
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { contraseñaVisible = !contraseñaVisible }) {
                        Icon(
                            imageVector = if (contraseñaVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = if (contraseñaVisible) "Ocultar contraseña"
                            else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (contraseñaVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
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

            // Botón de login
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

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de registro
            TextButton(onClick = alIrARegistro) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}
