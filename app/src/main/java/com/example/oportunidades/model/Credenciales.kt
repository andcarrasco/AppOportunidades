package com.example.oportunidades.model

// Datos que se envían al microservicio para iniciar sesión
data class Credenciales(
    val correo: String,
    val clave: String
)
