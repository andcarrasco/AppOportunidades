package com.example.oportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Insertar sigue el ejemplo de UsuarioDao. Las consultas se adaptan al caso.
@Dao
interface ResultadoDao {
    @Insert suspend fun insertar(registro: ResultadoEntity): Long

    @Query("SELECT * FROM resultados ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ResultadoEntity>>

    @Query("SELECT * FROM resultados WHERE id = :id LIMIT 1")
    suspend fun obtener(id: Long): ResultadoEntity?

    @Query(
        "UPDATE resultados SET " +
            "oportunidadId = :oportunidadId, fecha = :fecha, estado = :estado, " +
            "asistentes = :asistentes, contactos = :contactos, interesados = :interesados, " +
            "inscripciones = :inscripciones, observaciones = :observaciones WHERE id = :id"
    )
    suspend fun modificar(
        id: Long,
        oportunidadId: String,
        fecha: String,
        estado: String,
        asistentes: String,
        contactos: String,
        interesados: String,
        inscripciones: String,
        observaciones: String,
    )
}
