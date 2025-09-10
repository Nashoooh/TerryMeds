package com.example.terrymeds.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.terrymeds.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarMedicamentoScreen(
    userEmail: String,
    onNavigateBack: () -> Unit,
    onMedicamentoAgregado: () -> Unit
) {
    var nombreMedicamento by remember { mutableStateOf("") }
    var concentracion by remember { mutableStateOf("") }
    var cantidadPorDosis by remember { mutableStateOf("1") }
    var numeroTotalDosis by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("8") }
    var minutoInicio by remember { mutableStateOf("0") }
    var intervaloHoras by remember { mutableStateOf("8") }
    var instrucciones by remember { mutableStateOf("") }
    var formaFarmaceuticaSeleccionada by remember { mutableStateOf(FormaFarmaceutica.COMPRIMIDO) }
    var unidadDosisSeleccionada by remember { mutableStateOf(UnidadDosis.UNIDAD) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Medicamento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nombre del medicamento
            OutlinedTextField(
                value = nombreMedicamento,
                onValueChange = { nombreMedicamento = it },
                label = { Text("Nombre del medicamento *") },
                placeholder = { Text("Ej: Paracetamol, Ibuprofeno") },
                modifier = Modifier.fillMaxWidth()
            )

            // Concentración
            OutlinedTextField(
                value = concentracion,
                onValueChange = { concentracion = it },
                label = { Text("Concentración (ej: 500 mg, 10 ml)") },
                placeholder = { Text("Ej: 500 mg, 10 ml") },
                modifier = Modifier.fillMaxWidth()
            )

            // Forma farmacéutica
            Text(
                text = "Forma farmacéutica",
                style = MaterialTheme.typography.titleMedium
            )
            Column(
                modifier = Modifier.selectableGroup()
            ) {
                FormaFarmaceutica.values().forEach { forma ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (forma == formaFarmaceuticaSeleccionada),
                                onClick = { formaFarmaceuticaSeleccionada = forma },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (forma == formaFarmaceuticaSeleccionada),
                            onClick = null
                        )
                        Text(
                            text = forma.name.lowercase().replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Cantidad por dosis
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = cantidadPorDosis,
                    onValueChange = { cantidadPorDosis = it },
                    label = { Text("Cantidad por dosis *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )

                // Unidad de dosis
                var expandedUnidad by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedUnidad,
                    onExpandedChange = { expandedUnidad = !expandedUnidad },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = unidadDosisSeleccionada.name.lowercase(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unidad") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedUnidad)
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedUnidad,
                        onDismissRequest = { expandedUnidad = false }
                    ) {
                        UnidadDosis.values().forEach { unidad ->
                            DropdownMenuItem(
                                text = { Text(unidad.name.lowercase()) },
                                onClick = {
                                    unidadDosisSeleccionada = unidad
                                    expandedUnidad = false
                                }
                            )
                        }
                    }
                }
            }

            // Intervalo entre dosis
            OutlinedTextField(
                value = intervaloHoras,
                onValueChange = { intervaloHoras = it },
                label = { Text("Intervalo entre dosis (horas) *") },
                placeholder = { Text("Ej: 8 (cada 8 horas)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Hora de inicio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = horaInicio,
                    onValueChange = { horaInicio = it },
                    label = { Text("Hora inicio *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = minutoInicio,
                    onValueChange = { minutoInicio = it },
                    label = { Text("Minutos *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            // Número total de dosis
            OutlinedTextField(
                value = numeroTotalDosis,
                onValueChange = { numeroTotalDosis = it },
                label = { Text("Total de dosis *") },
                placeholder = { Text("Ej: 14 (para 7 días, 2 veces al día)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Instrucciones adicionales
            OutlinedTextField(
                value = instrucciones,
                onValueChange = { instrucciones = it },
                label = { Text("Instrucciones adicionales (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        // Validar campos obligatorios
                        if (nombreMedicamento.isNotBlank() &&
                            cantidadPorDosis.isNotBlank() &&
                            intervaloHoras.isNotBlank() &&
                            horaInicio.isNotBlank() &&
                            minutoInicio.isNotBlank() &&
                            numeroTotalDosis.isNotBlank()
                        ) {
                            try {
                                val nuevoMedicamento = MedicamentoUsuario(
                                    userEmail = userEmail,
                                    nombreMedicamento = nombreMedicamento.trim(),
                                    formaFarmaceutica = formaFarmaceuticaSeleccionada,
                                    concentracion = if (concentracion.isNotBlank()) concentracion.trim() else null,
                                    cantidadPorDosis = cantidadPorDosis.toFloat(),
                                    unidadDosis = unidadDosisSeleccionada,
                                    numeroTotalDosis = numeroTotalDosis.toInt(), // Ya no es null porque es obligatorio
                                    horaInicioTratamiento = HoraDelDia(horaInicio.toInt(), minutoInicio.toInt()),
                                    intervaloEntreDosisHoras = intervaloHoras.toInt(),
                                    instruccionesAdicionales = if (instrucciones.isNotBlank()) instrucciones.trim() else null,
                                    fechaInicioTratamientoEpochDay = System.currentTimeMillis() / (24 * 60 * 60 * 1000),
                                    activo = true
                                )
                                
                                MedicamentoManager.addMedicamento(nuevoMedicamento)
                                onMedicamentoAgregado()
                            } catch (e: Exception) {
                                // TODO: Mostrar error al usuario
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = nombreMedicamento.isNotBlank() &&
                            cantidadPorDosis.isNotBlank() &&
                            intervaloHoras.isNotBlank() &&
                            horaInicio.isNotBlank() &&
                            minutoInicio.isNotBlank() &&
                            numeroTotalDosis.isNotBlank()
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}
