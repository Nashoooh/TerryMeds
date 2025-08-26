package com.example.terrymeds.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons // Necesario para el ícono de ejemplo
import androidx.compose.material.icons.filled.ExitToApp // Ícono de ejemplo para logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    firstName: String?,
    lastName: String?,
    userEmail: String?,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            val appBarTitle = if (firstName != null || lastName != null) {
                "Inicio - ${firstName ?: ""} ${lastName ?: ""}".trim()
            } else if (userEmail != null) {
                "Inicio - $userEmail"
            } else {
                "Inicio"
            }
            TopAppBar(
                title = {
                    Text(appBarTitle)
                },
                 actions = {
                     IconButton(onClick = onLogout) {
                         Icon(
                             imageVector = Icons.Filled.ExitToApp,
                             contentDescription = "Cerrar Sesión"
                         )
                     }
                 }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val welcomeMessage = if (firstName != null || lastName != null) {
                "¡Bienvenido/a, ${firstName ?: ""} ${lastName ?: ""}".trim() + "!"
            } else if (userEmail != null) {
                "¡Bienvenido/a, $userEmail!"
            } else {
                "¡Bienvenido/a!"
            }
            Text(
                text = welcomeMessage,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() }
            )

            userEmail?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Email: $it",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLogout

            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}
