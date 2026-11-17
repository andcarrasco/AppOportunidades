package com.example.oportunidades.model

// Modelo principal que representa el estado del formulario de seguimiento
data class SeguimientoUiState(
    val id: Long = 0,                     // Id del registro (0 = nuevo)
    val oportunidadId: String = "",       // Oportunidad a la que pertenece
    val responsable: String = "",
    val tipo: String = "",                // Tipo de acción
    val fecha: String = "",
    val hora: String = "",
    val resultado: String = "",
    val proximaAccion: String = "",
    val proximaFecha: String = "",
    val observaciones: String = "",
    val errores: SeguimientoErrores = SeguimientoErrores() // Objeto que contiene los errores por campo
)
