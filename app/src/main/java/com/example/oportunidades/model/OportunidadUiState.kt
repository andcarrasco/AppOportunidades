package com.example.oportunidades.model

// Modelo principal que representa el estado del formulario de oportunidad
data class OportunidadUiState(
    val id: Long = 0,                     // Id del registro (0 = nuevo)
    val nombre: String = "",
    val tipo: String = "",                // Tipo de evento
    val descripcion: String = "",
    val fuente: String = "",              // Fuente de información
    val fechaDeteccion: String = "",
    val responsable: String = "",
    val prioridad: String = "",
    val potencial: String = "",           // Potencial comercial
    val estado: String = "Evento identificado", // Estado de gestión
    val organizacionId: String = "",      // Organización asociada
    val fechaInicio: String = "",
    val fechaTermino: String = "",
    val horario: String = "",
    val comuna: String = "",
    val region: String = "",
    val ubicacion: String = "",
    val publico: String = "",
    val asistentes: String = "",
    val condiciones: String = "",
    val requisitos: String = "",
    val costos: String = "",
    val espacios: String = "",
    val observaciones: String = "",
    val errores: OportunidadErrores = OportunidadErrores() // Objeto que contiene los errores por campo
)
