package com.example.oportunidades.model
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
object Fechas {
    fun hoy(): String = LocalDate.now().toString()
    fun hora(): String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    fun valida(valor: String): Boolean = try {
        valor.length == 10 && LocalDate.parse(valor).toString() == valor
    } catch (e: Exception) { false }
    fun horaValida(valor: String): Boolean = try {
        valor.length == 5 && LocalTime.parse(valor).format(DateTimeFormatter.ofPattern("HH:mm")) == valor
    } catch (e: Exception) { false }
    fun proxima(valor: String): Boolean = valida(valor) && valor >= hoy() && valor <= LocalDate.now().plusDays(7).toString()
}
