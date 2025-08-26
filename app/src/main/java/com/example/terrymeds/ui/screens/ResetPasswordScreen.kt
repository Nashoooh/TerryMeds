package com.example.terrymeds.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
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

    val screenTitle = if (currentStep == ResetPasswordStep.ENTER_EMAIL) {
        "Verificar Correo Electrónico"
    } else {
        "Restablecer Contraseña"
    }

    LaunchedEffect(currentStep) {
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(screenTitle) },
                navigationIcon = {
                    if (currentStep == ResetPasswordStep.RESET_PASSWORD) {
                        IconButton(onClick = {
                            currentStep = ResetPasswordStep.ENTER_EMAIL
                            passwordResetError = null
                            newPassword = ""
                            confirmPassword = ""
                            emailError = null
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver a verificación de correo"
                            )
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
                    onEmailChange = {
                        emailInput = it
                        emailError = null
                    },
                    emailError = emailError,
                    onVerifyEmail = {
                        if (emailInput.isBlank()) {
                            emailError = "El campo de correo no puede estar vacío."
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
                verifiedUserEmail?.let { validEmail ->
                    NewPasswordStep(
                        userEmail = validEmail,
                        newPassword = newPassword,
                        onNewPasswordChange = {
                            newPassword = it
                            passwordResetError = null
                        },
                        newPasswordVisible = newPasswordVisible,
                        onNewPasswordVisibilityChange = { newPasswordVisible = !newPasswordVisible },
                        confirmPassword = confirmPassword,
                        onConfirmPasswordChange = {
                            confirmPassword = it
                            passwordResetError = null
                        },
                        confirmPasswordVisible = confirmPasswordVisible,
                        onConfirmPasswordVisibilityChange = { confirmPasswordVisible = !confirmPasswordVisible },
                        passwordError = passwordResetError,
                        onResetPassword = {
                            if (newPassword.isBlank() || confirmPassword.isBlank()) {
                                passwordResetError = "Los campos de contraseña no pueden estar vacíos."
                                return@NewPasswordStep
                            }
                            if (newPassword.length < 6) {
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
                                        message = "Contraseña restablecida con éxito. Serás redirigido.",
                                        duration = SnackbarDuration.Long
                                    )
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
                    Text(
                        "Error: Correo electrónico no verificado. Por favor, vuelve atrás.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive }
                    )
                    LaunchedEffect(Unit) {
                        currentStep = ResetPasswordStep.ENTER_EMAIL
                    }
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
    val focusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Introduce tu correo electrónico para encontrar tu cuenta.",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .semantics { heading() }
        )
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Correo Electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onVerifyEmail()
                }
            ),
            isError = emailError != null,
            modifier = Modifier.fillMaxWidth()
        )
        emailError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onVerifyEmail,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verificar Correo")
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
    val focusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Crea una nueva contraseña para\n$userEmail",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .semantics { heading() }
        )
        OutlinedTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            label = { Text("Nueva Contraseña") },
            singleLine = true,
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            trailingIcon = {
                val image = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (newPasswordVisible) "Ocultar nueva contraseña" else "Mostrar nueva contraseña"
                IconButton(onClick = onNewPasswordVisibilityChange) {
                    Icon(imageVector = image, contentDescription = description)
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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onResetPassword()
                }
            ),
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (confirmPasswordVisible) "Ocultar confirmación de contraseña" else "Mostrar confirmación de contraseña"
                IconButton(onClick = onConfirmPasswordVisibilityChange) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth()
        )
        passwordError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onResetPassword,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Nueva Contraseña")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Step 1: Enter Email")
@Composable
fun ResetPasswordScreen_EnterEmailPreview() {
    TerryMedsTheme {
        var currentStep by remember { mutableStateOf(ResetPasswordStep.ENTER_EMAIL) }
        var emailInput by remember { mutableStateOf("test@example.com") }
        var emailError by remember { mutableStateOf<String?>("Email no encontrado (preview)") }

        Scaffold(topBar = { TopAppBar(title = { Text("Verificar Correo Electrónico") }) }) { padding ->
            Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (currentStep == ResetPasswordStep.ENTER_EMAIL) {
                    EmailVerificationStep(
                        email = emailInput,
                        onEmailChange = { emailInput = it; emailError = null },
                        emailError = emailError,
                        onVerifyEmail = {
                            if (emailInput == "test@example.com" && emailError == null) { // Solo cambiar si no hay error simulado
                                currentStep = ResetPasswordStep.RESET_PASSWORD
                            } else if (emailError == null) {
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
