package com.example.terrymeds.data

data class UserData(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val birthDate: Long // Almacenaremos la fecha como un Long (timestamp en milisegundos)
    // Esto es común y fácil de manejar con DatePickers de Material 3
)

