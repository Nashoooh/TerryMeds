package com.example.terrymeds.data.sqlite

import android.content.Context
import com.example.terrymeds.data.UserData
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Manager para operaciones de usuarios usando SQLite
 * Reemplaza al UserManager original que usaba listas en memoria
 */
class SQLiteUserManager private constructor(context: Context) {
    private val userDao = UserDao(context)
    
    companion object {
        @Volatile
        private var INSTANCE: SQLiteUserManager? = null
        
        fun getInstance(context: Context): SQLiteUserManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SQLiteUserManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    /**
     * Añade un nuevo usuario
     */
    fun addUser(user: UserData): Boolean {
        val result = userDao.insert(user)
        if (result) {
            println("SQLiteUserManager: Usuario añadido: $user")
        } else {
            println("SQLiteUserManager: Error al añadir usuario: $user")
        }
        return result
    }
    
    /**
     * Busca un usuario por email
     */
    fun findUserByEmail(email: String): UserData? {
        return userDao.getByEmail(email)
    }
    
    /**
     * Autentica un usuario con email y contraseña
     */
    fun loginUser(email: String, passwordAttempt: String): UserData? {
        val user = userDao.authenticate(email, passwordAttempt)
        if (user != null) {
            println("SQLiteUserManager: Login exitoso para $email")
        } else {
            println("SQLiteUserManager: Fallo de login para $email")
        }
        return user
    }
    
    /**
     * Restablece la contraseña de un usuario
     */
    fun resetPassword(email: String, newPassword: String): Boolean {
        val result = userDao.updatePassword(email, newPassword)
        if (result) {
            println("SQLiteUserManager: Contraseña restablecida para el usuario: $email")
        } else {
            println("SQLiteUserManager: No se encontró usuario con email $email para restablecer contraseña.")
        }
        return result
    }
    
    /**
     * Obtiene todos los usuarios
     */
    fun getAllUsers(): List<UserData> {
        return userDao.getAll()
    }
    
    /**
     * Actualiza un usuario
     */
    fun updateUser(updatedUser: UserData): Boolean {
        val result = userDao.update(updatedUser)
        if (result) {
            println("SQLiteUserManager: Usuario actualizado: $updatedUser")
        } else {
            println("SQLiteUserManager: No se encontró usuario para actualizar con email ${updatedUser.email}")
        }
        return result
    }
    
    /**
     * Elimina un usuario por email
     */
    fun deleteUserByEmail(email: String): Boolean {
        val result = userDao.delete(email)
        if (result) {
            println("SQLiteUserManager: Usuario con email $email eliminado.")
        } else {
            println("SQLiteUserManager: No se encontró usuario para eliminar con email $email.")
        }
        return result
    }
    
    /**
     * Busca usuarios por texto
     */
    fun searchUsers(query: String): List<UserData> {
        return userDao.search(query)
    }
    
    /**
     * Imprime todos los usuarios (debug)
     */
    fun printAllUsers() {
        println("---- Lista Actual de Usuarios (SQLiteUserManager) ----")
        val users = getAllUsers()
        if (users.isEmpty()) {
            println("No hay usuarios registrados.")
        } else {
            users.forEachIndexed { index, user ->
                println("${index + 1}. $user")
            }
        }
        println("------------------------------------------")
    }
    
    /**
     * Inicializa con usuario por defecto si no existe
     */
    fun initializeDefaultUser() {
        val defaultEmail = "nacho@correo.com"
        val existingUser = findUserByEmail(defaultEmail)
        
        if (existingUser == null) {
            val defaultUser = createDefaultUser()
            if (defaultUser != null) {
                val added = addUser(defaultUser)
                if (added) {
                    println("SQLiteUserManager: Usuario por defecto creado: $defaultUser")
                } else {
                    println("SQLiteUserManager: No se pudo crear el usuario por defecto")
                }
            }
        } else {
            println("SQLiteUserManager: Usuario por defecto ya existe: $existingUser")
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
}
