package com.example.oportunidades

import com.example.oportunidades.model.Fechas
import com.example.oportunidades.model.data.local.SeguimientoEntity
import com.example.oportunidades.repository.SeguimientoRepository
import com.example.oportunidades.viewmodel.SeguimientoViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
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

@OptIn(ExperimentalCoroutinesApi::class)
class SeguimientoViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    val llamada = SeguimientoEntity(
        id = 4,
        oportunidadId = "1",
        responsable = "Carla Demo",
        tipo = "Llamada",
        fecha = "2026-11-20",
        hora = "10:00",
        resultado = "Interesados",
        proximaAccion = "Enviar propuesta",
        proximaFecha = "2026-11-25",
        observaciones = "Ficticio"
    )
    val otra = llamada.copy(id = 5, oportunidadId = "2")

    fun repositorio(): SeguimientoRepository {
        val fakeRepository = mockk<SeguimientoRepository>()
        every { fakeRepository.obtenerTodos() } returns flowOf(listOf(llamada, otra))
        return fakeRepository
    }

    "al iniciar carga las gestiones y las filtra por oportunidad" {
        runTest(dispatcher) {
            val viewModel = SeguimientoViewModel(repositorio())
            advanceUntilIdle()

            viewModel.registros.value shouldContainExactly listOf(llamada, otra)
            viewModel.deOportunidad(viewModel.registros.value, 1L) shouldContainExactly listOf(llamada)
        }
    }

    "nuevo usa la oportunidad, el responsable de la sesión y la fecha de hoy" {
        runTest(dispatcher) {
            val viewModel = SeguimientoViewModel(repositorio())
            advanceUntilIdle()

            viewModel.nuevo("1", "Rodrigo Demo")

            viewModel.estado.value.oportunidadId shouldBe "1"
            viewModel.estado.value.responsable shouldBe "Rodrigo Demo"
            viewModel.estado.value.fecha shouldBe Fechas.hoy()
            viewModel.estado.value.hora shouldBe "09:00"
        }
    }

    "cargar copia la gestión y pasa la validación" {
        runTest(dispatcher) {
            val viewModel = SeguimientoViewModel(repositorio())
            advanceUntilIdle()

            viewModel.cargar(llamada)

            viewModel.estado.value.id shouldBe 4L
            viewModel.estado.value.proximaAccion shouldBe "Enviar propuesta"
            viewModel.validarFormulario() shouldBe true
        }
    }

    "el formulario vacío marca los campos obligatorios" {
        runTest(dispatcher) {
            val viewModel = SeguimientoViewModel(repositorio())
            advanceUntilIdle()

            viewModel.validarFormulario() shouldBe false
            val errores = viewModel.estado.value.errores
            errores.oportunidadId shouldBe "Campo obligatorio"
            errores.responsable shouldBe "Campo obligatorio"
            errores.tipo shouldBe "Campo obligatorio"
            errores.fecha shouldBe "Usa AAAA-MM-DD y una fecha válida"
            errores.hora shouldBe "Usa HH:mm y una hora válida"
            errores.resultado shouldBe "Campo obligatorio"
            errores.proximaAccion.shouldBeNull()
            errores.proximaFecha.shouldBeNull()
        }
    }

    "la próxima acción y su fecha se validan juntas" {
        runTest(dispatcher) {
            val viewModel = SeguimientoViewModel(repositorio())
            advanceUntilIdle()

            viewModel.cargar(llamada.copy(proximaAccion = "", proximaFecha = "2026-11-25"))
            viewModel.validarFormulario() shouldBe false
            viewModel.estado.value.errores.proximaAccion shouldBe "Describe la próxima acción"

            viewModel.cargar(llamada.copy(proximaFecha = ""))
            viewModel.validarFormulario() shouldBe false
            viewModel.estado.value.errores.proximaFecha shouldBe "Indica una fecha válida"

            viewModel.cargar(llamada.copy(proximaAccion = "", proximaFecha = "2026-99-99"))
            viewModel.validarFormulario() shouldBe false
            viewModel.estado.value.errores.proximaFecha shouldBe "Usa AAAA-MM-DD"

            viewModel.cargar(llamada.copy(proximaFecha = "2026-11-01"))
            viewModel.validarFormulario() shouldBe false
            viewModel.estado.value.errores.proximaFecha shouldBe "Debe ser igual o posterior a la gestión"
        }
    }

    "cada campo se actualiza y limpia su error" {
        runTest(dispatcher) {
            val viewModel = SeguimientoViewModel(repositorio())
            advanceUntilIdle()
            viewModel.nuevo("1", "")
            viewModel.onHoraChange("")
            viewModel.validarFormulario()

            viewModel.onResponsableChange("Carla Demo")
            viewModel.onTipoChange("Reunión")
            viewModel.onFechaChange("2026-11-20")
            viewModel.onHoraChange("11:30")
            viewModel.onResultadoChange("Acordaron reunión")
            viewModel.onProximaAccionChange("Enviar presentación")
            viewModel.onProximaFechaChange("2026-11-22")
            viewModel.onObservacionesChange("Sin observaciones")

            val estado = viewModel.estado.value
            estado.responsable shouldBe "Carla Demo"
            estado.tipo shouldBe "Reunión"
            estado.fecha shouldBe "2026-11-20"
            estado.hora shouldBe "11:30"
            estado.resultado shouldBe "Acordaron reunión"
            estado.proximaAccion shouldBe "Enviar presentación"
            estado.proximaFecha shouldBe "2026-11-22"
            estado.observaciones shouldBe "Sin observaciones"
            estado.errores.responsable.shouldBeNull()
            estado.errores.hora.shouldBeNull()
            viewModel.validarFormulario() shouldBe true
        }
    }

    "guardar envía la gestión al repositorio y queda con su id" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            coEvery { fakeRepository.guardar(any()) } returns 9L

            val viewModel = SeguimientoViewModel(fakeRepository)
            advanceUntilIdle()
            viewModel.cargar(llamada.copy(id = 0))
            var listo = false

            viewModel.guardar { listo = true }
            advanceUntilIdle()

            listo shouldBe true
            viewModel.estado.value.id shouldBe 9L
            coVerify { fakeRepository.guardar(llamada.copy(id = 0)) }
        }
    }
})
