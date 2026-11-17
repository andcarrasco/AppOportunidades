package com.example.oportunidades.model

// Modelo que almacena posibles errores individuales del formulario
data class OrganizacionErrores(
    val nombre: String? = null,
    val tipo: String? = null,
    val rubro: String? = null,
    val comuna: String? = null,
    val region: String? = null,
    val sitioWeb: String? = null,
    val publico: String? = null,
    val observaciones: String? = null
)
