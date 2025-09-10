package com.example.terrymeds.data

import java.util.UUID

// Representa un medicamento específico que un usuario está tomando,
// incluyendo su posología personalizada.
data class MedicamentoUsuario(
    val id: String = UUID.randomUUID().toString(),    // ID único para esta entrada de medicación del usuario
    val userEmail: String,                            // Email del usuario al que pertenece esta entrada                              // ID del usuario (de UserData)
    val nombreMedicamento: String,                    // Nombre del medicamento (podría ser el de DrugData o uno personalizado)
    val formaFarmaceutica: FormaFarmaceutica = FormaFarmaceutica.COMPRIMIDO, // Forma del medicamento
    val concentracion: String? = null,                // Dosis/concentración (Ej: "200 mg")
    // --- Información de la Posología (Cómo y Cuándo tomarlo) ---
    val cantidadPorDosis: Float = 1.0f,               // Cantidad a tomar en cada dosis (Ej: 1, 0.5, 5)
    val unidadDosis: UnidadDosis = UnidadDosis.UNIDAD, // Unidad de la cantidad (Ej: comprimido, ml, mg)
    val numeroTotalDosis: Int? = null,                // Ej: 21 dosis en total
    val horaInicioTratamiento: HoraDelDia,            // Hora de la primera dosis del día/tratamiento
    val intervaloEntreDosisHoras: Int,                // Cada cuántas horas tomar (Ej: 8 para cada 8 horas)
    val instruccionesAdicionales: String? = null,     // Ej: "Tomar con comida", "Agitar antes de usar"
    val fechaInicioTratamientoEpochDay: Long,         // Día de inicio del tratamiento (Epoch Day para ser independiente de la zona horaria para el día)
    var activo: Boolean = true                        // Para poder marcar un tratamiento como activo o inactivo
)

// Representa una hora del día (sin fecha)
data class HoraDelDia(
    val hora: Int,   // 0-23
    val minuto: Int  // 0-59
) {
    init {
        require(hora in 0..23) { "La hora debe estar entre 0 y 23." }
        require(minuto in 0..59) { "El minuto debe estar entre 0 y 59." }
    }

    override fun toString(): String {
        // String.format usará el Locale por defecto del sistema implícitamente
        return String.format("%02d:%02d", hora, minuto)
    }
}

enum class FormaFarmaceutica {
    TABLETA, CAPSULA, COMPRIMIDO, SOLUCION, JARABE, INHALADOR, GOTAS, CREMA, OTRO
}

enum class UnidadDosis {
    UNIDAD, ML, MG, G, GOTA, INHALACION, OTRO
}
