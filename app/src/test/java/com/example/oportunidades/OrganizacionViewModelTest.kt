package com.example.oportunidades

import com.example.oportunidades.model.data.local.OrganizacionEntity
import com.example.oportunidades.repository.OrganizacionRepository
import com.example.oportunidades.viewmodel.OrganizacionViewModel
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
class OrganizacionViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    val camara = OrganizacionEntity(
        id = 1,
        nombre = "Cámara de Comercio Demo",
        tipo = "Asociación",
        rubro = "Comercio",
        comuna = "Maipú",
        region = "Metropolitana",
        sitioWeb = "https://example.com",
        publico = "Empresas",
        observaciones = "Ficticia"
    )

    fun repositorio(): OrganizacionRepository {
        val fakeRepository = mockk<OrganizacionRepository>()
        every { fakeRepository.obtenerTodos() } returns flowOf(listOf(camara))
        return fakeRepository
    }

    "al iniciar carga las organizaciones guardadas" {
        runTest(dispatcher) {
            val viewModel = OrganizacionViewModel(repositorio())
            advanceUntilIdle()

            viewModel.registros.value shouldContainExactly listOf(camara)
        }
    }

    "nombre entrega el nombre de la organización o un texto por defecto" {
        runTest(dispatcher) {
            val viewModel = OrganizacionViewModel(repositorio())
            advanceUntilIdle()

            viewModel.nombre(listOf(camara), "1") shouldBe "Cámara de Comercio Demo"
            viewModel.nombre(listOf(camara), "9") shouldBe "Sin organización"
        }
    }

    "cargar y nuevo cambian el formulario" {
        runTest(dispatcher) {
            val viewModel = OrganizacionViewModel(repositorio())
            advanceUntilIdle()

            viewModel.cargar(camara)
            viewModel.estado.value.id shouldBe 1L
            viewModel.estado.value.sitioWeb shouldBe "https://example.com"

            viewModel.nuevo()
            viewModel.estado.value.id shouldBe 0L
            viewModel.estado.value.nombre shouldBe ""
        }
    }

    "el formulario vacío marca los campos obligatorios y cada campo limpia su error" {
        runTest(dispatcher) {
            val viewModel = OrganizacionViewModel(repositorio())
            advanceUntilIdle()

            viewModel.validarFormulario() shouldBe false
            viewModel.estado.value.errores.nombre shouldBe "Campo obligatorio"
            viewModel.estado.value.errores.tipo shouldBe "Campo obligatorio"
            viewModel.estado.value.errores.comuna shouldBe "Campo obligatorio"
            viewModel.estado.value.errores.region shouldBe "Campo obligatorio"

            viewModel.onNombreChange("Empresa Demo")
            viewModel.onTipoChange("Empresa")
            viewModel.onRubroChange("Logística")
            viewModel.onComunaChange("La Florida")
            viewModel.onRegionChange("Metropolitana")
            viewModel.onSitioWebChange("https://example.org")
            viewModel.onPublicoChange("Colaboradores")
            viewModel.onObservacionesChange("Sin observaciones")

            val estado = viewModel.estado.value
            estado.nombre shouldBe "Empresa Demo"
            estado.tipo shouldBe "Empresa"
            estado.rubro shouldBe "Logística"
            estado.comuna shouldBe "La Florida"
            estado.region shouldBe "Metropolitana"
            estado.sitioWeb shouldBe "https://example.org"
            estado.publico shouldBe "Colaboradores"
            estado.observaciones shouldBe "Sin observaciones"
            estado.errores.nombre.shouldBeNull()
            estado.errores.region.shouldBeNull()
            viewModel.validarFormulario() shouldBe true
        }
    }

    "guardar envía la organización al repositorio y queda con su id" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            coEvery { fakeRepository.guardar(any()) } returns 4L

            val viewModel = OrganizacionViewModel(fakeRepository)
            advanceUntilIdle()
            viewModel.onNombreChange("Empresa Demo")
            var listo = false

            viewModel.guardar { listo = true }
            advanceUntilIdle()

            listo shouldBe true
            viewModel.estado.value.id shouldBe 4L
            coVerify { fakeRepository.guardar(match { it.nombre == "Empresa Demo" && it.id == 0L }) }
        }
    }

    "buscarOCrear usa la organización existente sin importar mayúsculas" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()

            val viewModel = OrganizacionViewModel(fakeRepository)
            advanceUntilIdle()
            var id = ""

            viewModel.buscarOCrear(" cámara de comercio demo ", "Maipú", "Metropolitana") { id = it }
            advanceUntilIdle()

            id shouldBe "1"
            coVerify(exactly = 0) { fakeRepository.guardar(any()) }
        }
    }

    "buscarOCrear registra la organización si no existe" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            coEvery { fakeRepository.guardar(any()) } returns 8L

            val viewModel = OrganizacionViewModel(fakeRepository)
            advanceUntilIdle()
            var id = ""

            viewModel.buscarOCrear(" Municipalidad Demo ", "Puente Alto", "Metropolitana") { id = it }
            advanceUntilIdle()

            id shouldBe "8"
            coVerify {
                fakeRepository.guardar(match {
                    it.nombre == "Municipalidad Demo" &&
                            it.comuna == "Puente Alto" &&
                            it.region == "Metropolitana" &&
                            it.tipo == "Otra entidad"
                })
            }
        }
    }
})
