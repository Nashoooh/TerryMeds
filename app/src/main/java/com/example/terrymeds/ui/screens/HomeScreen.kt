package com.example.terrymeds.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.terrymeds.data.sqlite.SQLiteMedicamentoManager
import com.example.terrymeds.data.MedicamentoUsuario
import com.example.terrymeds.data.FormaFarmaceutica
import com.example.terrymeds.data.UnidadDosis
import com.example.terrymeds.data.HoraDelDia


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    firstName: String?,
    lastName: String?,
    userEmail: String?,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val medicamentoManager = remember { SQLiteMedicamentoManager.getInstance(context) }
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
        // Estado para mostrar/ocultar el formulario de agregar medicamento
        var mostrarFormularioAgregar by remember { mutableStateOf(false) }
        // Estado para forzar recomposición
        var refreshTrigger by remember { mutableIntStateOf(0) }
        
        if (mostrarFormularioAgregar && userEmail != null) {
            AgregarMedicamentoScreen(
                userEmail = userEmail,
                onNavigateBack = { mostrarFormularioAgregar = false },
                onMedicamentoAgregado = {
                    mostrarFormularioAgregar = false
                    refreshTrigger++
                }
            )
        } else {
        
        // Obtener medicamentos activos cada vez que cambie refreshTrigger
        val medicamentosActivos by remember(refreshTrigger, userEmail) {
            derivedStateOf {
                userEmail?.let { 
                    medicamentoManager.getActiveMedicamentosByUserEmail(it) 
                } ?: emptyList()
            }
        }
        
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
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
                    
                    // Botón para agregar medicamento
                    FilledTonalButton(
                        onClick = { 
                            mostrarFormularioAgregar = true
                        },
                        modifier = Modifier.size(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Agregar medicamento",
                            modifier = Modifier.size(20.dp)
                        )
                    }
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
                items(
                    items = medicamentosActivos,
                    key = { medicamento -> "${medicamento.id}_$refreshTrigger" }
                ) { medicamento ->
                    MedicamentoCard(
                        medicamento = medicamento,
                        medicamentoManager = medicamentoManager,
                        onDelete = { medicamentoId ->
                            medicamentoManager.deleteMedicamento(medicamentoId)
                            refreshTrigger++ // Forzar recomposición
                        }
                    )
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
        } // Cierre del else
    }
}

@Composable
fun MedicamentoCard(
    medicamento: MedicamentoUsuario,
    medicamentoManager: SQLiteMedicamentoManager,
    onDelete: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header con nombre y botón de eliminar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nombre y concentración
                Column(
                    modifier = Modifier.weight(1f)
                ) {
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
                }
                
                // Botón de eliminar
                IconButton(
                    onClick = { onDelete(medicamento.id) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar medicamento",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
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
            
            // Intervalo y próxima dosis
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cada ${medicamento.intervaloEntreDosisHoras}h",
                    style = MaterialTheme.typography.bodySmall
                )
                val proximaDosis = medicamentoManager.getProximaHoraDosis(medicamento)
                Text(
                    text = "Próxima dosis: $proximaDosis",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
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
            Spacer(modifier = Modifier.height(8.dp))
            if (medicamento.numeroTotalDosis != null) {
                val diasRestantes = medicamentoManager.getDiasRestantesTratamiento(medicamento)
                when {
                    diasRestantes == null -> {
                        Text(
                            text = "📊 Duración no calculable",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    diasRestantes > 0 -> {
                        val textoRestante = if (diasRestantes == 1) "$diasRestantes día restante" else "$diasRestantes días restantes"
                        Text(
                            text = "📅 $textoRestante",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    else -> {
                        Text(
                            text = "⚠️ Tratamiento completado",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else {
                Text(
                    text = "📊 Tratamiento continuo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
