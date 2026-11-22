package com.example.oportunidades

import com.example.oportunidades.model.Fechas
import com.example.oportunidades.model.data.local.ResultadoEntity
import com.example.oportunidades.repository.ResultadoRepository
import com.example.oportunidades.viewmodel.ResultadoViewModel
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
class ResultadoViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    val resultado = ResultadoEntity(
        id = 6,
        oportunidadId = "1",
        fecha = "2026-12-16",
        estado = "Activación realizada",
        asistentes = "1200",
        contactos = "80",
        interesados = "40",
        inscripciones = "15",
        observaciones = "Buena convocatoria",
        proximasAcciones = "Repetir el próximo año"
    )

    fun repositorio(): ResultadoRepository {
        val fakeRepository = mockk<ResultadoRepository>()
        every { fakeRepository.obtenerTodos() } returns flowOf(listOf(resultado))
        return fakeRepository
    }

    "al iniciar carga los resultados y los filtra por oportunidad" {
        runTest(dispatcher) {
            val viewModel = ResultadoViewModel(repositorio())
            advanceUntilIdle()

            viewModel.registros.value shouldContainExactly listOf(resultado)
            viewModel.deOportunidad(viewModel.registros.value, 1L) shouldContainExactly listOf(resultado)
            viewModel.deOportunidad(viewModel.registros.value, 2L) shouldBe emptyList()
        }
    }

    "abrir edita el resultado registrado o prepara uno nuevo" {
        runTest(dispatcher) {
            val viewModel = ResultadoViewModel(repositorio())
            advanceUntilIdle()

            viewModel.abrir(1L)
            viewModel.estado.value.id shouldBe 6L
            viewModel.estado.value.proximasAcciones shouldBe "Repetir el próximo año"

            viewModel.abrir(2L)
            viewModel.estado.value.id shouldBe 0L
            viewModel.estado.value.oportunidadId shouldBe "2"
            viewModel.estado.value.fecha shouldBe Fechas.hoy()
            viewModel.estado.value.estado shouldBe "Activación realizada"
        }
    }

    "el formulario vacío marca los campos obligatorios" {
        runTest(dispatcher) {
            val viewModel = ResultadoViewModel(repositorio())
            advanceUntilIdle()

            viewModel.validarFormulario() shouldBe false
            val errores = viewModel.estado.value.errores
            errores.oportunidadId shouldBe "Campo obligatorio"
            errores.fecha shouldBe "Usa AAAA-MM-DD y una fecha válida"
            errores.estado shouldBe "Campo obligatorio"
            errores.observaciones shouldBe "Campo obligatorio"
            errores.asistentes.shouldBeNull()
        }
    }

    "las cantidades deben ser números enteros no negativos" {
        runTest(dispatcher) {
            val viewModel = ResultadoViewModel(repositorio())
            advanceUntilIdle()

            viewModel.cargar(
                resultado.copy(asistentes = "-1", contactos = "diez", interesados = "1.5", inscripciones = "-3")
            )

            viewModel.validarFormulario() shouldBe false
            val errores = viewModel.estado.value.errores
            errores.asistentes shouldBe "Ingresa un número entero no negativo"
            errores.contactos shouldBe "Ingresa un número entero no negativo"
            errores.interesados shouldBe "Ingresa un número entero no negativo"
            errores.inscripciones shouldBe "Ingresa un número entero no negativo"
        }
    }

    "cada campo se actualiza, limpia su error y el resultado queda válido" {
        runTest(dispatcher) {
            val viewModel = ResultadoViewModel(repositorio())
            advanceUntilIdle()
            viewModel.nuevo("1")
            viewModel.onAsistentesChange("-1")
            viewModel.validarFormulario()

            viewModel.onFechaChange("2026-12-16")
            viewModel.onEstadoChange("Activación cancelada")
            viewModel.onAsistentesChange("0")
            viewModel.onContactosChange("0")
            viewModel.onInteresadosChange("0")
            viewModel.onInscripcionesChange("0")
            viewModel.onObservacionesChange("Se suspendió por lluvia")
            viewModel.onProximasAccionesChange("Reagendar")

            val estado = viewModel.estado.value
            estado.fecha shouldBe "2026-12-16"
            estado.estado shouldBe "Activación cancelada"
            estado.asistentes shouldBe "0"
            estado.contactos shouldBe "0"
            estado.interesados shouldBe "0"
            estado.inscripciones shouldBe "0"
            estado.observaciones shouldBe "Se suspendió por lluvia"
            estado.proximasAcciones shouldBe "Reagendar"
            estado.errores.asistentes.shouldBeNull()
            estado.errores.observaciones.shouldBeNull()
            viewModel.validarFormulario() shouldBe true
        }
    }

    "guardar envía el resultado con sus próximas acciones" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            coEvery { fakeRepository.guardar(any()) } returns 6L

            val viewModel = ResultadoViewModel(fakeRepository)
            advanceUntilIdle()
            viewModel.cargar(resultado)
            var listo = false

            viewModel.guardar { listo = true }
            advanceUntilIdle()

            listo shouldBe true
            viewModel.estado.value.id shouldBe 6L
            coVerify { fakeRepository.guardar(resultado) }
        }
    }
})
