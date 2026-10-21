package com.example.oportunidades.model.data.local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Adaptación de UsuarioDao: las mismas @Insert y @Query para el caso.
@Dao
interface OportunidadDao {
    @Insert suspend fun insertar(registro: OportunidadEntity): Long
    @Query("SELECT * FROM oportunidades ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<OportunidadEntity>>
    @Query("SELECT * FROM oportunidades WHERE id = :id LIMIT 1")
    suspend fun obtener(id: Long): OportunidadEntity?
    @Query("UPDATE oportunidades SET nombre = :nombre, tipo = :tipo, descripcion = :descripcion, fuente = :fuente, fechaDeteccion = :fechaDeteccion, responsable = :responsable, prioridad = :prioridad, potencial = :potencial, estado = :estado, organizacionId = :organizacionId, fechaInicio = :fechaInicio, fechaTermino = :fechaTermino, horario = :horario, comuna = :comuna, region = :region, ubicacion = :ubicacion, publico = :publico, asistentes = :asistentes, condiciones = :condiciones, requisitos = :requisitos, costos = :costos, espacios = :espacios, observaciones = :observaciones, actualizacion = :actualizacion WHERE id = :id")
    suspend fun modificar(id: Long, nombre: String, tipo: String, descripcion: String, fuente: String, fechaDeteccion: String, responsable: String, prioridad: String, potencial: String, estado: String, organizacionId: String, fechaInicio: String, fechaTermino: String, horario: String, comuna: String, region: String, ubicacion: String, publico: String, asistentes: String, condiciones: String, requisitos: String, costos: String, espacios: String, observaciones: String, actualizacion: String)
}
