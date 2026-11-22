package com.example.oportunidades.repository

import com.example.oportunidades.OportunidadesApp
import com.example.oportunidades.model.CambioEstado
import com.example.oportunidades.model.Fechas
import com.example.oportunidades.model.data.local.AppDatabase
import com.example.oportunidades.model.data.local.OportunidadDao
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.model.data.local.SeguimientoDao
import com.example.oportunidades.model.data.local.SeguimientoEntity
import com.example.oportunidades.remote.OportunidadApiService
import com.example.oportunidades.remote.RetrofitInstance
import kotlinx.coroutines.flow.Flow

// Las oportunidades se guardan en el microservicio y se deja una copia en Room
class OportunidadRepository(
    private val dao: OportunidadDao = AppDatabase.getDatabase(OportunidadesApp.instancia).oportunidadDao(),
    private val seguimientoDao: SeguimientoDao = AppDatabase.getDatabase(OportunidadesApp.instancia).seguimientoDao(),
    private val api: OportunidadApiService = RetrofitInstance.oportunidadesApi
) {

    // Copia local, disponible aunque no haya conexión
    fun obtenerTodos(): Flow<List<OportunidadEntity>> {
        return dao.obtenerTodos()
    }

    // Trae las oportunidades del microservicio y actualiza la copia local
    suspend fun sincronizar(): List<OportunidadEntity> {
        val lista = api.getOportunidades()
        dao.reemplazarTodos(lista)
        return lista
    }

    // POST si es nueva o PUT si ya existe. Devuelve la oportunidad con el id del microservicio.
    suspend fun guardar(oportunidad: OportunidadEntity): OportunidadEntity {
        val anterior = if (oportunidad.id == 0L) null else dao.obtener(oportunidad.id)
        val guardada = if (oportunidad.id == 0L) {
            api.crear(oportunidad)
        } else {
            api.actualizar(oportunidad.id, oportunidad)
        }
        dao.guardar(guardada)

        if (anterior == null || anterior.estado != guardada.estado) {
            registrarCambio(guardada, anterior?.estado)
        }
        return guardada
    }

    // PATCH: cambia solo el estado de la oportunidad
    suspend fun cambiarEstado(oportunidad: OportunidadEntity, estado: String): OportunidadEntity {
        val guardada = api.cambiarEstado(oportunidad.id, CambioEstado(estado))
        dao.guardar(guardada)

        if (oportunidad.estado != guardada.estado) {
            registrarCambio(guardada, oportunidad.estado)
        }
        return guardada
    }

    // DELETE en el microservicio y en la copia local
    suspend fun eliminar(oportunidad: OportunidadEntity) {
        val respuesta = api.eliminar(oportunidad.id)
        if (!respuesta.isSuccessful) {
            throw Exception("No se pudo eliminar en el servidor")
        }
        dao.eliminar(oportunidad)
    }

    // Deja el cambio de estado en el historial de la oportunidad
    private suspend fun registrarCambio(oportunidad: OportunidadEntity, estadoAnterior: String?) {
        seguimientoDao.insertar(
            SeguimientoEntity(
                oportunidadId = oportunidad.id.toString(),
                responsable = oportunidad.responsable,
                tipo = "Cambio de estado",
                fecha = Fechas.hoy(),
                hora = Fechas.hora(),
                resultado = if (estadoAnterior == null)
                    "Registro: ${oportunidad.estado}"
                else "$estadoAnterior → ${oportunidad.estado}"
            )
        )
    }
}
