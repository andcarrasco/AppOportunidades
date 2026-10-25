package com.example.oportunidades.model

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Las fechas se escriben siempre AAAA-MM-DD y las horas HH:mm.
object Fechas {
    private val formatoHora = DateTimeFormatter.ofPattern("HH:mm")

    fun hoy(): String {
        return LocalDate.now().toString()
    }

    fun hora(): String {
        return LocalTime.now().format(formatoHora)
    }

    // parse rechaza fechas que no existen, por ejemplo el 30 de febrero.
    fun valida(valor: String): Boolean {
        if (valor.length != 10) {
            return false
        }

        return try {
            val fecha = LocalDate.parse(valor)
            fecha.toString() == valor
        } catch (e: Exception) {
            false
        }
    }

    fun horaValida(valor: String): Boolean {
        if (valor.length != 5) {
            return false
        }

        return try {
            val hora = LocalTime.parse(valor)
            hora.format(formatoHora) == valor
        } catch (e: Exception) {
            false
        }
    }

    fun proxima(valor: String): Boolean {
        if (!valida(valor)) {
            return false
        }

        val fechaActual = LocalDate.now()
        val inicio = fechaActual.toString()
        val termino = fechaActual.plusDays(7).toString()
        return valor >= inicio && valor <= termino
    }
}
