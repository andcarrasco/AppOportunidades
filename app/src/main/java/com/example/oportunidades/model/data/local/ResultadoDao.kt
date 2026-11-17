package com.example.oportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultadoDao {

    @Insert
    suspend fun insertar(resultado: ResultadoEntity): Long

    @Update
    suspend fun modificar(resultado: ResultadoEntity)

    @Query("SELECT * FROM resultados ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ResultadoEntity>>
}
