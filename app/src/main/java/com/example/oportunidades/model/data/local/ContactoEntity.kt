package com.example.oportunidades.model.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contactos")
data class ContactoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val organizacionId: String = "",
    val nombre: String = "",
    val cargo: String = "",
    val area: String = "",
    val correo: String = "",
    val telefono: String = "",
    val medio: String = "",
    val estado: String = "",
    val observaciones: String = "",
)
