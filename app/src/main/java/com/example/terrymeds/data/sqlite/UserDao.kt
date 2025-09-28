package com.example.terrymeds.data.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.terrymeds.data.UserData

/**
 * DAO para operaciones CRUD de usuarios en SQLite
 */
class UserDao(context: Context) {
    private val helper = TerryMedsDBHelper(context)
    
    /**
     * Inserta un nuevo usuario en la base de datos
     */
    fun insert(user: UserData): Boolean {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(TerryMedsDBHelper.COLUMN_USER_EMAIL, user.email)
            put(TerryMedsDBHelper.COLUMN_USER_FIRST_NAME, user.firstName)
            put(TerryMedsDBHelper.COLUMN_USER_LAST_NAME, user.lastName)
            put(TerryMedsDBHelper.COLUMN_USER_PASSWORD, user.password)
            put(TerryMedsDBHelper.COLUMN_USER_BIRTH_DATE, user.birthDate)
        }
        val id = db.insert(TerryMedsDBHelper.TABLE_USERS, null, values)
        db.close()
        return id != -1L
    }
    
    /**
     * Actualiza un usuario existente por email
     */
    fun update(user: UserData): Boolean {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(TerryMedsDBHelper.COLUMN_USER_FIRST_NAME, user.firstName)
            put(TerryMedsDBHelper.COLUMN_USER_LAST_NAME, user.lastName)
            put(TerryMedsDBHelper.COLUMN_USER_PASSWORD, user.password)
            put(TerryMedsDBHelper.COLUMN_USER_BIRTH_DATE, user.birthDate)
        }
        val rows = db.update(
            TerryMedsDBHelper.TABLE_USERS, 
            values, 
            "${TerryMedsDBHelper.COLUMN_USER_EMAIL} = ?", 
            arrayOf(user.email)
        )
        db.close()
        return rows > 0
    }
    
    /**
     * Elimina un usuario por email
     */
    fun delete(email: String): Boolean {
        val db = helper.writableDatabase
        val rows = db.delete(
            TerryMedsDBHelper.TABLE_USERS, 
            "${TerryMedsDBHelper.COLUMN_USER_EMAIL} = ?", 
            arrayOf(email)
        )
        db.close()
        return rows > 0
    }
    
    /**
     * Obtiene todos los usuarios
     */
    fun getAll(): List<UserData> {
        val db = helper.readableDatabase
        val list = mutableListOf<UserData>()
        val cursor: Cursor = db.rawQuery(
            """
            SELECT ${TerryMedsDBHelper.COLUMN_USER_EMAIL}, 
                   ${TerryMedsDBHelper.COLUMN_USER_FIRST_NAME}, 
                   ${TerryMedsDBHelper.COLUMN_USER_LAST_NAME}, 
                   ${TerryMedsDBHelper.COLUMN_USER_PASSWORD}, 
                   ${TerryMedsDBHelper.COLUMN_USER_BIRTH_DATE}
            FROM ${TerryMedsDBHelper.TABLE_USERS} 
            ORDER BY ${TerryMedsDBHelper.COLUMN_USER_LAST_NAME}, ${TerryMedsDBHelper.COLUMN_USER_FIRST_NAME}
            """.trimIndent(),
            null
        )
        
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    UserData(
                        email = it.getString(0),
                        firstName = it.getString(1),
                        lastName = it.getString(2),
                        password = it.getString(3),
                        birthDate = it.getLong(4)
                    )
                )
            }
        }
        db.close()
        return list
    }
    
    /**
     * Busca un usuario por email
     */
    fun getByEmail(email: String): UserData? {
        val db = helper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT ${TerryMedsDBHelper.COLUMN_USER_EMAIL}, 
                   ${TerryMedsDBHelper.COLUMN_USER_FIRST_NAME}, 
                   ${TerryMedsDBHelper.COLUMN_USER_LAST_NAME}, 
                   ${TerryMedsDBHelper.COLUMN_USER_PASSWORD}, 
                   ${TerryMedsDBHelper.COLUMN_USER_BIRTH_DATE}
            FROM ${TerryMedsDBHelper.TABLE_USERS} 
            WHERE ${TerryMedsDBHelper.COLUMN_USER_EMAIL} = ? 
            LIMIT 1
            """.trimIndent(),
            arrayOf(email)
        )
        
        var user: UserData? = null
        cursor.use {
            if (it.moveToFirst()) {
                user = UserData(
                    email = it.getString(0),
                    firstName = it.getString(1),
                    lastName = it.getString(2),
                    password = it.getString(3),
                    birthDate = it.getLong(4)
                )
            }
        }
        db.close()
        return user
    }
    
    /**
     * Autentica un usuario con email y contraseña
     */
    fun authenticate(email: String, password: String): UserData? {
        val user = getByEmail(email)
        return if (user?.password == password) user else null
    }
    
    /**
     * Busca usuarios por texto (email, nombre o apellido)
     */
    fun search(query: String): List<UserData> {
        if (query.isBlank()) return getAll()
        
        val db = helper.readableDatabase
        val list = mutableListOf<UserData>()
        val like = "%$query%"
        val cursor = db.rawQuery(
            """
            SELECT ${TerryMedsDBHelper.COLUMN_USER_EMAIL}, 
                   ${TerryMedsDBHelper.COLUMN_USER_FIRST_NAME}, 
                   ${TerryMedsDBHelper.COLUMN_USER_LAST_NAME}, 
                   ${TerryMedsDBHelper.COLUMN_USER_PASSWORD}, 
                   ${TerryMedsDBHelper.COLUMN_USER_BIRTH_DATE}
            FROM ${TerryMedsDBHelper.TABLE_USERS}
            WHERE ${TerryMedsDBHelper.COLUMN_USER_EMAIL} LIKE ? 
               OR ${TerryMedsDBHelper.COLUMN_USER_FIRST_NAME} LIKE ? 
               OR ${TerryMedsDBHelper.COLUMN_USER_LAST_NAME} LIKE ?
            ORDER BY ${TerryMedsDBHelper.COLUMN_USER_LAST_NAME}, ${TerryMedsDBHelper.COLUMN_USER_FIRST_NAME}
            """.trimIndent(),
            arrayOf(like, like, like)
        )
        
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    UserData(
                        email = it.getString(0),
                        firstName = it.getString(1),
                        lastName = it.getString(2),
                        password = it.getString(3),
                        birthDate = it.getLong(4)
                    )
                )
            }
        }
        db.close()
        return list
    }
    
    /**
     * Actualiza solo la contraseña de un usuario
     */
    fun updatePassword(email: String, newPassword: String): Boolean {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(TerryMedsDBHelper.COLUMN_USER_PASSWORD, newPassword)
        }
        val rows = db.update(
            TerryMedsDBHelper.TABLE_USERS, 
            values, 
            "${TerryMedsDBHelper.COLUMN_USER_EMAIL} = ?", 
            arrayOf(email)
        )
        db.close()
        return rows > 0
    }
}
