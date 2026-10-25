package com.example.oportunidades.model

data class UsuarioUiState(
    val correo: String = "",
    val clave: String = "",
    val activo: Boolean = false,
    val errores: UsuarioErrores = UsuarioErrores(),
)
