package com.example.oportunidades.repository

import com.example.oportunidades.OportunidadesApp
import com.example.oportunidades.model.data.local.AppDatabase
import com.example.oportunidades.model.data.local.OrganizacionDao
import com.example.oportunidades.model.data.local.OrganizacionEntity
import kotlinx.coroutines.flow.Flow

// Este repositorio se encarga de acceder a los datos guardados en Room
class OrganizacionRepository(
    private val dao: OrganizacionDao = AppDatabase.getDatabase(OportunidadesApp.instancia).organizacionDao()
) {

    fun obtenerTodos(): Flow<List<OrganizacionEntity>> {
        return dao.obtenerTodos()
    }

    // Inserta si es nuevo o modifica si ya existe. Devuelve el id guardado.
    suspend fun guardar(organizacion: OrganizacionEntity): Long {
        return if (organizacion.id == 0L) {
            dao.insertar(organizacion)
        } else {
            dao.modificar(organizacion)
            organizacion.id
        }
    }
}
