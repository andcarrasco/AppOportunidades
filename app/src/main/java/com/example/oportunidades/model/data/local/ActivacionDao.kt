package com.example.oportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivacionDao {

    @Insert
    suspend fun insertar(activacion: ActivacionEntity): Long

    @Update
    suspend fun modificar(activacion: ActivacionEntity)

    @Query("SELECT * FROM activaciones ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ActivacionEntity>>
}
