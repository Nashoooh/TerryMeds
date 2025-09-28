package com.example.terrymeds.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.example.terrymeds.R
import com.example.terrymeds.data.UserData
import com.example.terrymeds.data.sqlite.SQLiteUserManager
import androidx.compose.ui.platform.LocalContext

@Composable
fun LoginScreen(
    onLoginSuccess: (UserData) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val userManager = remember { SQLiteUserManager.getInstance(context) }

    val lottieCompositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.calendar_drug))
    val lottieComposition = lottieCompositionResult.value

    val progress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever,
    )

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        if (lottieComposition != null) {
            LottieAnimation(
                composition = lottieComposition,
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = screenHeight * 0.4f)
                    .semantics { this.contentDescription = "Animación decorativa de calendario y pastillas" },
                contentScale = ContentScale.Fit
            )
        } else {
            Spacer(modifier = Modifier.height(screenHeight * 0.4f)) // Mantiene el espacio
        }

        Text(
            text = "Inicio de Sesión",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; loginError = null },
            label = { Text("Correo Electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = loginError != null && email.isBlank()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; loginError = null },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    // Lógica de login (refactorizar o duplicar del botón)
                    if (email.isNotBlank() && password.isNotBlank()) {
                        val loggedInUser = userManager.loginUser(email.trim(), password)
                        if (loggedInUser != null) {
                            loginError = null
                            onLoginSuccess(loggedInUser)
                        } else {
                            loginError = "Correo electrónico o contraseña incorrectos."
                        }
                    } else if (email.isBlank() && password.isBlank()) {
                        loginError = "Correo electrónico y contraseña no pueden estar vacíos."
                    } else if (email.isBlank()) {
                        loginError = "El correo electrónico no puede estar vacío."
                    } else {
                        loginError = "La contraseña no puede estar vacía."
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            isError = loginError != null && password.isBlank()
        )

        loginError?.let { errorText ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isBlank() && password.isBlank()) {
                    loginError = "Correo electrónico y contraseña no pueden estar vacíos."
                    return@Button
                }
                if (email.isBlank()) {
                    loginError = "El correo electrónico no puede estar vacío."
                    return@Button
                }
                if (password.isBlank()) {
                    loginError = "La contraseña no puede estar vacía."
                    return@Button
                }

                val loggedInUser = userManager.loginUser(email.trim(), password)

                if (loggedInUser != null) {
                    loginError = null
                    onLoginSuccess(loggedInUser)
                } else {
                    loginError = "Correo electrónico o contraseña incorrectos."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            onNavigateToRegister()
        }) {
            Text("¿No tienes cuenta? Regístrate")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            onNavigateToForgotPassword()
        }) {
            Text("¿Olvidaste tu contraseña?")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

