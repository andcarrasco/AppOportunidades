package com.example.oportunidades.model

// Modelo principal que representa el estado del formulario de organización
data class OrganizacionUiState(
    val id: Long = 0,                     // Id del registro (0 = nuevo)
    val nombre: String = "",
    val tipo: String = "",                // Tipo de organización
    val rubro: String = "",
    val comuna: String = "",
    val region: String = "",
    val sitioWeb: String = "",
    val publico: String = "",             // Público asociado
    val observaciones: String = "",
    val errores: OrganizacionErrores = OrganizacionErrores() // Objeto que contiene los errores por campo
)
