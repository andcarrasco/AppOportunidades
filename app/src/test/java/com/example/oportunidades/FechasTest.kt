package com.example.oportunidades

import com.example.oportunidades.model.Fechas
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class FechasTest : StringSpec({

    "hoy y hora usan los formatos AAAA-MM-DD y HH:mm" {
        Fechas.hoy() shouldBe LocalDate.now().toString()
        Fechas.horaValida(Fechas.hora()) shouldBe true
    }

    "una fecha imposible o mal escrita es rechazada" {
        Fechas.valida("2026-02-30") shouldBe false
        Fechas.valida("2026-2-01") shouldBe false
        Fechas.valida("2026/12/01") shouldBe false
        Fechas.valida("") shouldBe false
        Fechas.valida("2028-02-29") shouldBe true
        Fechas.valida("2026-12-15") shouldBe true
    }

    "una hora fuera de rango o mal escrita es rechazada" {
        Fechas.horaValida("25:00") shouldBe false
        Fechas.horaValida("9:00") shouldBe false
        Fechas.horaValida("09:6a") shouldBe false
        Fechas.horaValida("23:59") shouldBe true
        Fechas.horaValida("09:30") shouldBe true
    }

    "un evento es próximo si ocurre dentro de los siguientes 7 días" {
        val hoy = LocalDate.now()

        Fechas.proxima(hoy.toString()) shouldBe true
        Fechas.proxima(hoy.plusDays(7).toString()) shouldBe true
        Fechas.proxima(hoy.plusDays(8).toString()) shouldBe false
        Fechas.proxima(hoy.minusDays(1).toString()) shouldBe false
        Fechas.proxima("no es fecha") shouldBe false
    }
})
