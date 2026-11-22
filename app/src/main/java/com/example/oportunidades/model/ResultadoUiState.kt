package com.example.oportunidades.model

// Modelo principal que representa el estado del formulario de resultado
data class ResultadoUiState(
    val id: Long = 0,                     // Id del registro (0 = nuevo)
    val oportunidadId: String = "",       // Oportunidad a la que pertenece
    val fecha: String = "",
    val estado: String = "",              // Resultado final
    val asistentes: String = "",
    val contactos: String = "",
    val interesados: String = "",
    val inscripciones: String = "",
    val observaciones: String = "",
    val proximasAcciones: String = "",
    val errores: ResultadoErrores = ResultadoErrores() // Objeto que contiene los errores por campo
)
