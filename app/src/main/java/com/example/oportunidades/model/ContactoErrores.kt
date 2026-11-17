package com.example.oportunidades.model

// Modelo que almacena posibles errores individuales del formulario
data class ContactoErrores(
    val organizacionId: String? = null,
    val nombre: String? = null,
    val cargo: String? = null,
    val area: String? = null,
    val correo: String? = null,
    val telefono: String? = null,
    val medio: String? = null,
    val estado: String? = null,
    val observaciones: String? = null
)
