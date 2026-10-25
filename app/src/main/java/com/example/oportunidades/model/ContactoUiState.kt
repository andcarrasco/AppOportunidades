package com.example.oportunidades.model
data class ContactoUiState(
    val id: Long = 0,
    val organizacionId: String = "",
    val nombre: String = "",
    val cargo: String = "",
    val area: String = "",
    val correo: String = "",
    val telefono: String = "",
    val medio: String = "",
    val estado: String = "",
    val observaciones: String = "",
    val errores: ContactoErrores = ContactoErrores(),
)
