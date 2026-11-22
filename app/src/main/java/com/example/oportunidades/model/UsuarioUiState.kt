package com.example.oportunidades.model

// Modelo principal que representa el estado del formulario de acceso
data class UsuarioUiState(
    val correo: String = "",              // Correo electrónico
    val clave: String = "",               // Contraseña
    val activo: Boolean = false,          // Indica si la sesión está iniciada
    val nombre: String = "",              // Nombre del usuario que ingresó
    val perfil: String = "",              // Perfil con el que ingresó
    val mensaje: String = "",             // Aviso para la UI (por ejemplo, ingreso sin conexión)
    val errores: UsuarioErrores = UsuarioErrores() // Objeto que contiene los errores por campo
)
