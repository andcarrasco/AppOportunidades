package com.example.oportunidades.model

// Usuario que entrega el microservicio de usuarios al iniciar sesión
data class Usuario(
    val id: Long = 0,
    val nombre: String = "",
    val correo: String = "",
    val perfil: String = "",
    val area: String = "",
    val zona: String = "",
    val activo: Boolean = true
)
