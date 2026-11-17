package com.example.oportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OrganizacionDao {

    @Insert
    suspend fun insertar(organizacion: OrganizacionEntity): Long

    @Update
    suspend fun modificar(organizacion: OrganizacionEntity)

    @Query("SELECT * FROM organizaciones ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<OrganizacionEntity>>
}
