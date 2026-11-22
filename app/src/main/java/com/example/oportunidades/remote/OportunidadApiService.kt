package com.example.oportunidades.remote

import com.example.oportunidades.model.CambioEstado
import com.example.oportunidades.model.data.local.OportunidadEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Endpoints del microservicio de oportunidades (CRUD)
interface OportunidadApiService {

    @GET("api/oportunidades")
    suspend fun getOportunidades(): List<OportunidadEntity>

    @POST("api/oportunidades")
    suspend fun crear(@Body oportunidad: OportunidadEntity): OportunidadEntity

    @PUT("api/oportunidades/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body oportunidad: OportunidadEntity
    ): OportunidadEntity

    @PATCH("api/oportunidades/{id}/estado")
    suspend fun cambiarEstado(
        @Path("id") id: Long,
        @Body cambio: CambioEstado
    ): OportunidadEntity

    @DELETE("api/oportunidades/{id}")
    suspend fun eliminar(@Path("id") id: Long): Response<Unit>
}
