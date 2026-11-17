package com.example.oportunidades.model

// Modelo que almacena posibles errores individuales del formulario
data class ResultadoErrores(
    val oportunidadId: String? = null,
    val fecha: String? = null,
    val estado: String? = null,
    val asistentes: String? = null,
    val contactos: String? = null,
    val interesados: String? = null,
    val inscripciones: String? = null,
    val observaciones: String? = null
)
