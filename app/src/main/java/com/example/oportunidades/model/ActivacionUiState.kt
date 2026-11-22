package com.example.oportunidades.model

// Modelo principal que representa el estado del formulario de coordinación de la activación
data class ActivacionUiState(
    val id: Long = 0,                     // Id del registro (0 = nuevo)
    val oportunidadId: String = "",       // Oportunidad a la que pertenece
    val confirmada: Boolean = false,      // Participación confirmada
    val responsable: String = "",         // Responsable asignado a la activación
    val fecha: String = "",               // Fecha de la activación
    val condiciones: String = "",         // Condiciones de participación acordadas
    val observaciones: String = "",
    val errores: ActivacionErrores = ActivacionErrores() // Objeto que contiene los errores por campo
)
