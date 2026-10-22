package com.example.oportunidades.model
data class ResultadoUiState(
    val id: Long = 0,
    val oportunidadId: String = "",
    val fecha: String = "",
    val estado: String = "",
    val asistentes: String = "",
    val contactos: String = "",
    val interesados: String = "",
    val inscripciones: String = "",
    val observaciones: String = "",
    val errores: ResultadoErrores = ResultadoErrores()
)
