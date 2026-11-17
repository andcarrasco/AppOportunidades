package com.example.oportunidades.model

import android.net.Uri

// Modelo que representa las fotografías de evidencia de una oportunidad
data class EvidenciaUiState(
    val oportunidadId: Long = 0,
    val imagenes: List<Uri> = emptyList(),
    val capturando: Boolean = false,      // Indica si la cámara está abierta
    val mensaje: String = ""
)
