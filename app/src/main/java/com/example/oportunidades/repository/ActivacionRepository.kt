package com.example.oportunidades.repository

import com.example.oportunidades.OportunidadesApp
import com.example.oportunidades.model.data.local.AppDatabase
import com.example.oportunidades.model.data.local.ActivacionDao
import com.example.oportunidades.model.data.local.ActivacionEntity
import kotlinx.coroutines.flow.Flow

// Este repositorio se encarga de acceder a los datos guardados en Room
class ActivacionRepository(
    private val dao: ActivacionDao = AppDatabase.getDatabase(OportunidadesApp.instancia).activacionDao()
) {

    fun obtenerTodos(): Flow<List<ActivacionEntity>> {
        return dao.obtenerTodos()
    }

    // Inserta si es nuevo o modifica si ya existe. Devuelve el id guardado.
    suspend fun guardar(activacion: ActivacionEntity): Long {
        return if (activacion.id == 0L) {
            dao.insertar(activacion)
        } else {
            dao.modificar(activacion)
            activacion.id
        }
    }
}
