package com.example.oportunidades.model

// Feriado obtenido desde la API pública date.nager.at
data class Feriado(
    val date: String,       // Fecha del feriado (AAAA-MM-DD)
    val localName: String,  // Nombre en español
    val name: String        // Nombre en inglés
)
