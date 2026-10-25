package com.example.oportunidades.model
data class SeguimientoUiState(
    val id: Long = 0,
    val oportunidadId: String = "",
    val responsable: String = "",
    val tipo: String = "",
    val fecha: String = "",
    val hora: String = "",
    val resultado: String = "",
    val proximaAccion: String = "",
    val proximaFecha: String = "",
    val observaciones: String = "",
    val errores: SeguimientoErrores = SeguimientoErrores(),
)
