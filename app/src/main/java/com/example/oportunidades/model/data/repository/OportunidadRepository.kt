package com.example.oportunidades.model.data.repository

import androidx.room.withTransaction
import com.example.oportunidades.model.Fechas
import com.example.oportunidades.model.data.local.AppDatabase
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.model.data.local.SeguimientoEntity
import kotlinx.coroutines.flow.Flow

class OportunidadRepository(private val db: AppDatabase) {
    private val dao = db.oportunidadDao()

    fun obtenerTodos(): Flow<List<OportunidadEntity>> = dao.obtenerTodos()

    suspend fun organizacionExiste(id: Long): Boolean = db.organizacionDao().obtener(id) != null

    // Room guarda la ficha y su historial juntos. Si algo falla, revierte ambos.
    suspend fun guardar(registro: OportunidadEntity): Long {
        return db.withTransaction {
            val anterior = if (registro.id == 0L) null else dao.obtener(registro.id)
            var id = registro.id

            if (id == 0L) {
                id = dao.insertar(registro)
            } else {
                modificar(registro)
            }

            if (anterior == null || anterior.estado != registro.estado) {
                var descripcion = "Registro: ${registro.estado}"
                if (anterior != null) {
                    descripcion = "${anterior.estado} → ${registro.estado}"
                }

                val cambio =
                    SeguimientoEntity(
                        oportunidadId = id.toString(),
                        responsable = registro.responsable,
                        tipo = "Cambio de estado",
                        fecha = Fechas.hoy(),
                        hora = Fechas.hora(),
                        resultado = descripcion,
                    )
                db.seguimientoDao().insertar(cambio)
            }

            id
        }
    }

    private suspend fun modificar(registro: OportunidadEntity) {
        dao.modificar(
            id = registro.id,
            nombre = registro.nombre,
            tipo = registro.tipo,
            descripcion = registro.descripcion,
            fuente = registro.fuente,
            fechaDeteccion = registro.fechaDeteccion,
            responsable = registro.responsable,
            prioridad = registro.prioridad,
            potencial = registro.potencial,
            estado = registro.estado,
            organizacionId = registro.organizacionId,
            fechaInicio = registro.fechaInicio,
            fechaTermino = registro.fechaTermino,
            horario = registro.horario,
            comuna = registro.comuna,
            region = registro.region,
            ubicacion = registro.ubicacion,
            publico = registro.publico,
            asistentes = registro.asistentes,
            condiciones = registro.condiciones,
            requisitos = registro.requisitos,
            costos = registro.costos,
            espacios = registro.espacios,
            observaciones = registro.observaciones,
            actualizacion = registro.actualizacion,
        )
    }
}
