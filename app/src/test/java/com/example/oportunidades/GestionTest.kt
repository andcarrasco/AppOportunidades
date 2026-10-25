package com.example.oportunidades

import com.example.oportunidades.model.data.local.SeguimientoEntity
import com.example.oportunidades.viewmodel.GestionViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GestionTest {
    @Test
    fun registrarDespuesUnaGestionAntiguaNoCambiaLaUltimaGestion() {
        val reciente =
            SeguimientoEntity(
                id = 1,
                oportunidadId = "1",
                tipo = "Seguimiento",
                fecha = "2026-10-25",
                hora = "09:00",
            )
        val antigua = reciente.copy(id = 2, fecha = "2026-10-20", hora = "18:00")
        val cambioEstado = reciente.copy(id = 3, tipo = "Cambio de estado", hora = "19:00")
        val otraOportunidad = reciente.copy(id = 4, oportunidadId = "2", hora = "20:00")

        val ultima =
            GestionViewModel.ultimaGestion(
                "1",
                listOf(otraOportunidad, cambioEstado, antigua, reciente),
            )

        assertEquals(reciente, ultima)
    }

    @Test
    fun conLaMismaFechaSeComparaHoraYLuegoId() {
        val primera =
            SeguimientoEntity(
                id = 8,
                oportunidadId = "1",
                tipo = "Seguimiento",
                fecha = "2026-10-25",
                hora = "09:00",
            )
        val segunda = primera.copy(id = 2, hora = "10:00")
        val tercera = segunda.copy(id = 3)

        assertEquals(
            tercera,
            GestionViewModel.ultimaGestion("1", listOf(tercera, primera, segunda)),
        )
        assertNull(GestionViewModel.ultimaGestion("2", listOf(primera, segunda, tercera)))
        assertNull(GestionViewModel.ultimaGestion("1", emptyList()))
    }
}
