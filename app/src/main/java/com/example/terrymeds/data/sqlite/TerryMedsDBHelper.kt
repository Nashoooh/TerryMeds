package com.example.terrymeds.data.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DBHelper para TerryMeds - maneja la creación y actualización de la base de datos SQLite
 */
class TerryMedsDBHelper(context: Context) : SQLiteOpenHelper(
    context,
    "terry_meds.db", // Nombre de la base de datos
    null,
    2 // Versión de la base de datos
) {
    
    companion object {
        // Tabla usuarios
        const val TABLE_USERS = "users"
        const val COLUMN_USER_EMAIL = "email"
        const val COLUMN_USER_FIRST_NAME = "first_name"
        const val COLUMN_USER_LAST_NAME = "last_name"
        const val COLUMN_USER_PASSWORD = "password"
        const val COLUMN_USER_BIRTH_DATE = "birth_date"
        
        // Tabla medicamentos
        const val TABLE_MEDICAMENTOS = "medicamentos"
        const val COLUMN_MED_ID = "id"
        const val COLUMN_MED_USER_EMAIL = "user_email"
        const val COLUMN_MED_NOMBRE = "nombre_medicamento"
        const val COLUMN_MED_FORMA_FARMACEUTICA = "forma_farmaceutica"
        const val COLUMN_MED_CONCENTRACION = "concentracion"
        const val COLUMN_MED_CANTIDAD_POR_DOSIS = "cantidad_por_dosis"
        const val COLUMN_MED_UNIDAD_DOSIS = "unidad_dosis"
        const val COLUMN_MED_NUMERO_TOTAL_DOSIS = "numero_total_dosis"
        const val COLUMN_MED_HORA_INICIO_HORA = "hora_inicio_hora"
        const val COLUMN_MED_HORA_INICIO_MINUTO = "hora_inicio_minuto"
        const val COLUMN_MED_INTERVALO_HORAS = "intervalo_entre_dosis_horas"
        const val COLUMN_MED_INSTRUCCIONES = "instrucciones_adicionales"
        const val COLUMN_MED_FECHA_INICIO = "fecha_inicio_tratamiento_epoch_day"
        const val COLUMN_MED_ACTIVO = "activo"
    }
    
    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla usuarios
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_EMAIL TEXT PRIMARY KEY,
                $COLUMN_USER_FIRST_NAME TEXT NOT NULL,
                $COLUMN_USER_LAST_NAME TEXT NOT NULL,
                $COLUMN_USER_PASSWORD TEXT NOT NULL,
                $COLUMN_USER_BIRTH_DATE INTEGER NOT NULL
            )
        """.trimIndent()
        
        // Crear tabla medicamentos
        val createMedicamentosTable = """
            CREATE TABLE $TABLE_MEDICAMENTOS (
                $COLUMN_MED_ID TEXT PRIMARY KEY,
                $COLUMN_MED_USER_EMAIL TEXT NOT NULL,
                $COLUMN_MED_NOMBRE TEXT NOT NULL,
                $COLUMN_MED_FORMA_FARMACEUTICA TEXT NOT NULL,
                $COLUMN_MED_CONCENTRACION TEXT,
                $COLUMN_MED_CANTIDAD_POR_DOSIS REAL NOT NULL,
                $COLUMN_MED_UNIDAD_DOSIS TEXT NOT NULL,
                $COLUMN_MED_NUMERO_TOTAL_DOSIS INTEGER,
                $COLUMN_MED_HORA_INICIO_HORA INTEGER NOT NULL,
                $COLUMN_MED_HORA_INICIO_MINUTO INTEGER NOT NULL,
                $COLUMN_MED_INTERVALO_HORAS INTEGER NOT NULL,
                $COLUMN_MED_INSTRUCCIONES TEXT,
                $COLUMN_MED_FECHA_INICIO INTEGER NOT NULL,
                $COLUMN_MED_ACTIVO INTEGER NOT NULL DEFAULT 1,
                FOREIGN KEY ($COLUMN_MED_USER_EMAIL) REFERENCES $TABLE_USERS ($COLUMN_USER_EMAIL)
            )
        """.trimIndent()
        
        db.execSQL(createUsersTable)
        db.execSQL(createMedicamentosTable)
        
        // Insertar usuario por defecto
        insertDefaultUser(db)
    }
    
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Para desarrollo: borra y recrea las tablas
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEDICAMENTOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }
    
    private fun insertDefaultUser(db: SQLiteDatabase) {
        try {
            val defaultUserInsert = """
                INSERT INTO $TABLE_USERS (
                    $COLUMN_USER_EMAIL, 
                    $COLUMN_USER_FIRST_NAME, 
                    $COLUMN_USER_LAST_NAME, 
                    $COLUMN_USER_PASSWORD, 
                    $COLUMN_USER_BIRTH_DATE
                ) VALUES (
                    'nacho@correo.com',
                    'Ignacio',
                    'Andana',
                    'Clave123',
                    724723200000
                )
            """.trimIndent()
            
            db.execSQL(defaultUserInsert)
        } catch (e: Exception) {
            // Si el usuario ya existe, no hacer nada
        }
    }
}
