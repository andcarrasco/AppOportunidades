package com.example.oportunidades.model.data.local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Adaptación de UsuarioDao: las mismas @Insert y @Query para el caso.
@Dao
interface SeguimientoDao {
    @Insert suspend fun insertar(registro: SeguimientoEntity): Long
    @Query("SELECT * FROM seguimientos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<SeguimientoEntity>>
    @Query("SELECT * FROM seguimientos WHERE id = :id LIMIT 1")
    suspend fun obtener(id: Long): SeguimientoEntity?
    @Query("UPDATE seguimientos SET oportunidadId = :oportunidadId, responsable = :responsable, tipo = :tipo, fecha = :fecha, hora = :hora, resultado = :resultado, proximaAccion = :proximaAccion, proximaFecha = :proximaFecha, observaciones = :observaciones WHERE id = :id")
    suspend fun modificar(id: Long, oportunidadId: String, responsable: String, tipo: String, fecha: String, hora: String, resultado: String, proximaAccion: String, proximaFecha: String, observaciones: String)
}
