package com.example.oportunidades.model
data class FiltrosUiState(
    val nombre: String = "", val fecha: String = "", val estado: String = "", val tipo: String = "",
    val comuna: String = "", val region: String = "", val prioridad: String = "",
    val responsable: String = "", val organizacionId: String = ""
)
