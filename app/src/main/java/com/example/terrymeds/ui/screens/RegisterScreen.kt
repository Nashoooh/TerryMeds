package com.example.terrymeds.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
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
import com.example.terrymeds.data.UserData
import com.example.terrymeds.data.UserManager
import com.example.terrymeds.ui.theme.TerryMedsTheme
import kotlinx.coroutines.launch // Para las coroutines del Snackbar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit = {}
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val selectedDateInMillis = datePickerState.selectedDateMillis

    val formattedDate = remember(selectedDateInMillis) {
        selectedDateInMillis?.let {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
        } ?: "Seleccionar fecha"
    }

    // Para el Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold( // Añadimos Scaffold para poder usar SnackbarHost
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Aplicar padding del Scaffold
                .padding(16.dp), // Padding adicional para el contenido
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = formattedDate,
                onValueChange = { /* No editable */ },
                label = { Text("Fecha de Nacimiento") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerDialog = true },
                trailingIcon = {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = "Seleccionar fecha",
                        modifier = Modifier.clickable { showDatePickerDialog = true }
                    )
                }
            )

            if (showDatePickerDialog) {
                DatePickerDialog(
                    onDismissRequest = { showDatePickerDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showDatePickerDialog = false }) { Text("Aceptar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePickerDialog = false }) { Text("Cancelar") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank() && password.isNotBlank() && selectedDateInMillis != null) {
                        // Comprobar si el email ya existe
                        if (UserManager.findUserByEmail(email.trim()) != null) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "El correo electrónico ya está registrado.",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        } else {
                            val newUser = UserData(
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                email = email.trim(),
                                password = password,
                                birthDate = selectedDateInMillis
                            )
                            UserManager.addUser(newUser)
                            UserManager.printAllUsers()

                            // Limpiar campos
                            firstName = ""
                            lastName = ""
                            email = ""
                            password = ""
                            // datePickerState.selectedDateMillis = null // Opcional resetear fecha

                            // Mostrar mensaje de éxito
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Registro Exitoso",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            onNavigateToLogin()

                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Por favor, completa todos los campos.",
                                duration = SnackbarDuration.Short
                            )
                        }
                        println("Por favor, completa todos los campos, incluyendo la fecha de nacimiento.")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text("¿Ya tienes cuenta? Inicia Sesión")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    TerryMedsTheme {
        RegisterScreen()
    }
}

