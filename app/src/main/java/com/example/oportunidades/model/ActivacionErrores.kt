package com.example.oportunidades.model

// Modelo que almacena posibles errores individuales del formulario
data class ActivacionErrores(
    val oportunidadId: String? = null,
    val responsable: String? = null,
    val fecha: String? = null,
    val condiciones: String? = null
)
