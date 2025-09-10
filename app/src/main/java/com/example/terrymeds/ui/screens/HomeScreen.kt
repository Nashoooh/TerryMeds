package com.example.terrymeds.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons // Necesario para el ícono de ejemplo
import androidx.compose.material.icons.filled.ExitToApp // Ícono de ejemplo para logout
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.terrymeds.data.MedicamentoManager
import com.example.terrymeds.data.MedicamentoUsuario


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
        }        ) { innerPadding ->
        val medicamentosActivos = userEmail?.let { 
            MedicamentoManager.getActiveMedicamentosByUserEmail(it) 
        } ?: emptyList()
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
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
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Medication,
                        contentDescription = "Medicamentos",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mis Medicamentos Activos",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (medicamentosActivos.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No tienes medicamentos activos",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(medicamentosActivos) { medicamento ->
                    MedicamentoCard(medicamento = medicamento)
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onLogout
                ) {
                    Text("Cerrar Sesión")
                }
            }
        }
    }
}

@Composable
fun MedicamentoCard(medicamento: MedicamentoUsuario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Nombre y concentración
            Text(
                text = medicamento.nombreMedicamento,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            medicamento.concentracion?.let { concentracion ->
                Text(
                    text = concentracion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Forma farmacéutica y dosis
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Forma: ${medicamento.formaFarmaceutica.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${medicamento.cantidadPorDosis} ${medicamento.unidadDosis.name.lowercase()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Intervalo y hora de inicio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cada ${medicamento.intervaloEntreDosisHoras}h",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Inicio: ${medicamento.horaInicioTratamiento}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            // Instrucciones adicionales
            medicamento.instruccionesAdicionales?.let { instrucciones ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "💡 $instrucciones",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Días restantes del tratamiento
            medicamento.numeroTotalDosis?.let { totalDosis ->
                val diasRestantes = MedicamentoManager.getDiasRestantesTratamiento(medicamento)
                diasRestantes?.let { dias ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (dias > 0) "📅 $dias días restantes" else "⚠️ Tratamiento completado",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (dias > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
