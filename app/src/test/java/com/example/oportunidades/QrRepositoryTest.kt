package com.example.oportunidades

import com.example.oportunidades.model.QrResult
import com.example.oportunidades.repository.QrRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe

class QrRepositoryTest : StringSpec({

    val repository = QrRepository()

    "processQrContent entrega el contenido leído" {
        repository.processQrContent("Nombre: Feria Demo") shouldBe QrResult("Nombre: Feria Demo")
    }

    "lee una línea por campo aunque tenga tildes o mayúsculas" {
        val campos = repository.leerCampos(
            """
            Nombre: Feria Demo
            Tipo de evento: Feria empresarial
            Región = Metropolitana
            Fecha inicio: 2026-12-01
            Hora: 09:30
            Organización: Empresa Demo
            Dato desconocido: se ignora
            """.trimIndent()
        )

        campos["nombre"] shouldBe "Feria Demo"
        campos["tipo"] shouldBe "Feria empresarial"
        campos["region"] shouldBe "Metropolitana"
        campos["fechaInicio"] shouldBe "2026-12-01"
        campos["horario"] shouldBe "09:30"
        campos["organizacion"] shouldBe "Empresa Demo"
        campos.size shouldBe 6
    }

    "un texto sin campos se usa como descripción y uno vacío no trae datos" {
        repository.leerCampos("https://example.com/evento") shouldBe
                mapOf("descripcion" to "https://example.com/evento")
        repository.leerCampos("   ").shouldBeEmpty()
    }

    "los campos sin valor se ignoran" {
        val campos = repository.leerCampos("Nombre: Feria\nComuna:   \nCosto=1000")

        campos shouldBe mapOf("nombre" to "Feria", "costos" to "1000")
    }
})
