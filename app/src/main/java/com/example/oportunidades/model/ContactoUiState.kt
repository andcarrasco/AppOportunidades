package com.example.oportunidades.model

// Modelo principal que representa el estado del formulario de contacto
data class ContactoUiState(
    val id: Long = 0,                     // Id del registro (0 = nuevo)
    val organizacionId: String = "",      // Organización a la que pertenece
    val nombre: String = "",
    val cargo: String = "",
    val area: String = "",
    val correo: String = "",
    val telefono: String = "",
    val medio: String = "",               // Medio de contacto preferente
    val estado: String = "",              // Estado del contacto
    val observaciones: String = "",
    val errores: ContactoErrores = ContactoErrores() // Objeto que contiene los errores por campo
)
