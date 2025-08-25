package com.example.terrymeds.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.terrymeds.data.UserManager
import com.example.terrymeds.ui.theme.TerryMedsTheme
import kotlinx.coroutines.launch

private enum class ResetPasswordStep {
    ENTER_EMAIL,
    RESET_PASSWORD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    onNavigateToLogin: () -> Unit,
) {
    var currentStep by remember { mutableStateOf(ResetPasswordStep.ENTER_EMAIL) }
    var emailInput by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var verifiedUserEmail by remember { mutableStateOf<String?>(null) }

    var newPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var passwordResetError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (currentStep == ResetPasswordStep.ENTER_EMAIL) "Verificar Email"
                        else "Restablecer Contraseña"
                    )
                },
                navigationIcon = {
                    // Mostrar flecha de atrás solo si estamos en el paso de resetear, para volver al paso de email
                    if (currentStep == ResetPasswordStep.RESET_PASSWORD) {
                        IconButton(onClick = {
                            currentStep = ResetPasswordStep.ENTER_EMAIL
                            // Limpiar errores y campos del paso anterior
                            passwordResetError = null
                            newPassword = ""
                            confirmPassword = ""
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = currentStep == ResetPasswordStep.ENTER_EMAIL,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                EmailVerificationStep(
                    email = emailInput,
                    onEmailChange = { emailInput = it; emailError = null },
                    emailError = emailError,
                    onVerifyEmail = {
                        if (emailInput.isBlank()) {
                            emailError = "El campo de email no puede estar vacío."
                            return@EmailVerificationStep
                        }

                        val user = UserManager.findUserByEmail(emailInput.trim())
                        if (user != null) {
                            verifiedUserEmail = user.email
                            currentStep = ResetPasswordStep.RESET_PASSWORD
                            emailError = null
                        } else {
                            emailError = "El correo electrónico no está registrado."
                        }
                    }
                )
            }

            AnimatedVisibility(
                visible = currentStep == ResetPasswordStep.RESET_PASSWORD,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                // Solo mostrar este paso si verifiedUserEmail no es null
                verifiedUserEmail?.let { validEmail ->
                    NewPasswordStep(
                        userEmail = validEmail,
                        newPassword = newPassword,
                        onNewPasswordChange = { newPassword = it; passwordResetError = null },
                        newPasswordVisible = newPasswordVisible,
                        onNewPasswordVisibilityChange = { newPasswordVisible = !newPasswordVisible },
                        confirmPassword = confirmPassword,
                        onConfirmPasswordChange = { confirmPassword = it; passwordResetError = null },
                        confirmPasswordVisible = confirmPasswordVisible,
                        onConfirmPasswordVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
                        passwordError = passwordResetError,
                        onResetPassword = {
                            // Validaciones
                            if (newPassword.isBlank() || confirmPassword.isBlank()) {
                                passwordResetError = "Los campos de contraseña no pueden estar vacíos."
                                return@NewPasswordStep
                            }
                            if (newPassword.length < 6) { // Ejemplo
                                passwordResetError = "La contraseña debe tener al menos 6 caracteres."
                                return@NewPasswordStep
                            }
                            if (newPassword != confirmPassword) {
                                passwordResetError = "Las contraseñas no coinciden."
                                return@NewPasswordStep
                            }

                            val success = UserManager.resetPassword(validEmail, newPassword)
                            if (success) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Contraseña restablecida con éxito.",
                                        duration = SnackbarDuration.Short
                                    )
                                    kotlinx.coroutines.delay(1L)
                                }
                                onNavigateToLogin()
                            } else {
                                passwordResetError = "Error al restablecer la contraseña. Inténtalo de nuevo."
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = passwordResetError!!,
                                        duration = SnackbarDuration.Long
                                    )
                                }
                            }
                        }
                    )
                } ?: run {
                    Text("Error: Email no verificado. Por favor, vuelve atrás.")
                    currentStep = ResetPasswordStep.ENTER_EMAIL
                }
            }
        }
    }
}

@Composable
private fun EmailVerificationStep(
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String?,
    onVerifyEmail: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Introduce tu correo electrónico para restablecer tu contraseña.",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Correo Electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = emailError != null,
            modifier = Modifier.fillMaxWidth()
        )
        emailError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onVerifyEmail, modifier = Modifier.fillMaxWidth()) {
            Text("Verificar Email")
        }
    }
}

@Composable
private fun NewPasswordStep(
    userEmail: String,
    newPassword: String,
    onNewPasswordChange: (String) -> Unit,
    newPasswordVisible: Boolean,
    onNewPasswordVisibilityChange: () -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmPasswordVisible: Boolean,
    onConfirmPasswordVisibilityChange: () -> Unit,
    passwordError: String?,
    onResetPassword: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Crea una nueva contraseña para\n$userEmail",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        OutlinedTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            label = { Text("Nueva Contraseña") },
            singleLine = true,
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = onNewPasswordVisibilityChange) {
                    Icon(imageVector = image, contentDescription = if (newPasswordVisible) "Ocultar" else "Mostrar")
                }
            },
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text("Confirmar Nueva Contraseña") },
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = onConfirmPasswordVisibilityChange) {
                    Icon(imageVector = image, contentDescription = if (confirmPasswordVisible) "Ocultar" else "Mostrar")
                }
            },
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth()
        )
        passwordError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onResetPassword, modifier = Modifier.fillMaxWidth()) {
            Text("Guardar Contraseña")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Step 1: Enter Email")
@Composable
fun ResetPasswordScreen_EnterEmailPreview() {
    TerryMedsTheme {

        var currentStep by remember { mutableStateOf(ResetPasswordStep.ENTER_EMAIL) }
        var emailInput by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf<String?>(null) }

        Scaffold(topBar = { TopAppBar(title = { Text("Verificar Email") }) }) { padding ->
            Column(Modifier.padding(padding).padding(16.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                if (currentStep == ResetPasswordStep.ENTER_EMAIL) {
                    EmailVerificationStep(
                        email = emailInput,
                        onEmailChange = { emailInput = it; emailError = null },
                        emailError = emailError,
                        onVerifyEmail = {
                            if (emailInput == "test@example.com") {
                                currentStep = ResetPasswordStep.RESET_PASSWORD
                            } else {
                                emailError = "Email no encontrado (preview)"
                            }
                        }
                    )
                } else {
                    Text("Simulando paso de reseteo de contraseña...")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Step 2: Reset Password Form")
@Composable
fun ResetPasswordScreen_ResetFormPreview() {
    TerryMedsTheme {
        Scaffold(topBar = { TopAppBar(title = { Text("Restablecer Contraseña") }) }) { padding ->
            Column(Modifier.padding(padding)) {
                NewPasswordStep(
                    userEmail = "usuario@example.com",
                    newPassword = "",
                    onNewPasswordChange = {},
                    newPasswordVisible = false,
                    onNewPasswordVisibilityChange = {},
                    confirmPassword = "",
                    onConfirmPasswordChange = {},
                    confirmPasswordVisible = false,
                    onConfirmPasswordVisibilityChange = {},
                    passwordError = null,
                    onResetPassword = {}
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Full ResetPasswordScreen")
@Composable
fun ResetPasswordScreenFullPreview() {
    TerryMedsTheme {
        ResetPasswordScreen(
            onNavigateToLogin = { println("PREVIEW: Navegando a login...") }
        )
    }
}
