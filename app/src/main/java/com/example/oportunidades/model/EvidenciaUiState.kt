package com.example.oportunidades.model

import android.net.Uri

data class EvidenciaUiState(
    val oportunidadId: Long = 0,
    val imagenes: List<Uri> = emptyList(),
    val capturando: Boolean = false,
    val mensaje: String = "",
)
