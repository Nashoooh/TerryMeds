package com.example.terrymeds.data

import java.util.UUID // Para generar IDs únicos

// Información general sobre un tipo de medicamento.
data class DrugData(
    val id: String = UUID.randomUUID().toString(), // ID único para el tipo de medicamento
    val name: String,                            // Nombre del medicamento (Ej: Ibuprofeno, Amoxicilina)
    val description: String? = null,             // Descripción opcional
    val form: DrugForm = DrugForm.COMPRIMIDO,        // Forma del medicamento (Ej: Pastilla, Jarabe, Inyección)
    val strength: String? = null                 // Dosis/concentración (Ej: "200 mg", "500 mg/5 ml")

)

enum class DrugForm {
    TABLETA, CAPSULA, COMPRIMIDO, SOLUCION, INHALADOR, OTRO
}
