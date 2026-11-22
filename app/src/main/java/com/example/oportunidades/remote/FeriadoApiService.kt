package com.example.oportunidades.remote

import com.example.oportunidades.model.Feriado
import retrofit2.http.GET
import retrofit2.http.Path

// API externa: feriados de Chile
interface FeriadoApiService {

    @GET("api/v3/PublicHolidays/{anio}/CL")
    suspend fun getFeriados(@Path("anio") anio: Int): List<Feriado>
}
