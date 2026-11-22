package com.example.oportunidades.remote

import com.example.oportunidades.model.Credenciales
import com.example.oportunidades.model.Usuario
import retrofit2.http.Body
import retrofit2.http.POST

// Endpoints del microservicio de usuarios
interface UsuarioApiService {

    @POST("api/usuarios/login")
    suspend fun login(@Body credenciales: Credenciales): Usuario
}
