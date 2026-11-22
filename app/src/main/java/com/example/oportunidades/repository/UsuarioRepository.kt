package com.example.oportunidades.repository

import com.example.oportunidades.model.Catalogos
import com.example.oportunidades.model.Credenciales
import com.example.oportunidades.model.Usuario
import com.example.oportunidades.remote.RetrofitInstance
import com.example.oportunidades.remote.UsuarioApiService

// Este repositorio se encarga del inicio de sesión
class UsuarioRepository(
    private val api: UsuarioApiService = RetrofitInstance.usuariosApi
) {

    // Valida las credenciales en el microservicio de usuarios
    suspend fun login(correo: String, clave: String): Usuario {
        return api.login(Credenciales(correo, clave))
    }

    // Cuentas ficticias para poder ingresar si el microservicio no responde
    fun loginSinConexion(correo: String, clave: String): Usuario? {
        if (clave != Catalogos.CLAVE_DEMO) {
            return null
        }
        return Catalogos.usuariosDemo.find { it.correo == correo }
    }
}
