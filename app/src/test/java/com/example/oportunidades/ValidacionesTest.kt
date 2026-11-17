package com.example.oportunidades

import com.example.oportunidades.model.Fechas
import com.example.oportunidades.viewmodel.UsuarioViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesTest {

    @Test
    fun formularioVacioMuestraErrores() {
        val viewModel = UsuarioViewModel()

        assertFalse(viewModel.validarFormulario())
        assertNotNull(viewModel.estado.value.errores.correo)
        assertNotNull(viewModel.estado.value.errores.clave)
    }

    @Test
    fun escribirEnUnCampoLimpiaSuError() {
        val viewModel = UsuarioViewModel()
        viewModel.validarFormulario()

        viewModel.onCorreoChange("ejecutivo@example.com")

        assertNull(viewModel.estado.value.errores.correo)
        assertNotNull(viewModel.estado.value.errores.clave)
    }

    @Test
    fun accesoDeDemostracionYCierreDeSesion() {
        val viewModel = UsuarioViewModel()
        viewModel.onCorreoChange("ejecutivo@example.com")
        viewModel.onClaveChange("Incorrecta")
        assertFalse(viewModel.validarFormulario())

        viewModel.onClaveChange("Demo2026")
        assertTrue(viewModel.validarFormulario())
        assertTrue(viewModel.estado.value.activo)
        assertEquals("", viewModel.estado.value.clave)

        viewModel.salir()
        assertFalse(viewModel.estado.value.activo)
        assertEquals("", viewModel.estado.value.correo)
    }

    @Test
    fun fechaImposibleYHoraFueraDeRangoSonRechazadas() {
        assertFalse(Fechas.valida("2026-02-30"))
        assertFalse(Fechas.horaValida("25:00"))
        assertTrue(Fechas.valida("2028-02-29"))
        assertTrue(Fechas.horaValida("23:59"))
    }
}
