package com.example.oportunidades

import com.example.oportunidades.model.FiltrosUiState
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.model.data.local.ResultadoEntity
import com.example.oportunidades.model.data.local.SeguimientoEntity
import com.example.oportunidades.repository.OportunidadRepository
import com.example.oportunidades.repository.ResultadoRepository
import com.example.oportunidades.repository.SeguimientoRepository
import com.example.oportunidades.viewmodel.GestionViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class GestionViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    val feria = OportunidadEntity(
        id = 1,
        nombre = "Feria Demo",
        tipo = "Feria empresarial",
        estado = "Contacto pendiente",
        fechaInicio = "2026-12-15",
        comuna = "Maipú",
        region = "Metropolitana",
        prioridad = "Alta",
        responsable = "Carla Demo",
        organizacionId = "1"
    )
    val jornada = OportunidadEntity(
        id = 2,
        nombre = "Jornada de Bienestar",
        tipo = "Jornada de beneficios",
        estado = "Propuesta enviada",
        fechaInicio = "2027-01-12",
        comuna = "La Florida",
        region = "Metropolitana",
        prioridad = "Media",
        responsable = "Rodrigo Demo",
        organizacionId = "2"
    )

    fun crearViewModel(
        oportunidades: List<OportunidadEntity> = listOf(feria, jornada),
        seguimientos: List<SeguimientoEntity> = emptyList(),
        resultados: List<ResultadoEntity> = emptyList()
    ): GestionViewModel {
        val oportunidadRepository = mockk<OportunidadRepository>()
        val seguimientoRepository = mockk<SeguimientoRepository>()
        val resultadoRepository = mockk<ResultadoRepository>()
        every { oportunidadRepository.obtenerTodos() } returns flowOf(oportunidades)
        every { seguimientoRepository.obtenerTodos() } returns flowOf(seguimientos)
        every { resultadoRepository.obtenerTodos() } returns flowOf(resultados)
        return GestionViewModel(oportunidadRepository, seguimientoRepository, resultadoRepository)
    }

    "al iniciar carga oportunidades, seguimientos y resultados" {
        runTest(dispatcher) {
            val gestion = SeguimientoEntity(id = 1, oportunidadId = "1", tipo = "Llamada")
            val resultado = ResultadoEntity(id = 1, oportunidadId = "2")

            val viewModel = crearViewModel(seguimientos = listOf(gestion), resultados = listOf(resultado))
            viewModel.cargando.value shouldBe true
            advanceUntilIdle()

            viewModel.oportunidades.value shouldContainExactly listOf(feria, jornada)
            viewModel.seguimientos.value shouldContainExactly listOf(gestion)
            viewModel.resultados.value shouldContainExactly listOf(resultado)
            viewModel.cargando.value shouldBe false
        }
    }

    "los filtros validan la fecha y se pueden limpiar" {
        runTest(dispatcher) {
            val viewModel = crearViewModel()

            viewModel.onFiltrosChange(FiltrosUiState(fecha = "2026-13-01"))
            viewModel.filtros.value.errores.fecha shouldBe "Usa una fecha válida AAAA-MM-DD"

            viewModel.onFiltrosChange(FiltrosUiState(fecha = "2026-12-15", nombre = "feria"))
            viewModel.filtros.value.errores.fecha.shouldBeNull()
            viewModel.filtros.value.nombre shouldBe "feria"

            viewModel.limpiarFiltros()
            viewModel.filtros.value shouldBe FiltrosUiState()
        }
    }

    "filtrar busca por nombre, fecha, estado, tipo, ubicación, prioridad, responsable y organización" {
        runTest(dispatcher) {
            val viewModel = crearViewModel()
            val lista = listOf(feria, jornada)

            viewModel.filtrar(lista, FiltrosUiState()) shouldContainExactly lista
            viewModel.filtrar(lista, FiltrosUiState(nombre = "FERIA")) shouldContainExactly listOf(feria)
            viewModel.filtrar(lista, FiltrosUiState(fecha = "2027-01-12")) shouldContainExactly listOf(jornada)
            viewModel.filtrar(lista, FiltrosUiState(estado = "Propuesta enviada")) shouldContainExactly listOf(jornada)
            viewModel.filtrar(lista, FiltrosUiState(tipo = "Feria empresarial")) shouldContainExactly listOf(feria)
            viewModel.filtrar(lista, FiltrosUiState(comuna = "florida")) shouldContainExactly listOf(jornada)
            viewModel.filtrar(lista, FiltrosUiState(region = "Metro")) shouldContainExactly lista
            viewModel.filtrar(lista, FiltrosUiState(prioridad = "Alta")) shouldContainExactly listOf(feria)
            viewModel.filtrar(lista, FiltrosUiState(responsable = "rodrigo")) shouldContainExactly listOf(jornada)
            viewModel.filtrar(lista, FiltrosUiState(organizacionId = "1")) shouldContainExactly listOf(feria)
            viewModel.filtrar(lista, FiltrosUiState(nombre = "No existe")).shouldBeEmpty()
        }
    }

    "avisos muestra eventos próximos, contactos pendientes, propuestas sin respuesta y gestiones" {
        runTest(dispatcher) {
            val viewModel = crearViewModel()
            val manana = LocalDate.now().plusDays(1).toString()
            val ayer = LocalDate.now().minusDays(1).toString()
            val proximo = feria.copy(fechaInicio = manana)
            val gestiones = listOf(
                SeguimientoEntity(
                    id = 1,
                    oportunidadId = "2",
                    tipo = "Envío de propuesta",
                    fecha = "2026-11-01",
                    hora = "10:00",
                    proximaAccion = "Llamar",
                    proximaFecha = ayer
                )
            )

            val avisos = viewModel.avisos(listOf(proximo, jornada), gestiones, emptyList())
            val textos = avisos.map { it.second }

            textos shouldContain "Evento próximo: $manana"
            textos shouldContain "Contacto pendiente"
            textos shouldContain "Sin acciones de seguimiento"
            textos shouldContain "Propuesta sin respuesta"
            textos shouldContain "Seguimiento atrasado: $ayer · Llamar"
        }
    }

    "avisos distingue seguimientos pendientes y omite oportunidades cerradas" {
        runTest(dispatcher) {
            val viewModel = crearViewModel()
            val futura = LocalDate.now().plusDays(10).toString()
            val gestiones = listOf(
                SeguimientoEntity(
                    id = 1,
                    oportunidadId = "1",
                    tipo = "Llamada",
                    fecha = "2026-11-01",
                    hora = "10:00",
                    proximaAccion = "Reunión",
                    proximaFecha = futura
                )
            )
            val descartada = jornada.copy(estado = "Oportunidad descartada")
            val conResultado = jornada.copy(id = 5)
            val cierres = listOf(ResultadoEntity(id = 1, oportunidadId = "5"))

            val lejana = feria.copy(fechaInicio = "2030-01-01")

            val avisos = viewModel.avisos(listOf(lejana, descartada, conResultado), gestiones, cierres)

            avisos.map { it.second } shouldContainExactly listOf(
                "Contacto pendiente",
                "Seguimiento pendiente: $futura · Reunión"
            )
            avisos.all { it.first == lejana } shouldBe true
        }
    }

    "la última gestión ignora cambios de estado, otras oportunidades y gestiones antiguas" {
        val reciente = SeguimientoEntity(
            id = 1,
            oportunidadId = "1",
            tipo = "Seguimiento",
            fecha = "2026-10-25",
            hora = "09:00"
        )
        val antigua = reciente.copy(id = 2, fecha = "2026-10-20", hora = "18:00")
        val cambioEstado = reciente.copy(id = 3, tipo = "Cambio de estado", hora = "19:00")
        val otraOportunidad = reciente.copy(id = 4, oportunidadId = "2", hora = "20:00")

        GestionViewModel.ultimaGestion(
            "1",
            listOf(otraOportunidad, cambioEstado, antigua, reciente)
        ) shouldBe reciente
    }

    "con la misma fecha la última gestión se decide por hora y luego por id" {
        val primera = SeguimientoEntity(
            id = 8,
            oportunidadId = "1",
            tipo = "Seguimiento",
            fecha = "2026-10-25",
            hora = "09:00"
        )
        val segunda = primera.copy(id = 2, hora = "10:00")
        val tercera = segunda.copy(id = 3)

        GestionViewModel.ultimaGestion("1", listOf(tercera, primera, segunda)) shouldBe tercera
        GestionViewModel.ultimaGestion("2", listOf(primera, segunda, tercera)).shouldBeNull()
        GestionViewModel.ultimaGestion("1", emptyList()).shouldBeNull()
    }
})
