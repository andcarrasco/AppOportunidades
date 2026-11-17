package com.example.oportunidades.model

// Modelo que almacena posibles errores individuales del formulario
data class UsuarioErrores(
    val correo: String? = null,
    val clave: String? = null
)
