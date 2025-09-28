package com.example.terrymeds.data.sqlite

import android.content.Context
import com.example.terrymeds.data.MedicamentoUsuario
import com.example.terrymeds.data.FormaFarmaceutica
import com.example.terrymeds.data.UnidadDosis
import com.example.terrymeds.data.HoraDelDia

/**
 * Manager para operaciones de medicamentos usando SQLite
 * Reemplaza al MedicamentoManager original que usaba listas en memoria
 */
class SQLiteMedicamentoManager private constructor(context: Context) {
    private val medicamentoDao = MedicamentoDao(context)
    
    companion object {
        @Volatile
        private var INSTANCE: SQLiteMedicamentoManager? = null
        
        fun getInstance(context: Context): SQLiteMedicamentoManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SQLiteMedicamentoManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    /**
     * Añade un nuevo medicamento
     */
    fun addMedicamento(medicamento: MedicamentoUsuario): Boolean {
        val result = medicamentoDao.insert(medicamento)
        if (result) {
            println("SQLiteMedicamentoManager: Medicamento añadido: ${medicamento.nombreMedicamento} para ${medicamento.userEmail}")
        } else {
            println("SQLiteMedicamentoManager: Error al añadir medicamento: ${medicamento.nombreMedicamento}")
        }
        return result
    }
    
    /**
     * Obtiene medicamentos por email de usuario
     */
    fun getMedicamentosByUserEmail(userEmail: String): List<MedicamentoUsuario> {
        return medicamentoDao.getByUserEmail(userEmail)
    }
    
    /**
     * Obtiene medicamentos activos por email de usuario
     */
    fun getActiveMedicamentosByUserEmail(userEmail: String): List<MedicamentoUsuario> {
        return medicamentoDao.getActiveByUserEmail(userEmail)
    }
    
    /**
     * Busca un medicamento por ID
     */
    fun getMedicamentoById(id: String): MedicamentoUsuario? {
        return medicamentoDao.getById(id)
    }
    
    /**
     * Actualiza un medicamento
     */
    fun updateMedicamento(updatedMedicamento: MedicamentoUsuario): Boolean {
        val result = medicamentoDao.update(updatedMedicamento)
        if (result) {
            println("SQLiteMedicamentoManager: Medicamento actualizado: ${updatedMedicamento.nombreMedicamento}")
        } else {
            println("SQLiteMedicamentoManager: No se encontró medicamento para actualizar con ID ${updatedMedicamento.id}")
        }
        return result
    }
    
    /**
     * Desactiva un medicamento
     */
    fun deactivateMedicamento(medicamentoId: String): Boolean {
        val result = medicamentoDao.deactivate(medicamentoId)
        if (result) {
            println("SQLiteMedicamentoManager: Medicamento desactivado con ID $medicamentoId")
        } else {
            println("SQLiteMedicamentoManager: No se encontró medicamento con ID $medicamentoId para desactivar")
        }
        return result
    }
    
    /**
     * Activa un medicamento
     */
    fun activateMedicamento(medicamentoId: String): Boolean {
        val result = medicamentoDao.activate(medicamentoId)
        if (result) {
            println("SQLiteMedicamentoManager: Medicamento activado con ID $medicamentoId")
        } else {
            println("SQLiteMedicamentoManager: No se encontró medicamento con ID $medicamentoId para activar")
        }
        return result
    }
    
    /**
     * Elimina un medicamento
     */
    fun deleteMedicamento(medicamentoId: String): Boolean {
        val result = medicamentoDao.delete(medicamentoId)
        if (result) {
            println("SQLiteMedicamentoManager: Medicamento con ID $medicamentoId eliminado")
        } else {
            println("SQLiteMedicamentoManager: No se encontró medicamento para eliminar con ID $medicamentoId")
        }
        return result
    }
    
    /**
     * Obtiene todos los medicamentos
     */
    fun getAllMedicamentos(): List<MedicamentoUsuario> {
        return medicamentoDao.getAll()
    }
    
    /**
     * Busca medicamentos por texto
     */
    fun searchMedicamentos(query: String, userEmail: String? = null): List<MedicamentoUsuario> {
        return medicamentoDao.search(query, userEmail)
    }
    
    /**
     * Función auxiliar para calcular días restantes del tratamiento
     */
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
    
    /**
     * Función simple para obtener la próxima hora de dosis
     */
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
    
    /**
     * Imprime todos los medicamentos (debug)
     */
    fun printAllMedicamentos() {
        println("---- Lista Actual de Medicamentos (SQLiteMedicamentoManager) ----")
        val medicamentos = getAllMedicamentos()
        if (medicamentos.isEmpty()) {
            println("No hay medicamentos registrados.")
        } else {
            medicamentos.forEachIndexed { index, medicamento ->
                println("${index + 1}. ${medicamento.nombreMedicamento} (${medicamento.userEmail}) - Activo: ${medicamento.activo}")
            }
        }
        println("----------------------------------------------------------")
    }
    
    /**
     * Imprime medicamentos de un usuario específico (debug)
     */
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
    
    /**
     * Inicializa con medicamentos por defecto si no existen
     */
    fun initializeDefaultMedicamentos() {
        val defaultUserEmail = "nacho@correo.com"
        val existingMedicamentos = getMedicamentosByUserEmail(defaultUserEmail)
        
        if (existingMedicamentos.isEmpty()) {
            createDefaultMedicamentos(defaultUserEmail)
        } else {
            println("SQLiteMedicamentoManager: Ya existen medicamentos para el usuario por defecto")
        }
    }
    
    private fun createDefaultMedicamentos(userEmail: String) {
        try {
            val today = System.currentTimeMillis() / (24 * 60 * 60 * 1000)

            val medicamentosEjemplo = listOf(
                MedicamentoUsuario(
                    userEmail = userEmail,
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
                    userEmail = userEmail,
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
                addMedicamento(medicamento)
            }
            println("SQLiteMedicamentoManager: ${medicamentosEjemplo.size} medicamentos de ejemplo creados")
        } catch (e: Exception) {
            println("SQLiteMedicamentoManager: Error creando medicamentos de ejemplo: ${e.message}")
        }
    }
}
