package com.example.oportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OportunidadDao {

    @Insert
    suspend fun insertar(oportunidad: OportunidadEntity): Long

    @Update
    suspend fun modificar(oportunidad: OportunidadEntity)

    @Query("SELECT * FROM oportunidades ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<OportunidadEntity>>

    @Query("SELECT * FROM oportunidades WHERE id = :id LIMIT 1")
    suspend fun obtener(id: Long): OportunidadEntity?
}
