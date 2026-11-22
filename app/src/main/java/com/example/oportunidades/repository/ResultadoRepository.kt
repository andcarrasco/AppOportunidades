package com.example.oportunidades.repository

import com.example.oportunidades.OportunidadesApp
import com.example.oportunidades.model.data.local.AppDatabase
import com.example.oportunidades.model.data.local.ResultadoDao
import com.example.oportunidades.model.data.local.ResultadoEntity
import kotlinx.coroutines.flow.Flow

// Este repositorio se encarga de acceder a los datos guardados en Room
class ResultadoRepository(
    private val dao: ResultadoDao = AppDatabase.getDatabase(OportunidadesApp.instancia).resultadoDao()
) {

    fun obtenerTodos(): Flow<List<ResultadoEntity>> {
        return dao.obtenerTodos()
    }

    // Inserta si es nuevo o modifica si ya existe. Devuelve el id guardado.
    suspend fun guardar(resultado: ResultadoEntity): Long {
        return if (resultado.id == 0L) {
            dao.insertar(resultado)
        } else {
            dao.modificar(resultado)
            resultado.id
        }
    }
}
