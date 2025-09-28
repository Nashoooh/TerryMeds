package com.example.terrymeds.data.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.terrymeds.data.MedicamentoUsuario
import com.example.terrymeds.data.FormaFarmaceutica
import com.example.terrymeds.data.UnidadDosis
import com.example.terrymeds.data.HoraDelDia
import java.util.UUID

/**
 * DAO para operaciones CRUD de medicamentos en SQLite
 */
class MedicamentoDao(context: Context) {
    private val helper = TerryMedsDBHelper(context)
    
    /**
     * Inserta un nuevo medicamento en la base de datos
     */
    fun insert(medicamento: MedicamentoUsuario): Boolean {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(TerryMedsDBHelper.COLUMN_MED_ID, medicamento.id)
            put(TerryMedsDBHelper.COLUMN_MED_USER_EMAIL, medicamento.userEmail)
            put(TerryMedsDBHelper.COLUMN_MED_NOMBRE, medicamento.nombreMedicamento)
            put(TerryMedsDBHelper.COLUMN_MED_FORMA_FARMACEUTICA, medicamento.formaFarmaceutica.name)
            put(TerryMedsDBHelper.COLUMN_MED_CONCENTRACION, medicamento.concentracion)
            put(TerryMedsDBHelper.COLUMN_MED_CANTIDAD_POR_DOSIS, medicamento.cantidadPorDosis)
            put(TerryMedsDBHelper.COLUMN_MED_UNIDAD_DOSIS, medicamento.unidadDosis.name)
            put(TerryMedsDBHelper.COLUMN_MED_NUMERO_TOTAL_DOSIS, medicamento.numeroTotalDosis)
            put(TerryMedsDBHelper.COLUMN_MED_HORA_INICIO_HORA, medicamento.horaInicioTratamiento.hora)
            put(TerryMedsDBHelper.COLUMN_MED_HORA_INICIO_MINUTO, medicamento.horaInicioTratamiento.minuto)
            put(TerryMedsDBHelper.COLUMN_MED_INTERVALO_HORAS, medicamento.intervaloEntreDosisHoras)
            put(TerryMedsDBHelper.COLUMN_MED_INSTRUCCIONES, medicamento.instruccionesAdicionales)
            put(TerryMedsDBHelper.COLUMN_MED_FECHA_INICIO, medicamento.fechaInicioTratamientoEpochDay)
            put(TerryMedsDBHelper.COLUMN_MED_ACTIVO, if (medicamento.activo) 1 else 0)
        }
        val id = db.insert(TerryMedsDBHelper.TABLE_MEDICAMENTOS, null, values)
        db.close()
        return id != -1L
    }
    
    /**
     * Actualiza un medicamento existente por ID
     */
    fun update(medicamento: MedicamentoUsuario): Boolean {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(TerryMedsDBHelper.COLUMN_MED_USER_EMAIL, medicamento.userEmail)
            put(TerryMedsDBHelper.COLUMN_MED_NOMBRE, medicamento.nombreMedicamento)
            put(TerryMedsDBHelper.COLUMN_MED_FORMA_FARMACEUTICA, medicamento.formaFarmaceutica.name)
            put(TerryMedsDBHelper.COLUMN_MED_CONCENTRACION, medicamento.concentracion)
            put(TerryMedsDBHelper.COLUMN_MED_CANTIDAD_POR_DOSIS, medicamento.cantidadPorDosis)
            put(TerryMedsDBHelper.COLUMN_MED_UNIDAD_DOSIS, medicamento.unidadDosis.name)
            put(TerryMedsDBHelper.COLUMN_MED_NUMERO_TOTAL_DOSIS, medicamento.numeroTotalDosis)
            put(TerryMedsDBHelper.COLUMN_MED_HORA_INICIO_HORA, medicamento.horaInicioTratamiento.hora)
            put(TerryMedsDBHelper.COLUMN_MED_HORA_INICIO_MINUTO, medicamento.horaInicioTratamiento.minuto)
            put(TerryMedsDBHelper.COLUMN_MED_INTERVALO_HORAS, medicamento.intervaloEntreDosisHoras)
            put(TerryMedsDBHelper.COLUMN_MED_INSTRUCCIONES, medicamento.instruccionesAdicionales)
            put(TerryMedsDBHelper.COLUMN_MED_FECHA_INICIO, medicamento.fechaInicioTratamientoEpochDay)
            put(TerryMedsDBHelper.COLUMN_MED_ACTIVO, if (medicamento.activo) 1 else 0)
        }
        val rows = db.update(
            TerryMedsDBHelper.TABLE_MEDICAMENTOS, 
            values, 
            "${TerryMedsDBHelper.COLUMN_MED_ID} = ?", 
            arrayOf(medicamento.id)
        )
        db.close()
        return rows > 0
    }
    
    /**
     * Elimina un medicamento por ID
     */
    fun delete(medicamentoId: String): Boolean {
        val db = helper.writableDatabase
        val rows = db.delete(
            TerryMedsDBHelper.TABLE_MEDICAMENTOS, 
            "${TerryMedsDBHelper.COLUMN_MED_ID} = ?", 
            arrayOf(medicamentoId)
        )
        db.close()
        return rows > 0
    }
    
    /**
     * Obtiene todos los medicamentos
     */
    fun getAll(): List<MedicamentoUsuario> {
        val db = helper.readableDatabase
        val list = mutableListOf<MedicamentoUsuario>()
        val cursor: Cursor = db.rawQuery(
            """
            SELECT * FROM ${TerryMedsDBHelper.TABLE_MEDICAMENTOS} 
            ORDER BY ${TerryMedsDBHelper.COLUMN_MED_NOMBRE}
            """.trimIndent(),
            null
        )
        
        cursor.use {
            while (it.moveToNext()) {
                list.add(createMedicamentoFromCursor(it))
            }
        }
        db.close()
        return list
    }
    
    /**
     * Obtiene medicamentos por email de usuario
     */
    fun getByUserEmail(userEmail: String): List<MedicamentoUsuario> {
        val db = helper.readableDatabase
        val list = mutableListOf<MedicamentoUsuario>()
        val cursor = db.rawQuery(
            """
            SELECT * FROM ${TerryMedsDBHelper.TABLE_MEDICAMENTOS} 
            WHERE ${TerryMedsDBHelper.COLUMN_MED_USER_EMAIL} = ?
            ORDER BY ${TerryMedsDBHelper.COLUMN_MED_NOMBRE}
            """.trimIndent(),
            arrayOf(userEmail)
        )
        
        cursor.use {
            while (it.moveToNext()) {
                list.add(createMedicamentoFromCursor(it))
            }
        }
        db.close()
        return list
    }
    
    /**
     * Obtiene medicamentos activos por email de usuario
     */
    fun getActiveByUserEmail(userEmail: String): List<MedicamentoUsuario> {
        val db = helper.readableDatabase
        val list = mutableListOf<MedicamentoUsuario>()
        val cursor = db.rawQuery(
            """
            SELECT * FROM ${TerryMedsDBHelper.TABLE_MEDICAMENTOS} 
            WHERE ${TerryMedsDBHelper.COLUMN_MED_USER_EMAIL} = ? 
              AND ${TerryMedsDBHelper.COLUMN_MED_ACTIVO} = 1
            ORDER BY ${TerryMedsDBHelper.COLUMN_MED_NOMBRE}
            """.trimIndent(),
            arrayOf(userEmail)
        )
        
        cursor.use {
            while (it.moveToNext()) {
                list.add(createMedicamentoFromCursor(it))
            }
        }
        db.close()
        return list
    }
    
    /**
     * Busca un medicamento por ID
     */
    fun getById(medicamentoId: String): MedicamentoUsuario? {
        val db = helper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT * FROM ${TerryMedsDBHelper.TABLE_MEDICAMENTOS} 
            WHERE ${TerryMedsDBHelper.COLUMN_MED_ID} = ? 
            LIMIT 1
            """.trimIndent(),
            arrayOf(medicamentoId)
        )
        
        var medicamento: MedicamentoUsuario? = null
        cursor.use {
            if (it.moveToFirst()) {
                medicamento = createMedicamentoFromCursor(it)
            }
        }
        db.close()
        return medicamento
    }
    
    /**
     * Busca medicamentos por texto (nombre del medicamento)
     */
    fun search(query: String, userEmail: String? = null): List<MedicamentoUsuario> {
        if (query.isBlank()) {
            return if (userEmail != null) getByUserEmail(userEmail) else getAll()
        }
        
        val db = helper.readableDatabase
        val list = mutableListOf<MedicamentoUsuario>()
        val like = "%$query%"
        
        val sql = if (userEmail != null) {
            """
            SELECT * FROM ${TerryMedsDBHelper.TABLE_MEDICAMENTOS}
            WHERE ${TerryMedsDBHelper.COLUMN_MED_USER_EMAIL} = ?
              AND (${TerryMedsDBHelper.COLUMN_MED_NOMBRE} LIKE ? 
                   OR ${TerryMedsDBHelper.COLUMN_MED_CONCENTRACION} LIKE ?)
            ORDER BY ${TerryMedsDBHelper.COLUMN_MED_NOMBRE}
            """.trimIndent()
        } else {
            """
            SELECT * FROM ${TerryMedsDBHelper.TABLE_MEDICAMENTOS}
            WHERE ${TerryMedsDBHelper.COLUMN_MED_NOMBRE} LIKE ? 
               OR ${TerryMedsDBHelper.COLUMN_MED_CONCENTRACION} LIKE ?
            ORDER BY ${TerryMedsDBHelper.COLUMN_MED_NOMBRE}
            """.trimIndent()
        }
        
        val args = if (userEmail != null) {
            arrayOf(userEmail, like, like)
        } else {
            arrayOf(like, like)
        }
        
        val cursor = db.rawQuery(sql, args)
        
        cursor.use {
            while (it.moveToNext()) {
                list.add(createMedicamentoFromCursor(it))
            }
        }
        db.close()
        return list
    }
    
    /**
     * Desactiva un medicamento (marca como inactivo)
     */
    fun deactivate(medicamentoId: String): Boolean {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(TerryMedsDBHelper.COLUMN_MED_ACTIVO, 0)
        }
        val rows = db.update(
            TerryMedsDBHelper.TABLE_MEDICAMENTOS, 
            values, 
            "${TerryMedsDBHelper.COLUMN_MED_ID} = ?", 
            arrayOf(medicamentoId)
        )
        db.close()
        return rows > 0
    }
    
    /**
     * Activa un medicamento (marca como activo)
     */
    fun activate(medicamentoId: String): Boolean {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(TerryMedsDBHelper.COLUMN_MED_ACTIVO, 1)
        }
        val rows = db.update(
            TerryMedsDBHelper.TABLE_MEDICAMENTOS, 
            values, 
            "${TerryMedsDBHelper.COLUMN_MED_ID} = ?", 
            arrayOf(medicamentoId)
        )
        db.close()
        return rows > 0
    }
    
    /**
     * Función auxiliar para crear un objeto MedicamentoUsuario desde un cursor
     */
    private fun createMedicamentoFromCursor(cursor: Cursor): MedicamentoUsuario {
        return MedicamentoUsuario(
            id = cursor.getString(cursor.getColumnIndexOrThrow(TerryMedsDBHelper.COLUMN_MED_ID)),
            userEmail = cursor.getString(cursor.getColumnIndexOrThrow(TerryMedsDBHelper.COLUMN_MED_USER_EMAIL)),
            nombreMedicamento = cursor.getString(cursor.getColumnIndexOrThrow(TerryMedsDBHelper.COLUMN_MED_NOMBRE)),
            formaFarmaceutica = FormaFarmaceutica.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow(TerryMedsDBHelper.COLUMN_MED_FORMA_FARMACEUTICA))
            ),
            concentracion = cursor.getString(cursor.getColumnIndexOrThrow(TerryMedsDBHelper.COLUMN_MED_CONCENTRACION)),
            cantidadPorDosis = cursor.getFloat(cursor.getColumnIndexOrThrow(TerryMedsDBHelper.COLUMN_MED_CANTIDAD_POR_DOSIS)),
            unidadDosis = UnidadDosis.valueOf(
                cursor.getString(cursor.getColumnIndexOrThrow(TerryMedsDBHelper.COLUMN_MED_UNIDAD_DOSIS))
            ),
            numeroTotalDosis = cursor.getIntOrNull(cursor.getColumnIndexOrThrow(TerryMedsDBHelper.COLUMN_MED_NUMERO_TOTAL_DOSIS)),
            horaInicioTratamiento = HoraDelDia(
                cursor.getInt(cursor.getColumnIndexOrThrow(TerryMedsDBHelper.COLUMN_MED_HORA_INICIO_HORA)),
                cursor.getInt(cursor.getColumnIndexOrThrow(TerryMedsDBHelper.COLUMN_MED_HORA_INICIO_MINUTO))
            ),
            intervaloEntreDosisHoras = cursor.getInt(cursor.getColumnIndexOrThrow(TerryMedsDBHelper.COLUMN_MED_INTERVALO_HORAS)),
            instruccionesAdicionales = cursor.getString(cursor.getColumnIndexOrThrow(TerryMedsDBHelper.COLUMN_MED_INSTRUCCIONES)),
            fechaInicioTratamientoEpochDay = cursor.getLong(cursor.getColumnIndexOrThrow(TerryMedsDBHelper.COLUMN_MED_FECHA_INICIO)),
            activo = cursor.getInt(cursor.getColumnIndexOrThrow(TerryMedsDBHelper.COLUMN_MED_ACTIVO)) == 1
        )
    }
    
    /**
     * Extensión para manejar valores NULL en enteros
     */
    private fun Cursor.getIntOrNull(columnIndex: Int): Int? {
        return if (isNull(columnIndex)) null else getInt(columnIndex)
    }
}
