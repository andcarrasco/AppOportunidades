package com.example.oportunidades

import com.example.oportunidades.model.Feriado
import com.example.oportunidades.remote.FeriadoApiService
import com.example.oportunidades.repository.FeriadoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class FeriadoRepositoryTest : StringSpec({

    "getFeriados() debe retornar la lista de feriados simulada" {
        // 1. Simulamos el resultado de la API
        val fakeFeriados = listOf(
            Feriado("2026-12-08", "Inmaculada Concepción", "Immaculate Conception"),
            Feriado("2026-12-25", "Navidad", "Christmas Day")
        )
        // 2. Creamos un mock de FeriadoApiService
        val mockApi = mockk<FeriadoApiService>()
        coEvery { mockApi.getFeriados(2026) } returns fakeFeriados
        // 3. Usamos el repositorio inyectando el mock
        val repo = FeriadoRepository(mockApi)
        // 4. Ejecutamos el test
        runTest {
            val result = repo.getFeriados(2026)
            result shouldContainExactly fakeFeriados
        }
    }
})
