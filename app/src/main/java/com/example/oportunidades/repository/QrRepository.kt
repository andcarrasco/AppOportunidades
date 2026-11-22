package com.example.oportunidades.repository

import com.example.oportunidades.model.QrResult
import java.text.Normalizer

// Este repositorio procesa el contenido de los códigos QR
class QrRepository {

    // Nombres aceptados en el código QR para cada campo de la oportunidad
    private val claves = mapOf(
        "nombre" to "nombre",
        "oportunidad" to "nombre",
        "evento" to "nombre",
        "tipo" to "tipo",
        "tipodeevento" to "tipo",
        "descripcion" to "descripcion",
        "fuente" to "fuente",
        "fechadedeteccion" to "fechaDeteccion",
        "fechadeteccion" to "fechaDeteccion",
        "responsable" to "responsable",
        "prioridad" to "prioridad",
        "potencial" to "potencial",
        "estado" to "estado",
        "organizacion" to "organizacion",
        "empresa" to "organizacion",
        "fechainicio" to "fechaInicio",
        "fechadeinicio" to "fechaInicio",
        "fecha" to "fechaInicio",
        "fechatermino" to "fechaTermino",
        "fechadetermino" to "fechaTermino",
        "horario" to "horario",
        "hora" to "horario",
        "comuna" to "comuna",
        "region" to "region",
        "ubicacion" to "ubicacion",
        "lugar" to "ubicacion",
        "publico" to "publico",
        "asistentes" to "asistentes",
        "condiciones" to "condiciones",
        "requisitos" to "requisitos",
        "costos" to "costos",
        "costo" to "costos",
        "espacios" to "espacios",
        "observaciones" to "observaciones"
    )

    fun processQrContent(content: String): QrResult {
        return QrResult(content)
    }

    // Lee un código con una línea por campo, por ejemplo "Nombre: Feria Demo" o "comuna=Santiago".
    // Si no trae campos reconocidos, todo el texto se usa como descripción.
    fun leerCampos(content: String): Map<String, String> {
        val campos = mutableMapOf<String, String>()
        content.lines().forEach { linea ->
            val separador = linea.indexOfFirst { it == ':' || it == '=' }
            if (separador > 0) {
                val campo = claves[normalizar(linea.substring(0, separador))]
                val valor = linea.substring(separador + 1).trim()
                if (campo != null && valor.isNotEmpty()) {
                    campos[campo] = valor
                }
            }
        }
        if (campos.isEmpty() && content.isNotBlank()) {
            campos["descripcion"] = content.trim()
        }
        return campos
    }

    // Quita tildes, mayúsculas y espacios para comparar los nombres de los campos
    private fun normalizar(texto: String): String {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replace(Regex("\\p{M}"), "")
            .lowercase()
            .filter { it.isLetter() }
    }
}
