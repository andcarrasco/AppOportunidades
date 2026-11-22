package com.example.oportunidades

import com.example.oportunidades.model.Fechas
import com.example.oportunidades.model.data.local.ActivacionEntity
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.repository.ActivacionRepository
import com.example.oportunidades.repository.OportunidadRepository
import com.example.oportunidades.viewmodel.ActivacionViewModel
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
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ActivacionViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    val activacion = ActivacionEntity(
        id = 3,
        oportunidadId = "1",
        confirmada = true,
        responsable = "Valentina Demo",
        fecha = "2026-12-15",
        condiciones = "Stand 3x3",
        observaciones = "Llevar pendón"
    )

    fun repositorio(): ActivacionRepository {
        val fakeRepository = mockk<ActivacionRepository>()
        every { fakeRepository.obtenerTodos() } returns flowOf(listOf(activacion))
        return fakeRepository
    }

    "al iniciar carga las activaciones y busca la de una oportunidad" {
        runTest(dispatcher) {
            val viewModel = ActivacionViewModel(repositorio(), mockk())
            advanceUntilIdle()

            viewModel.registros.value shouldContainExactly listOf(activacion)
            viewModel.deOportunidad(viewModel.registros.value, 1L) shouldBe activacion
            viewModel.deOportunidad(viewModel.registros.value, 2L).shouldBeNull()
        }
    }

    "abrir edita la activación registrada o prepara una nueva con el responsable" {
        runTest(dispatcher) {
            val viewModel = ActivacionViewModel(repositorio(), mockk())
            advanceUntilIdle()

            viewModel.abrir(1L, "Otra persona")
            viewModel.estado.value.id shouldBe 3L
            viewModel.estado.value.confirmada shouldBe true
            viewModel.estado.value.responsable shouldBe "Valentina Demo"

            viewModel.abrir(2L, "Valentina Demo")
            viewModel.estado.value.id shouldBe 0L
            viewModel.estado.value.oportunidadId shouldBe "2"
            viewModel.estado.value.confirmada shouldBe false
            viewModel.estado.value.responsable shouldBe "Valentina Demo"
            viewModel.estado.value.fecha shouldBe Fechas.hoy()
        }
    }

    "el formulario vacío marca los campos obligatorios" {
        runTest(dispatcher) {
            val viewModel = ActivacionViewModel(repositorio(), mockk())
            advanceUntilIdle()
            viewModel.nuevo()
            viewModel.onFechaChange("15-12-2026")

            viewModel.validarFormulario() shouldBe false
            val errores = viewModel.estado.value.errores
            errores.oportunidadId shouldBe "Campo obligatorio"
            errores.responsable shouldBe "Asigna un responsable"
            errores.fecha shouldBe "Usa AAAA-MM-DD y una fecha válida"
            errores.condiciones shouldBe "Registra las condiciones de participación"
        }
    }

    "cada campo se actualiza, limpia su error y la activación queda válida" {
        runTest(dispatcher) {
            val viewModel = ActivacionViewModel(repositorio(), mockk())
            advanceUntilIdle()
            viewModel.nuevo("1")
            viewModel.validarFormulario()

            viewModel.onConfirmadaChange(true)
            viewModel.onResponsableChange("Carla Demo")
            viewModel.onFechaChange("2026-12-20")
            viewModel.onCondicionesChange("Mesa y dos sillas")
            viewModel.onObservacionesChange("Confirmado por correo")

            val estado = viewModel.estado.value
            estado.confirmada shouldBe true
            estado.responsable shouldBe "Carla Demo"
            estado.fecha shouldBe "2026-12-20"
            estado.condiciones shouldBe "Mesa y dos sillas"
            estado.observaciones shouldBe "Confirmado por correo"
            estado.errores.responsable.shouldBeNull()
            estado.errores.condiciones.shouldBeNull()
            viewModel.validarFormulario() shouldBe true
        }
    }

    val propuesta = OportunidadEntity(id = 1, nombre = "Feria Demo", estado = "Propuesta enviada")

    "guardar envía la activación y confirma la participación en el microservicio" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            val oportunidadRepository = mockk<OportunidadRepository>()
            coEvery { fakeRepository.guardar(any()) } returns 3L
            coEvery { oportunidadRepository.cambiarEstado(propuesta, "Participación confirmada") } returns
                    propuesta.copy(estado = "Participación confirmada")

            val viewModel = ActivacionViewModel(fakeRepository, oportunidadRepository)
            advanceUntilIdle()
            viewModel.cargar(activacion)
            var listo = false

            viewModel.guardar(propuesta) { listo = true }
            advanceUntilIdle()

            listo shouldBe true
            viewModel.estado.value.id shouldBe 3L
            viewModel.mensaje.value shouldBe ""
            coVerify { fakeRepository.guardar(activacion) }
            coVerify { oportunidadRepository.cambiarEstado(propuesta, "Participación confirmada") }
        }
    }

    "si la participación ya estaba confirmada o no se confirma no cambia el estado" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            val oportunidadRepository = mockk<OportunidadRepository>()
            coEvery { fakeRepository.guardar(any()) } returns 3L

            val viewModel = ActivacionViewModel(fakeRepository, oportunidadRepository)
            advanceUntilIdle()
            var listo = 0

            viewModel.cargar(activacion)
            viewModel.guardar(propuesta.copy(estado = "Participación confirmada")) { listo++ }
            advanceUntilIdle()

            viewModel.cargar(activacion.copy(confirmada = false))
            viewModel.guardar(propuesta) { listo++ }
            advanceUntilIdle()

            viewModel.cargar(activacion)
            viewModel.guardar(null) { listo++ }
            advanceUntilIdle()

            listo shouldBe 3
            coVerify(exactly = 0) { oportunidadRepository.cambiarEstado(any(), any()) }
        }
    }

    "si el microservicio no responde se muestra un mensaje y no se continúa" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            val oportunidadRepository = mockk<OportunidadRepository>()
            coEvery { fakeRepository.guardar(any()) } returns 3L
            coEvery { oportunidadRepository.cambiarEstado(any(), any()) } throws IOException()

            val viewModel = ActivacionViewModel(fakeRepository, oportunidadRepository)
            advanceUntilIdle()
            viewModel.cargar(activacion)
            var listo = false

            viewModel.guardar(propuesta) { listo = true }
            advanceUntilIdle()

            listo shouldBe false
            viewModel.mensaje.value shouldBe "No se pudo guardar: revisa que el microservicio esté activo"
            coVerify(exactly = 0) { fakeRepository.guardar(any()) }
        }
    }

    "si falla el guardado en Room se muestra un mensaje y la app no se cae" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            val oportunidadRepository = mockk<OportunidadRepository>()
            coEvery { fakeRepository.guardar(any()) } throws IllegalStateException()

            val viewModel = ActivacionViewModel(fakeRepository, oportunidadRepository)
            advanceUntilIdle()
            viewModel.cargar(activacion.copy(confirmada = false))
            var listo = false

            viewModel.guardar(propuesta) { listo = true }
            advanceUntilIdle()

            listo shouldBe false
            viewModel.mensaje.value shouldBe "No se pudo guardar: revisa que el microservicio esté activo"
        }
    }
})
