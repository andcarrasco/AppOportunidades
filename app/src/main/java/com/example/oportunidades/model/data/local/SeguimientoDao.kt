package com.example.oportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SeguimientoDao {

    @Insert
    suspend fun insertar(seguimiento: SeguimientoEntity): Long

    @Update
    suspend fun modificar(seguimiento: SeguimientoEntity)

    @Query("SELECT * FROM seguimientos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<SeguimientoEntity>>
}
