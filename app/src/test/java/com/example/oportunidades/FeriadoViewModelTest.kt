package com.example.oportunidades

import com.example.oportunidades.model.Feriado
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.repository.FeriadoRepository
import com.example.oportunidades.viewmodel.FeriadoViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import java.io.IOException
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class FeriadoViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    val navidad = Feriado("2026-12-25", "Navidad", "Christmas Day")
    val anioNuevo = Feriado("2027-01-01", "Año Nuevo", "New Year's Day")
    val glorias = Feriado("2026-09-19", "Día de las Glorias del Ejército", "Army Day")

    "feriados debe contener los feriados de este año y del siguiente después de fetchFeriados()" {
        runTest(dispatcher) {
            val anio = LocalDate.now().year
            val fakeRepository = mockk<FeriadoRepository>()
            coEvery { fakeRepository.getFeriados(anio) } returns listOf(glorias, navidad)
            coEvery { fakeRepository.getFeriados(anio + 1) } returns listOf(anioNuevo)

            val viewModel = FeriadoViewModel(fakeRepository)
            advanceUntilIdle()

            viewModel.feriados.value shouldContainExactly listOf(glorias, navidad, anioNuevo)
            viewModel.cargando.value shouldBe false
            viewModel.mensaje.value shouldBe ""
        }
    }

    "si la API no responde se muestra un mensaje" {
        runTest(dispatcher) {
            val fakeRepository = mockk<FeriadoRepository>()
            coEvery { fakeRepository.getFeriados(any()) } throws IOException()

            val viewModel = FeriadoViewModel(fakeRepository)
            advanceUntilIdle()

            viewModel.feriados.value.shouldBeEmpty()
            viewModel.cargando.value shouldBe false
            viewModel.mensaje.value shouldBe "No se pudieron obtener los feriados. Revisa la conexión a internet."
        }
    }

    "al actualizar después de un error se limpian el mensaje y se cargan los feriados" {
        runTest(dispatcher) {
            val fakeRepository = mockk<FeriadoRepository>()
            coEvery { fakeRepository.getFeriados(any()) } throws IOException()

            val viewModel = FeriadoViewModel(fakeRepository)
            advanceUntilIdle()

            coEvery { fakeRepository.getFeriados(any()) } returns listOf(navidad)
            viewModel.fetchFeriados()
            advanceUntilIdle()

            viewModel.feriados.value shouldContainExactly listOf(navidad, navidad)
            viewModel.mensaje.value shouldBe ""
        }
    }

    "proximos deja solo los feriados desde hoy en adelante" {
        runTest(dispatcher) {
            val fakeRepository = mockk<FeriadoRepository>()
            coEvery { fakeRepository.getFeriados(any()) } returns emptyList()

            val viewModel = FeriadoViewModel(fakeRepository)
            advanceUntilIdle()

            viewModel.proximos(listOf(glorias, navidad, anioNuevo), "2026-12-25") shouldContainExactly
                    listOf(navidad, anioNuevo)
        }
    }

    "oportunidadesDelDia encuentra los eventos que comienzan en un feriado" {
        runTest(dispatcher) {
            val fakeRepository = mockk<FeriadoRepository>()
            coEvery { fakeRepository.getFeriados(any()) } returns emptyList()
            val enNavidad = OportunidadEntity(id = 1, nombre = "Feria navideña", fechaInicio = "2026-12-25")
            val otroDia = OportunidadEntity(id = 2, nombre = "Jornada", fechaInicio = "2026-12-26")

            val viewModel = FeriadoViewModel(fakeRepository)
            advanceUntilIdle()

            viewModel.oportunidadesDelDia(navidad, listOf(enNavidad, otroDia)) shouldContainExactly listOf(enNavidad)
            viewModel.oportunidadesDelDia(anioNuevo, listOf(enNavidad, otroDia)).shouldBeEmpty()
        }
    }
})
