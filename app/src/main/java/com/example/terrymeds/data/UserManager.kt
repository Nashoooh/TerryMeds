package com.example.terrymeds.data

import androidx.compose.runtime.mutableStateListOf
import java.text.SimpleDateFormat
import java.util.Locale


object UserManager {
    private val userList = mutableStateListOf<UserData>()

    init {
        val defaultUser = createDefaultUser()
        if (defaultUser != null) {
            userList.add(defaultUser)
            println("UserManager: Usuario por defecto añadido: $defaultUser")
        } else {
            println("UserManager: Error al crear el usuario por defecto (fecha inválida).")
        }
    }

    private fun createDefaultUser(): UserData? {
        val firstName = "Ignacio"
        val lastName = "Andana"
        val email = "nacho@correo.com"
        val password = "Clave123"
        val birthDateString = "30/12/1992"

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        return try {
            val birthDateLong = sdf.parse(birthDateString)?.time
            if (birthDateLong != null) {
                UserData(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password,
                    birthDate = birthDateLong
                )
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error parseando fecha para usuario por defecto: ${e.message}")
            null
        }
    }

    fun addUser(user: UserData) {
        userList.add(user)
        println("UserManager: Usuario añadido: $user")
        println("UserManager: Lista actual: $userList")
    }

    fun findUserByEmail(email: String): UserData? {
        return userList.find { it.email == email }
    }

    fun loginUser(email: String, passwordAttempt: String): UserData? {
        val user = findUserByEmail(email)
        if (user != null) {
            if (user.password == passwordAttempt) {
                println("UserManager: Login exitoso para $email")
                return user
            }

        }
        println("UserManager: Fallo de login para $email")
        return null
    }

    fun resetPassword(email: String, newPassword: String): Boolean {
        val user = findUserByEmail(email)
        return if (user != null) {

            val updatedUser = user.copy(password = newPassword)

            val userIndex = userList.indexOfFirst { it.email.equals(email, ignoreCase = true) }
            if (userIndex != -1) {
                userList[userIndex] = updatedUser
                println("UserManager: Contraseña restablecida para el usuario: ${user.email}")
                printAllUsers()
                true
            } else {
                println("UserManager: Error interno al intentar actualizar usuario para restablecer contraseña.")
                false
            }
        } else {
            println("UserManager: No se encontró usuario con email $email para restablecer contraseña.")
            false
        }
    }

    fun getAllUsers(): List<UserData> {
        return userList.toList()
    }

    fun updateUser(updatedUser: UserData): Boolean {
        val userIndex = userList.indexOfFirst { it.email == updatedUser.email }
        return if (userIndex != -1) {
            userList[userIndex] = updatedUser
            println("UserManager: Usuario actualizado: $updatedUser")
            true
        } else {
            println("UserManager: No se encontró usuario para actualizar con email ${updatedUser.email}")
            false
        }
    }

    fun deleteUserByEmail(email: String): Boolean {
        val removed = userList.removeIf { it.email == email }
        if (removed) {
            println("UserManager: Usuario con email $email eliminado.")
        } else {
            println("UserManager: No se encontró usuario para eliminar con email $email.")
        }
        return removed
    }

    fun printAllUsers() {
        println("---- Lista Actual de Usuarios (UserManager) ----")
        if (userList.isEmpty()) {
            println("No hay usuarios registrados.")
        } else {
            userList.forEachIndexed { index, user ->
                println("${index + 1}. $user")
            }
        }
        println("------------------------------------------")
    }
}
