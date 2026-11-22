package com.example.oportunidades

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.oportunidades.model.Feriado
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.repository.FeriadoRepository
import com.example.oportunidades.repository.OportunidadRepository
import com.example.oportunidades.repository.ResultadoRepository
import com.example.oportunidades.repository.SeguimientoRepository
import com.example.oportunidades.ui.screen.FeriadosScreen
import com.example.oportunidades.viewmodel.FeriadoViewModel
import com.example.oportunidades.viewmodel.GestionViewModel
import com.example.oportunidades.viewmodel.MainViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class FeriadosScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun el_feriado_y_el_evento_de_ese_dia_deben_aparecer_en_pantalla() {

        // Datos simulados (fechas lejanas para que siempre sean próximas)
        val fakeFeriados = listOf(
            Feriado(
                date = "2099-12-25",
                localName = "Navidad",
                name = "Christmas Day"
            )
        )
        val fakeOportunidades = listOf(
            OportunidadEntity(
                id = 1,
                nombre = "Feria navideña Demo",
                fechaInicio = "2099-12-25"
            )
        )

        // Simulamos los repositorios
        val fakeFeriadoRepository = mockk<FeriadoRepository>()
        val anio = LocalDate.now().year
        coEvery { fakeFeriadoRepository.getFeriados(anio) } returns fakeFeriados
        coEvery { fakeFeriadoRepository.getFeriados(anio + 1) } returns emptyList()

        val fakeOportunidadRepository = mockk<OportunidadRepository>()
        val fakeSeguimientoRepository = mockk<SeguimientoRepository>()
        val fakeResultadoRepository = mockk<ResultadoRepository>()
        every { fakeOportunidadRepository.obtenerTodos() } returns flowOf(fakeOportunidades)
        every { fakeSeguimientoRepository.obtenerTodos() } returns flowOf(emptyList())
        every { fakeResultadoRepository.obtenerTodos() } returns flowOf(emptyList())

        // Creamos los ViewModel con los repositorios simulados
        val fakeViewModel = FeriadoViewModel(fakeFeriadoRepository)
        val gestion = GestionViewModel(
            fakeOportunidadRepository,
            fakeSeguimientoRepository,
            fakeResultadoRepository
        )

        // Renderizamos la interfaz
        composeRule.setContent {
            FeriadosScreen(
                viewModel = fakeViewModel,
                gestion = gestion,
                mainViewModel = MainViewModel()
            )
        }

        // Esperamos que aparezca el feriado
        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodes(
                hasText("Evento ese día: Feria navideña Demo")
            ).fetchSemanticsNodes().isNotEmpty()
        }

        // Verificamos el feriado y el evento de ese día
        composeRule.onNodeWithText("Navidad")
            .assertIsDisplayed()

        composeRule.onNodeWithText("Evento ese día: Feria navideña Demo")
            .assertIsDisplayed()
    }
}
