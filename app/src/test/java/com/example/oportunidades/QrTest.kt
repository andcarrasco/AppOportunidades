package com.example.oportunidades

import com.example.oportunidades.viewmodel.QrViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrTest {
    @Test
    fun leeUnaLineaPorCampoAunqueTengaTildesOMayusculas() {
        val campos =
            QrViewModel.leerCampos(
                """
                Nombre: Feria Demo
                Tipo de evento: Feria empresarial
                Región = Metropolitana
                Fecha inicio: 2026-12-01
                Hora: 09:30
                Organización: Empresa Demo
                Dato desconocido: se ignora
                """
                    .trimIndent()
            )

        assertEquals("Feria Demo", campos["nombre"])
        assertEquals("Feria empresarial", campos["tipo"])
        assertEquals("Metropolitana", campos["region"])
        assertEquals("2026-12-01", campos["fechaInicio"])
        assertEquals("09:30", campos["horario"])
        assertEquals("Empresa Demo", campos["organizacion"])
        assertEquals(6, campos.size)
    }

    @Test
    fun unTextoSinCamposSeUsaComoDescripcion() {
        assertEquals(
            mapOf("descripcion" to "https://example.com/evento"),
            QrViewModel.leerCampos("https://example.com/evento"),
        )
        assertTrue(QrViewModel.leerCampos("   ").isEmpty())
    }
}
