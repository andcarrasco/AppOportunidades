package com.example.oportunidades.model.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "resultados")
data class ResultadoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val oportunidadId: String = "",
    val fecha: String = "",
    val estado: String = "",
    val asistentes: String = "",
    val contactos: String = "",
    val interesados: String = "",
    val inscripciones: String = "",
    val observaciones: String = "",
)
