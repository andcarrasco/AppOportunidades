package com.example.oportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Insertar sigue el ejemplo de UsuarioDao. Las consultas se adaptan al caso.
@Dao
interface ContactoDao {
    @Insert suspend fun insertar(registro: ContactoEntity): Long

    @Query("SELECT * FROM contactos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ContactoEntity>>

    @Query("SELECT * FROM contactos WHERE id = :id LIMIT 1")
    suspend fun obtener(id: Long): ContactoEntity?

    @Query(
        "UPDATE contactos SET " +
            "organizacionId = :organizacionId, nombre = :nombre, cargo = :cargo, " +
            "area = :area, correo = :correo, telefono = :telefono, " +
            "medio = :medio, estado = :estado, observaciones = :observaciones WHERE id = :id"
    )
    suspend fun modificar(
        id: Long,
        organizacionId: String,
        nombre: String,
        cargo: String,
        area: String,
        correo: String,
        telefono: String,
        medio: String,
        estado: String,
        observaciones: String,
    )
}
