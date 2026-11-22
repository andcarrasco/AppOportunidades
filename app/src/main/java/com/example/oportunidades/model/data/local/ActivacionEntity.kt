package com.example.oportunidades.model.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activaciones")
data class ActivacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val oportunidadId: String = "",
    val confirmada: Boolean = false,
    val responsable: String = "",
    val fecha: String = "",
    val condiciones: String = "",
    val observaciones: String = ""
)
