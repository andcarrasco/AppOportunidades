package com.example.oportunidades.repository

import com.example.oportunidades.OportunidadesApp
import com.example.oportunidades.model.data.local.AppDatabase
import com.example.oportunidades.model.data.local.ContactoDao
import com.example.oportunidades.model.data.local.ContactoEntity
import kotlinx.coroutines.flow.Flow

// Este repositorio se encarga de acceder a los datos guardados en Room
class ContactoRepository(
    private val dao: ContactoDao = AppDatabase.getDatabase(OportunidadesApp.instancia).contactoDao()
) {

    fun obtenerTodos(): Flow<List<ContactoEntity>> {
        return dao.obtenerTodos()
    }

    // Inserta si es nuevo o modifica si ya existe. Devuelve el id guardado.
    suspend fun guardar(contacto: ContactoEntity): Long {
        return if (contacto.id == 0L) {
            dao.insertar(contacto)
        } else {
            dao.modificar(contacto)
            contacto.id
        }
    }
}
