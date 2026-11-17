package com.example.oportunidades.model

// Modelo principal que representa el estado del formulario de acceso
data class UsuarioUiState(
    val correo: String = "",              // Correo electrónico
    val clave: String = "",               // Contraseña
    val activo: Boolean = false,          // Indica si la sesión está iniciada
    val errores: UsuarioErrores = UsuarioErrores() // Objeto que contiene los errores por campo
)
