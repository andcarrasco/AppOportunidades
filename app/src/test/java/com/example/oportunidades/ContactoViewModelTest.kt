package com.example.oportunidades

import com.example.oportunidades.model.data.local.ContactoEntity
import com.example.oportunidades.repository.ContactoRepository
import com.example.oportunidades.viewmodel.ContactoViewModel
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
class ContactoViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    val contacto = ContactoEntity(
        id = 2,
        organizacionId = "1",
        nombre = "Ana Demo",
        cargo = "Encargada de bienestar",
        area = "Bienestar",
        correo = "ana@example.com",
        telefono = "+56 9 0000 0000",
        medio = "Correo",
        estado = "Pendiente",
        observaciones = "Ficticio"
    )
    val otro = contacto.copy(id = 3, organizacionId = "2", nombre = "Luis Demo")

    fun repositorio(): ContactoRepository {
        val fakeRepository = mockk<ContactoRepository>()
        every { fakeRepository.obtenerTodos() } returns flowOf(listOf(contacto, otro))
        return fakeRepository
    }

    "al iniciar carga los contactos y los filtra por organización" {
        runTest(dispatcher) {
            val viewModel = ContactoViewModel(repositorio())
            advanceUntilIdle()

            viewModel.registros.value shouldContainExactly listOf(contacto, otro)
            viewModel.deOrganizacion(viewModel.registros.value, "2") shouldContainExactly listOf(otro)
        }
    }

    "nuevo prepara un contacto de la organización y cargar copia un registro" {
        runTest(dispatcher) {
            val viewModel = ContactoViewModel(repositorio())
            advanceUntilIdle()

            viewModel.nuevo("5")
            viewModel.estado.value.organizacionId shouldBe "5"
            viewModel.estado.value.id shouldBe 0L

            viewModel.cargar(contacto)
            viewModel.estado.value.id shouldBe 2L
            viewModel.estado.value.cargo shouldBe "Encargada de bienestar"
            viewModel.estado.value.telefono shouldBe "+56 9 0000 0000"
        }
    }

    "el formulario vacío marca errores y cada campo limpia su error" {
        runTest(dispatcher) {
            val viewModel = ContactoViewModel(repositorio())
            advanceUntilIdle()

            viewModel.validarFormulario() shouldBe false
            val errores = viewModel.estado.value.errores
            errores.organizacionId shouldBe "Campo obligatorio"
            errores.nombre shouldBe "Campo obligatorio"
            errores.cargo shouldBe "Campo obligatorio"
            errores.area shouldBe "Campo obligatorio"
            errores.correo shouldBe "Correo inválido"
            errores.telefono shouldBe "Campo obligatorio"
            errores.medio shouldBe "Campo obligatorio"
            errores.estado shouldBe "Campo obligatorio"

            viewModel.onOrganizacionIdChange("1")
            viewModel.onNombreChange("Ana Demo")
            viewModel.onCargoChange("Jefa")
            viewModel.onAreaChange("Eventos")
            viewModel.onCorreoChange("ana@example.com")
            viewModel.onTelefonoChange("123")
            viewModel.onMedioChange("Teléfono")
            viewModel.onEstadoChange("Contactado")
            viewModel.onObservacionesChange("Llamar en la mañana")

            val estado = viewModel.estado.value
            estado.organizacionId shouldBe "1"
            estado.nombre shouldBe "Ana Demo"
            estado.cargo shouldBe "Jefa"
            estado.area shouldBe "Eventos"
            estado.correo shouldBe "ana@example.com"
            estado.telefono shouldBe "123"
            estado.medio shouldBe "Teléfono"
            estado.estado shouldBe "Contactado"
            estado.observaciones shouldBe "Llamar en la mañana"
            estado.errores.correo.shouldBeNull()
            estado.errores.estado.shouldBeNull()
            viewModel.validarFormulario() shouldBe true
        }
    }

    "guardar envía el contacto al repositorio y queda con su id" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            coEvery { fakeRepository.guardar(any()) } returns 2L

            val viewModel = ContactoViewModel(fakeRepository)
            advanceUntilIdle()
            viewModel.cargar(contacto)
            var listo = false

            viewModel.guardar { listo = true }
            advanceUntilIdle()

            listo shouldBe true
            viewModel.estado.value.id shouldBe 2L
            coVerify { fakeRepository.guardar(contacto) }
        }
    }
})
