package com.example.oportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface OportunidadDao {

    // Inserta o reemplaza usando el id que entrega el microservicio
    @Upsert
    suspend fun guardar(oportunidad: OportunidadEntity)

    @Upsert
    suspend fun guardarTodos(oportunidades: List<OportunidadEntity>)

    @Delete
    suspend fun eliminar(oportunidad: OportunidadEntity)

    @Query("DELETE FROM oportunidades")
    suspend fun eliminarTodos()

    // Deja en Room la misma lista que tiene el microservicio
    @Transaction
    suspend fun reemplazarTodos(oportunidades: List<OportunidadEntity>) {
        eliminarTodos()
        guardarTodos(oportunidades)
    }

    @Query("SELECT * FROM oportunidades ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<OportunidadEntity>>

    @Query("SELECT * FROM oportunidades WHERE id = :id LIMIT 1")
    suspend fun obtener(id: Long): OportunidadEntity?
}
