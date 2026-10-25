package com.example.oportunidades

import com.example.oportunidades.model.*
import com.example.oportunidades.viewmodel.*
import org.junit.Assert.*
import org.junit.Test

class ValidacionesTest {
    private fun oportunidadValida() =
        OportunidadUiState(
            nombre = "Feria Demo",
            tipo = "Feria empresarial",
            fuente = "Registro ficticio",
            responsable = "Ejecutivo Demo",
            prioridad = "Media",
            potencial = "Alto",
            estado = "Evento identificado",
            organizacionId = "1",
            comuna = "Comuna Demo",
            region = "Región Demo",
            publico = "Trabajadores",
            fechaDeteccion = "2026-10-20",
            fechaInicio = "2026-11-10",
            fechaTermino = "2026-11-10",
            horario = "09:30",
            costos = "0",
            asistentes = "100",
        )

    @Test
    fun formularioVacioEntregaErroresEnCamposObligatorios() {
        val e = OportunidadViewModel.validarDatos(OportunidadUiState())
        assertNotNull(e.nombre)
        assertNotNull(e.organizacionId)
        assertNotNull(e.fechaInicio)
    }

    @Test
    fun oportunidadValidaPermiteEventoHistorico() {
        val e =
            OportunidadViewModel.validarDatos(
                oportunidadValida().copy(fechaInicio = "2025-01-01", fechaTermino = "2025-01-02")
            )
        assertNull(e.fechaInicio)
        assertNull(e.fechaTermino)
        assertNull(e.costos)
    }

    @Test
    fun fechaFinalAnteriorAlInicioEsRechazada() {
        assertNotNull(
            OportunidadViewModel.validarDatos(oportunidadValida().copy(fechaTermino = "2026-11-09"))
                .fechaTermino
        )
    }

    @Test
    fun numerosInvalidosYNoFinitosSonRechazados() {
        val e =
            OportunidadViewModel.validarDatos(
                oportunidadValida().copy(costos = "NaN", asistentes = "-1")
            )
        assertNotNull(e.costos)
        assertNotNull(e.asistentes)
        assertNotNull(
            OportunidadViewModel.validarDatos(oportunidadValida().copy(costos = "abc")).costos
        )
    }

    @Test
    fun fechaImposibleYHoraFueraDeRangoSonRechazadas() {
        assertFalse(Fechas.valida("2026-02-30"))
        assertFalse(Fechas.horaValida("25:00"))
        assertTrue(Fechas.valida("2028-02-29"))
        assertTrue(Fechas.horaValida("23:59"))
    }

    @Test
    fun proximaGestionRequiereDescripcionYFechaCoherentes() {
        val base =
            SeguimientoUiState(
                oportunidadId = "1",
                responsable = "Demo",
                tipo = "Seguimiento",
                fecha = "2026-10-20",
                hora = "09:00",
                resultado = "Sin respuesta",
            )
        assertNotNull(
            SeguimientoViewModel.validarDatos(base.copy(proximaFecha = "2026-10-21")).proximaAccion
        )
        assertNotNull(
            SeguimientoViewModel.validarDatos(
                    base.copy(proximaAccion = "Llamar", proximaFecha = "2026-10-19")
                )
                .proximaFecha
        )
        assertNull(
            SeguimientoViewModel.validarDatos(
                    base.copy(proximaAccion = "Llamar", proximaFecha = "2026-10-21")
                )
                .proximaFecha
        )
    }

    @Test
    fun accesoDeDemostracionYLimpiezaDeSesion() {
        val vm = UsuarioViewModel()
        vm.onCorreoChange("ejecutivo@example.com")
        vm.onClaveChange("Incorrecta")
        assertFalse(vm.ingresar())
        vm.onClaveChange("Demo2026")
        assertTrue(vm.ingresar())
        assertTrue(vm.estado.value.activo)
        assertEquals("", vm.estado.value.clave)
        vm.salir()
        assertFalse(vm.estado.value.activo)
        assertEquals("", vm.estado.value.correo)
    }
}
