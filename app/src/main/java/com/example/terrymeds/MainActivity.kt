package com.example.terrymeds // CAMBIADO a com.example.terrymeds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.terrymeds.ui.screens.LoginScreen
import com.example.terrymeds.ui.screens.RegisterScreen
import com.example.terrymeds.ui.screens.HomeScreen
import com.example.terrymeds.ui.screens.ResetPasswordScreen
import com.example.terrymeds.ui.theme.TerryMedsTheme

// RUTAS
private const val LOGIN_ROUTE = "login"
private const val REGISTER_ROUTE = "register"
private const val HOME_ROUTE = "home"
private const val RESET_PASSWORD_ROUTE = "reset_password"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TerryMedsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LOGIN_ROUTE
    ) {
        composable(route = LOGIN_ROUTE) {
            LoginScreen(
                onLoginSuccess = { userData ->

                    val firstNameArg = userData.firstName ?: "null"
                    val lastNameArg = userData.lastName ?: "null"
                    val emailArg = userData.email ?: "null"

                    navController.navigate(
                        "$HOME_ROUTE/$firstNameArg/$lastNameArg/$emailArg"
                    ) {
                        popUpTo(LOGIN_ROUTE) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(REGISTER_ROUTE)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(RESET_PASSWORD_ROUTE)
                }
            )
        }

        composable(route = REGISTER_ROUTE) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(LOGIN_ROUTE) {
                        popUpTo(LOGIN_ROUTE) { inclusive = true }
                    }
                }
                // Si RegisterScreen necesita onShowSnackbar, asegúrate de tener Scaffold y SnackbarHostState allí.
            )
        }

        composable(
            route = "$HOME_ROUTE/{firstName}/{lastName}/{userEmail}",
            arguments = listOf(
                navArgument("firstName") { type = NavType.StringType; nullable = true },
                navArgument("lastName") { type = NavType.StringType; nullable = true },
                navArgument("userEmail") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val firstName = backStackEntry.arguments?.getString("firstName").let { if (it == "null") null else it }
            val lastName = backStackEntry.arguments?.getString("lastName").let { if (it == "null") null else it }
            val userEmail = backStackEntry.arguments?.getString("userEmail").let { if (it == "null") null else it }


            HomeScreen(
                firstName = firstName,
                lastName = lastName,
                userEmail = userEmail, // Solo estos parámetros son necesarios para HomeScreen según tu descripción
                onLogout = {
                    navController.navigate(LOGIN_ROUTE) {
                        popUpTo("$HOME_ROUTE/{firstName}/{lastName}/{userEmail}") { inclusive = true }
                        launchSingleTop = true
                    }
                }

            )
        }

        composable(route = RESET_PASSWORD_ROUTE) {
            ResetPasswordScreen(
                onNavigateToLogin = {
                    navController.navigate(LOGIN_ROUTE) {
                        popUpTo(LOGIN_ROUTE) { inclusive = true }
                    }
                }
            )
        }

    }
}
