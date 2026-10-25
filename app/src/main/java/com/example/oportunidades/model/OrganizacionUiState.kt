package com.example.oportunidades.model
data class OrganizacionUiState(
    val id: Long = 0,
    val nombre: String = "",
    val tipo: String = "",
    val rubro: String = "",
    val comuna: String = "",
    val region: String = "",
    val sitioWeb: String = "",
    val publico: String = "",
    val observaciones: String = "",
    val errores: OrganizacionErrores = OrganizacionErrores(),
)
