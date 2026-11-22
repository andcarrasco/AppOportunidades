package com.example.oportunidades.repository

import com.example.oportunidades.model.Feriado
import com.example.oportunidades.remote.FeriadoApiService
import com.example.oportunidades.remote.RetrofitInstance

// Este repositorio obtiene los feriados de Chile desde la API externa
class FeriadoRepository(
    private val api: FeriadoApiService = RetrofitInstance.feriadosApi
) {

    suspend fun getFeriados(anio: Int): List<Feriado> {
        return api.getFeriados(anio)
    }
}
