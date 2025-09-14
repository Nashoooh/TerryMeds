package com.example.terrymeds.data

import androidx.compose.runtime.mutableStateListOf

object MedicamentoManager {
    private val medicamentosList = mutableStateListOf<MedicamentoUsuario>()

    init {
        // Crear medicamentos de ejemplo para el usuario por defecto
        createDefaultMedicamentos()
    }

    private fun createDefaultMedicamentos() {
        try {
            val defaultUserEmail = "nacho@correo.com"
            val today = System.currentTimeMillis() / (24 * 60 * 60 * 1000)

            val medicamentosEjemplo = listOf(
                MedicamentoUsuario(
                    userEmail = defaultUserEmail,
                    nombreMedicamento = "Paracetamol",
                    formaFarmaceutica = FormaFarmaceutica.COMPRIMIDO,
                    concentracion = "500 mg",
                    cantidadPorDosis = 1.0f,
                    unidadDosis = UnidadDosis.UNIDAD,
                    numeroTotalDosis = 20,
                    horaInicioTratamiento = HoraDelDia(8, 0),
                    intervaloEntreDosisHoras = 8,
                    instruccionesAdicionales = "Tomar con alimentos",
                    fechaInicioTratamientoEpochDay = today,
                    activo = true
                ),
                MedicamentoUsuario(
                    userEmail = defaultUserEmail,
                    nombreMedicamento = "Vitamina D3",
                    formaFarmaceutica = FormaFarmaceutica.CAPSULA,
                    concentracion = "1000 UI",
                    cantidadPorDosis = 1.0f,
                    unidadDosis = UnidadDosis.UNIDAD,
                    numeroTotalDosis = 30,
                    horaInicioTratamiento = HoraDelDia(9, 0),
                    intervaloEntreDosisHoras = 24,
                    instruccionesAdicionales = "Tomar con el desayuno",
                    fechaInicioTratamientoEpochDay = today - 5, // Comenzó hace 5 días
                    activo = true
                )
            )

            medicamentosEjemplo.forEach { medicamento ->
                medicamentosList.add(medicamento)
            }
            println("MedicamentoManager: ${medicamentosEjemplo.size} medicamentos de ejemplo creados")
        } catch (e: Exception) {
            println("MedicamentoManager: Error creando medicamentos de ejemplo: ${e.message}")
        }
    }

    fun addMedicamento(medicamento: MedicamentoUsuario) {
        medicamentosList.add(medicamento)
        println("MedicamentoManager: Medicamento añadido: ${medicamento.nombreMedicamento} para ${medicamento.userEmail}")
    }

    fun getMedicamentosByUserEmail(userEmail: String): List<MedicamentoUsuario> {
        return medicamentosList.filter { it.userEmail == userEmail }
    }

    fun getActiveMedicamentosByUserEmail(userEmail: String): List<MedicamentoUsuario> {
        return medicamentosList.filter { it.userEmail == userEmail && it.activo }
    }

    fun getMedicamentoById(id: String): MedicamentoUsuario? {
        return medicamentosList.find { it.id == id }
    }

    fun updateMedicamento(updatedMedicamento: MedicamentoUsuario): Boolean {
        val medicamentoIndex = medicamentosList.indexOfFirst { it.id == updatedMedicamento.id }
        return if (medicamentoIndex != -1) {
            medicamentosList[medicamentoIndex] = updatedMedicamento
            println("MedicamentoManager: Medicamento actualizado: ${updatedMedicamento.nombreMedicamento}")
            true
        } else {
            println("MedicamentoManager: No se encontró medicamento para actualizar con ID ${updatedMedicamento.id}")
            false
        }
    }

    fun deactivateMedicamento(medicamentoId: String): Boolean {
        val medicamento = getMedicamentoById(medicamentoId)
        return if (medicamento != null) {
            val updatedMedicamento = medicamento.copy(activo = false)
            updateMedicamento(updatedMedicamento)
        } else {
            println("MedicamentoManager: No se encontró medicamento con ID $medicamentoId para desactivar")
            false
        }
    }

    fun activateMedicamento(medicamentoId: String): Boolean {
        val medicamento = getMedicamentoById(medicamentoId)
        return if (medicamento != null) {
            val updatedMedicamento = medicamento.copy(activo = true)
            updateMedicamento(updatedMedicamento)
        } else {
            println("MedicamentoManager: No se encontró medicamento con ID $medicamentoId para activar")
            false
        }
    }

    fun deleteMedicamento(medicamentoId: String): Boolean {
        val removed = medicamentosList.removeIf { it.id == medicamentoId }
        if (removed) {
            println("MedicamentoManager: Medicamento con ID $medicamentoId eliminado")
        } else {
            println("MedicamentoManager: No se encontró medicamento para eliminar con ID $medicamentoId")
        }
        return removed
    }

    fun getAllMedicamentos(): List<MedicamentoUsuario> {
        return medicamentosList.toList()
    }

    // Función auxiliar para calcular días restantes del tratamiento (mejorada)
    fun getDiasRestantesTratamiento(medicamento: MedicamentoUsuario): Int? {
        return try {
            val totalDosis = medicamento.numeroTotalDosis ?: return null
            
            if (totalDosis <= 0 || medicamento.intervaloEntreDosisHoras <= 0) {
                return null
            }
            
            val todayEpoch = System.currentTimeMillis() / (24 * 60 * 60 * 1000)
            val diasTranscurridos = (todayEpoch - medicamento.fechaInicioTratamientoEpochDay).toInt().coerceAtLeast(0)
            
            // Calcular cuántas dosis se han tomado basándose en los días transcurridos
            val dosisPorDia = 24.0 / medicamento.intervaloEntreDosisHoras
            val dosisConsumidas = (diasTranscurridos * dosisPorDia).toInt().coerceAtLeast(0)
            val dosisRestantes = (totalDosis - dosisConsumidas).coerceAtLeast(0)
            
            // Calcular días restantes basado en las dosis restantes
            if (dosisRestantes > 0 && dosisPorDia > 0) {
                (dosisRestantes / dosisPorDia).toInt().coerceAtLeast(1)
            } else {
                0
            }
        } catch (e: Exception) {
            null
        }
    }

    // Función simple para obtener la próxima hora de dosis
    fun getProximaHoraDosis(medicamento: MedicamentoUsuario): HoraDelDia {
        return try {
            val horasDesdeInicio = medicamento.horaInicioTratamiento.hora
            val minutosDesdeInicio = medicamento.horaInicioTratamiento.minuto
            val intervalohoras = medicamento.intervaloEntreDosisHoras
            
            // Validar datos de entrada
            if (intervalohoras <= 0) {
                return medicamento.horaInicioTratamiento
            }
            
            // Obtener hora actual usando Calendar (compatible con Android)
            val calendar = java.util.Calendar.getInstance()
            val horaActual = calendar.get(java.util.Calendar.HOUR_OF_DAY)
            val minutoActual = calendar.get(java.util.Calendar.MINUTE)
            
            var proximaHora = horasDesdeInicio
            var proximoMinuto = minutosDesdeInicio
            
            // Buscar la próxima hora de dosis
            var iterations = 0
            while ((proximaHora < horaActual || (proximaHora == horaActual && proximoMinuto <= minutoActual)) && iterations < 100) {
                proximaHora += intervalohoras
                if (proximaHora >= 24) {
                    proximaHora -= 24
                }
                iterations++
            }
            
            HoraDelDia(proximaHora.coerceIn(0, 23), proximoMinuto.coerceIn(0, 59))
        } catch (e: Exception) {
            medicamento.horaInicioTratamiento
        }
    }

    fun printAllMedicamentos() {
        println("---- Lista Actual de Medicamentos (MedicamentoManager) ----")
        if (medicamentosList.isEmpty()) {
            println("No hay medicamentos registrados.")
        } else {
            medicamentosList.forEachIndexed { index, medicamento ->
                println("${index + 1}. ${medicamento.nombreMedicamento} (${medicamento.userEmail}) - Activo: ${medicamento.activo}")
            }
        }
        println("----------------------------------------------------------")
    }

    fun printMedicamentosByUser(userEmail: String) {
        println("---- Medicamentos de $userEmail ----")
        val userMedicamentos = getMedicamentosByUserEmail(userEmail)
        if (userMedicamentos.isEmpty()) {
            println("No hay medicamentos registrados para este usuario.")
        } else {
            userMedicamentos.forEachIndexed { index, medicamento ->
                println("${index + 1}. ${medicamento.nombreMedicamento} - ${medicamento.concentracion} - Activo: ${medicamento.activo}")
            }
        }
        println("----------------------------------------")
    }
}