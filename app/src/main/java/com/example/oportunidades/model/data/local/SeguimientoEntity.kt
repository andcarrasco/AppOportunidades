package com.example.oportunidades.model.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seguimientos")
data class SeguimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val oportunidadId: String = "",
    val responsable: String = "",
    val tipo: String = "",
    val fecha: String = "",
    val hora: String = "",
    val resultado: String = "",
    val proximaAccion: String = "",
    val proximaFecha: String = "",
    val observaciones: String = "",
)
