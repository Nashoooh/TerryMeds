package com.example.terrymeds.data

import androidx.compose.runtime.mutableStateListOf

object MedicamentoManager {
    private val medicamentosList = mutableStateListOf<MedicamentoUsuario>()

    init {
        // Crear medicamentos de ejemplo para el usuario por defecto
        createDefaultMedicamentos()
    }

    private fun createDefaultMedicamentos() {
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
            ),
            MedicamentoUsuario(
                userEmail = defaultUserEmail,
                nombreMedicamento = "Jarabe para la tos",
                formaFarmaceutica = FormaFarmaceutica.JARABE,
                concentracion = "10 ml",
                cantidadPorDosis = 10.0f,
                unidadDosis = UnidadDosis.ML,
                numeroTotalDosis = 15,
                horaInicioTratamiento = HoraDelDia(20, 0),
                intervaloEntreDosisHoras = 12,
                instruccionesAdicionales = "Agitar antes de usar",
                fechaInicioTratamientoEpochDay = today - 2, // Comenzó hace 2 días
                activo = true
            )
        )

        medicamentosEjemplo.forEach { medicamento ->
            medicamentosList.add(medicamento)
            println("MedicamentoManager: Medicamento de ejemplo añadido: ${medicamento.nombreMedicamento}")
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

    // Función auxiliar para calcular días restantes del tratamiento (simplificada)
    fun getDiasRestantesTratamiento(medicamento: MedicamentoUsuario): Int? {
        if (medicamento.numeroTotalDosis == null) return null
        
        val today = System.currentTimeMillis() / (24 * 60 * 60 * 1000)
        val diasTranscurridos = (today - medicamento.fechaInicioTratamientoEpochDay).toInt()
        val dosisIntervaloEnDias = medicamento.intervaloEntreDosisHoras / 24.0
        val dosisConsumidas = (diasTranscurridos / dosisIntervaloEnDias).toInt()
        val dosisRestantes = medicamento.numeroTotalDosis - dosisConsumidas
        
        return if (dosisRestantes > 0) {
            (dosisRestantes * dosisIntervaloEnDias).toInt()
        } else {
            0
        }
    }

    // Función simple para obtener la próxima hora de dosis
    fun getProximaHoraDosis(medicamento: MedicamentoUsuario): HoraDelDia {
        val horasDesdeInicio = medicamento.horaInicioTratamiento.hora
        val minutosDesdeInicio = medicamento.horaInicioTratamiento.minuto
        val intervalohoras = medicamento.intervaloEntreDosisHoras
        
        // Obtener hora actual usando Calendar (compatible con Android)
        val calendar = java.util.Calendar.getInstance()
        val horaActual = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minutoActual = calendar.get(java.util.Calendar.MINUTE)
        
        var proximaHora = horasDesdeInicio
        var proximoMinuto = minutosDesdeInicio
        
        // Buscar la próxima hora de dosis
        while (proximaHora < horaActual || (proximaHora == horaActual && proximoMinuto <= minutoActual)) {
            proximaHora += intervalohoras
            if (proximaHora >= 24) {
                proximaHora -= 24
            }
        }
        
        return HoraDelDia(proximaHora, proximoMinuto)
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