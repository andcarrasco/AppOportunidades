package com.example.oportunidades.repository

import com.example.oportunidades.OportunidadesApp
import com.example.oportunidades.model.data.local.AppDatabase
import com.example.oportunidades.model.data.local.SeguimientoDao
import com.example.oportunidades.model.data.local.SeguimientoEntity
import kotlinx.coroutines.flow.Flow

// Este repositorio se encarga de acceder a los datos guardados en Room
class SeguimientoRepository(
    private val dao: SeguimientoDao = AppDatabase.getDatabase(OportunidadesApp.instancia).seguimientoDao()
) {

    fun obtenerTodos(): Flow<List<SeguimientoEntity>> {
        return dao.obtenerTodos()
    }

    // Inserta si es nuevo o modifica si ya existe. Devuelve el id guardado.
    suspend fun guardar(seguimiento: SeguimientoEntity): Long {
        return if (seguimiento.id == 0L) {
            dao.insertar(seguimiento)
        } else {
            dao.modificar(seguimiento)
            seguimiento.id
        }
    }
}
