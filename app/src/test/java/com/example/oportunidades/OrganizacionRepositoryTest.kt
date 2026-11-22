package com.example.oportunidades

import com.example.oportunidades.model.data.local.ActivacionDao
import com.example.oportunidades.model.data.local.ActivacionEntity
import com.example.oportunidades.model.data.local.ContactoDao
import com.example.oportunidades.model.data.local.ContactoEntity
import com.example.oportunidades.model.data.local.OrganizacionDao
import com.example.oportunidades.model.data.local.OrganizacionEntity
import com.example.oportunidades.model.data.local.ResultadoDao
import com.example.oportunidades.model.data.local.ResultadoEntity
import com.example.oportunidades.model.data.local.SeguimientoDao
import com.example.oportunidades.model.data.local.SeguimientoEntity
import com.example.oportunidades.repository.ActivacionRepository
import com.example.oportunidades.repository.ContactoRepository
import com.example.oportunidades.repository.OrganizacionRepository
import com.example.oportunidades.repository.ResultadoRepository
import com.example.oportunidades.repository.SeguimientoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

// Repositorios que solo usan Room: insertan si el registro es nuevo y modifican si ya existe
class OrganizacionRepositoryTest : StringSpec({

    "OrganizacionRepository inserta las nuevas, modifica las existentes y lista las guardadas" {
        val organizacion = OrganizacionEntity(id = 0, nombre = "Empresa Demo")
        val mockDao = mockk<OrganizacionDao>(relaxed = true)
        coEvery { mockDao.insertar(organizacion) } returns 5L
        every { mockDao.obtenerTodos() } returns flowOf(listOf(organizacion.copy(id = 5)))
        val repo = OrganizacionRepository(mockDao)

        runTest {
            repo.guardar(organizacion) shouldBe 5L
            repo.guardar(organizacion.copy(id = 5)) shouldBe 5L
            coVerify { mockDao.modificar(organizacion.copy(id = 5)) }
            repo.obtenerTodos().first() shouldContainExactly listOf(organizacion.copy(id = 5))
        }
    }

    "ContactoRepository inserta los nuevos y modifica los existentes" {
        val contacto = ContactoEntity(id = 0, nombre = "Ana Demo")
        val mockDao = mockk<ContactoDao>(relaxed = true)
        coEvery { mockDao.insertar(contacto) } returns 2L
        every { mockDao.obtenerTodos() } returns flowOf(emptyList())
        val repo = ContactoRepository(mockDao)

        runTest {
            repo.guardar(contacto) shouldBe 2L
            repo.guardar(contacto.copy(id = 2)) shouldBe 2L
            coVerify { mockDao.modificar(contacto.copy(id = 2)) }
            repo.obtenerTodos().first() shouldBe emptyList()
        }
    }

    "SeguimientoRepository inserta las nuevas gestiones y modifica las existentes" {
        val gestion = SeguimientoEntity(id = 0, oportunidadId = "1", tipo = "Llamada")
        val mockDao = mockk<SeguimientoDao>(relaxed = true)
        coEvery { mockDao.insertar(gestion) } returns 7L
        every { mockDao.obtenerTodos() } returns flowOf(listOf(gestion))
        val repo = SeguimientoRepository(mockDao)

        runTest {
            repo.guardar(gestion) shouldBe 7L
            repo.guardar(gestion.copy(id = 7)) shouldBe 7L
            coVerify { mockDao.modificar(gestion.copy(id = 7)) }
            repo.obtenerTodos().first() shouldContainExactly listOf(gestion)
        }
    }

    "ResultadoRepository inserta los nuevos resultados y modifica los existentes" {
        val resultado = ResultadoEntity(id = 0, oportunidadId = "1", estado = "Activación realizada")
        val mockDao = mockk<ResultadoDao>(relaxed = true)
        coEvery { mockDao.insertar(resultado) } returns 4L
        every { mockDao.obtenerTodos() } returns flowOf(listOf(resultado))
        val repo = ResultadoRepository(mockDao)

        runTest {
            repo.guardar(resultado) shouldBe 4L
            repo.guardar(resultado.copy(id = 4)) shouldBe 4L
            coVerify { mockDao.modificar(resultado.copy(id = 4)) }
            repo.obtenerTodos().first() shouldContainExactly listOf(resultado)
        }
    }

    "ActivacionRepository inserta las nuevas activaciones y modifica las existentes" {
        val activacion = ActivacionEntity(id = 0, oportunidadId = "1", confirmada = true)
        val mockDao = mockk<ActivacionDao>(relaxed = true)
        coEvery { mockDao.insertar(activacion) } returns 3L
        every { mockDao.obtenerTodos() } returns flowOf(listOf(activacion))
        val repo = ActivacionRepository(mockDao)

        runTest {
            repo.guardar(activacion) shouldBe 3L
            repo.guardar(activacion.copy(id = 3)) shouldBe 3L
            coVerify { mockDao.modificar(activacion.copy(id = 3)) }
            repo.obtenerTodos().first() shouldContainExactly listOf(activacion)
        }
    }
})
