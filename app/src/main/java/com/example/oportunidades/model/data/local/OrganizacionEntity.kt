package com.example.oportunidades.model.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "organizaciones")
data class OrganizacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String = "",
    val tipo: String = "",
    val rubro: String = "",
    val comuna: String = "",
    val region: String = "",
    val sitioWeb: String = "",
    val publico: String = "",
    val observaciones: String = ""
)
