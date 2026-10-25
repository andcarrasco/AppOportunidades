package com.example.oportunidades.model.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Insertar sigue el ejemplo de UsuarioDao. Las consultas se adaptan al caso.
@Dao
interface OrganizacionDao {
    @Insert suspend fun insertar(registro: OrganizacionEntity): Long

    @Query("SELECT * FROM organizaciones ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<OrganizacionEntity>>

    @Query("SELECT * FROM organizaciones WHERE id = :id LIMIT 1")
    suspend fun obtener(id: Long): OrganizacionEntity?

    @Query(
        "UPDATE organizaciones SET " +
            "nombre = :nombre, tipo = :tipo, rubro = :rubro, " +
            "comuna = :comuna, region = :region, sitioWeb = :sitioWeb, " +
            "publico = :publico, observaciones = :observaciones WHERE id = :id"
    )
    suspend fun modificar(
        id: Long,
        nombre: String,
        tipo: String,
        rubro: String,
        comuna: String,
        region: String,
        sitioWeb: String,
        publico: String,
        observaciones: String,
    )
}
