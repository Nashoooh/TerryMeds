package com.example.terrymeds.ui.screens

// Tus importaciones actuales, algunas podrían ya no ser necesarias si eliminas
// elementos como Lottie o LazyColumn si no se usan más.
// Por ahora, mantendré las que tenías por si las reutilizas.
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight // No se usa directamente en este ejemplo simplificado, pero puede ser útil
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
// Las importaciones de Lottie, LocalDate, TextStyle, Locale, R, TerryMedsTheme
// podrían no ser necesarias si HomeScreen ya no las usa.
// import com.airbnb.lottie.compose.LottieAnimation
// import com.airbnb.lottie.compose.LottieCompositionSpec
// import com.airbnb.lottie.compose.LottieConstants
// import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.terrymeds.ui.theme.TerryMedsTheme
// import com.example.terrymeds.R // Solo si usas recursos directos aquí
// import java.time.LocalDate
// import java.time.format.TextStyle
// import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    firstName: String?,
    lastName: String?,
    userEmail: String?,
    onLogout: () -> Unit
    // Ya NO se reciben: onNavigateToAllRecipes, onRecipeClick
) {
    Scaffold(
        topBar = {
            val appBarTitle = if (firstName != null || lastName != null) { // Cambiado a OR para mostrar el nombre si alguno está presente
                "Inicio - ${firstName ?: ""} ${lastName ?: ""}".trim()
            } else if (userEmail != null) {
                "Inicio - $userEmail" // Mostrar email si no hay nombre
            }
            else {
                "Inicio"
            }
            TopAppBar(
                title = { Text(appBarTitle) }
                // Puedes añadir acciones aquí si es necesario, como el botón de logout
                // actions = {
                //    IconButton(onClick = onLogout) {
                //        Icon(Icons.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                //    }
                // }
            )
        }
    ) { innerPadding ->
        // Cambiado de LazyColumn a Column ya que el contenido es simple y fijo
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Padding del Scaffold
                .padding(16.dp),       // Padding adicional para el contenido
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Para centrar el contenido verticalmente
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
                textAlign = TextAlign.Center
            )

            userEmail?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Email: $it",
                    style = MaterialTheme.typography.bodyLarge, // Un poco más grande para el email
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp)) // Más espacio antes del botón

            Button(onClick = onLogout) {
                Text("Cerrar Sesión")
            }

            // Aquí ya no iría nada relacionado con recetas
        }
    }
}



