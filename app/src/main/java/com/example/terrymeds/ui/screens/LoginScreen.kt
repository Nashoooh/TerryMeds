package com.example.terrymeds.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.terrymeds.R
import com.example.terrymeds.data.UserData
import com.example.terrymeds.data.UserManager
import com.example.terrymeds.ui.theme.TerryMedsTheme
import androidx.compose.ui.layout.ContentScale

@Composable
fun LoginScreen(
    onLoginSuccess: (UserData) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }

    // val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.recetas))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        LottieAnimation(
//            composition = composition,
//            iterations = LottieConstants.IterateForever,
//            modifier = Modifier.wrapContentSize(Alignment.Center),
//            contentScale = ContentScale.Fit
//        )

        Text(
            text = "Inicio de Sesión",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; loginError = null },
            label = { Text("Correo Electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            isError = loginError != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; loginError = null },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            isError = loginError != null
        )

        loginError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Validaciones básicas
                if (email.isBlank() || password.isBlank()) {
                    loginError = "Correo y contraseña no pueden estar vacíos."
                    return@Button
                }

                // Llamar a la función de login de UserManager
                val loggedInUser = UserManager.loginUser(email.trim(), password)

                if (loggedInUser != null) {
                    println("LoginScreen: Login exitoso para ${loggedInUser.email}")
                    loginError = null
                    onLoginSuccess(loggedInUser)
                } else {
                    println("LoginScreen: Email o contraseña incorrectos.")
                    loginError = "Email o contraseña incorrectos."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = {
            onNavigateToRegister()
            println("Ir a Registro")
        }) {
            Text("¿No tienes cuenta? Regístrate")
        }

         TextButton(onClick = {
             onNavigateToForgotPassword()
             println("Ir a Recuperar Contraseña")
         }) {
             Text("¿Olvidaste tu contraseña?")
         }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TerryMedsTheme {
        LoginScreen(
            onLoginSuccess = { user ->
                println("Preview Login Exitoso: Email=${user.email}, Nombre=${user.firstName}, Apellido=${user.lastName}")
            },
            onNavigateToRegister = {
                println("Preview: Navegar a Registro")
            },
            onNavigateToForgotPassword = {
                println("Preview: Navegar a Olvidé Contraseña")
            }
        )
    }
}
