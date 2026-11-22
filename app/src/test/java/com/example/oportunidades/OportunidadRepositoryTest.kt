package com.example.oportunidades

import com.example.oportunidades.model.CambioEstado
import com.example.oportunidades.model.data.local.OportunidadDao
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.model.data.local.SeguimientoDao
import com.example.oportunidades.remote.OportunidadApiService
import com.example.oportunidades.repository.OportunidadRepository
import io.kotest.assertions.throwables.shouldThrow
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
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class OportunidadRepositoryTest : StringSpec({

    val feria = OportunidadEntity(id = 3, nombre = "Feria Demo", estado = "Evento identificado", responsable = "Carla Demo")

    "obtenerTodos() entrega la copia guardada en Room" {
        val mockDao = mockk<OportunidadDao>()
        every { mockDao.obtenerTodos() } returns flowOf(listOf(feria))
        val repo = OportunidadRepository(mockDao, mockk(), mockk())

        runTest {
            repo.obtenerTodos().first() shouldContainExactly listOf(feria)
        }
    }

    "sincronizar() debe reemplazar la copia local con la lista del microservicio" {
        val fakeOportunidades = listOf(feria, feria.copy(id = 4, nombre = "Jornada Demo"))
        val mockDao = mockk<OportunidadDao>(relaxed = true)
        val mockApi = mockk<OportunidadApiService>()
        coEvery { mockApi.getOportunidades() } returns fakeOportunidades
        val repo = OportunidadRepository(mockDao, mockk(), mockApi)

        runTest {
            repo.sincronizar() shouldContainExactly fakeOportunidades
            coVerify { mockDao.reemplazarTodos(fakeOportunidades) }
        }
    }

    "guardar() una oportunidad nueva hace POST y registra su estado inicial en el historial" {
        val nueva = feria.copy(id = 0)
        val mockDao = mockk<OportunidadDao>(relaxed = true)
        val mockSeguimientos = mockk<SeguimientoDao>(relaxed = true)
        val mockApi = mockk<OportunidadApiService>()
        coEvery { mockApi.crear(nueva) } returns feria
        val repo = OportunidadRepository(mockDao, mockSeguimientos, mockApi)

        runTest {
            repo.guardar(nueva) shouldBe feria
            coVerify { mockDao.guardar(feria) }
            coVerify(exactly = 0) { mockApi.actualizar(any(), any()) }
            coVerify {
                mockSeguimientos.insertar(match {
                    it.oportunidadId == "3" &&
                            it.tipo == "Cambio de estado" &&
                            it.resultado == "Registro: Evento identificado"
                })
            }
        }
    }

    "guardar() una oportunidad existente hace PUT y registra el cambio de estado" {
        val modificada = feria.copy(estado = "Contacto realizado")
        val mockDao = mockk<OportunidadDao>(relaxed = true)
        val mockSeguimientos = mockk<SeguimientoDao>(relaxed = true)
        val mockApi = mockk<OportunidadApiService>()
        coEvery { mockDao.obtener(3) } returns feria
        coEvery { mockApi.actualizar(3, modificada) } returns modificada
        val repo = OportunidadRepository(mockDao, mockSeguimientos, mockApi)

        runTest {
            repo.guardar(modificada) shouldBe modificada
            coVerify(exactly = 0) { mockApi.crear(any()) }
            coVerify {
                mockSeguimientos.insertar(match {
                    it.resultado == "Evento identificado → Contacto realizado"
                })
            }
        }
    }

    "guardar() sin cambio de estado no agrega historial" {
        val modificada = feria.copy(nombre = "Feria Demo 2026")
        val mockDao = mockk<OportunidadDao>(relaxed = true)
        val mockSeguimientos = mockk<SeguimientoDao>(relaxed = true)
        val mockApi = mockk<OportunidadApiService>()
        coEvery { mockDao.obtener(3) } returns feria
        coEvery { mockApi.actualizar(3, modificada) } returns modificada
        val repo = OportunidadRepository(mockDao, mockSeguimientos, mockApi)

        runTest {
            repo.guardar(modificada)
            coVerify { mockDao.guardar(modificada) }
            coVerify(exactly = 0) { mockSeguimientos.insertar(any()) }
        }
    }

    "cambiarEstado() hace PATCH, guarda la copia y deja el cambio en el historial" {
        val confirmada = feria.copy(estado = "Participación confirmada")
        val mockDao = mockk<OportunidadDao>(relaxed = true)
        val mockSeguimientos = mockk<SeguimientoDao>(relaxed = true)
        val mockApi = mockk<OportunidadApiService>()
        coEvery { mockApi.cambiarEstado(3, CambioEstado("Participación confirmada")) } returns confirmada
        val repo = OportunidadRepository(mockDao, mockSeguimientos, mockApi)

        runTest {
            repo.cambiarEstado(feria, "Participación confirmada") shouldBe confirmada
            coVerify { mockDao.guardar(confirmada) }
            coVerify {
                mockSeguimientos.insertar(match {
                    it.resultado == "Evento identificado → Participación confirmada"
                })
            }
        }
    }

    "cambiarEstado() al mismo estado no agrega historial" {
        val mockDao = mockk<OportunidadDao>(relaxed = true)
        val mockSeguimientos = mockk<SeguimientoDao>(relaxed = true)
        val mockApi = mockk<OportunidadApiService>()
        coEvery { mockApi.cambiarEstado(3, CambioEstado("Evento identificado")) } returns feria
        val repo = OportunidadRepository(mockDao, mockSeguimientos, mockApi)

        runTest {
            repo.cambiarEstado(feria, "Evento identificado")
            coVerify(exactly = 0) { mockSeguimientos.insertar(any()) }
        }
    }

    "eliminar() hace DELETE en el microservicio y en la copia local" {
        val mockDao = mockk<OportunidadDao>(relaxed = true)
        val mockApi = mockk<OportunidadApiService>()
        coEvery { mockApi.eliminar(3) } returns Response.success(Unit)
        val repo = OportunidadRepository(mockDao, mockk(), mockApi)

        runTest {
            repo.eliminar(feria)
            coVerify { mockDao.eliminar(feria) }
        }
    }

    "eliminar() no borra la copia local si el microservicio responde con error" {
        val mockDao = mockk<OportunidadDao>(relaxed = true)
        val mockApi = mockk<OportunidadApiService>()
        coEvery { mockApi.eliminar(3) } returns Response.error<Unit>(404, "".toResponseBody())
        val repo = OportunidadRepository(mockDao, mockk(), mockApi)

        runTest {
            shouldThrow<Exception> { repo.eliminar(feria) }
            coVerify(exactly = 0) { mockDao.eliminar(any()) }
        }
    }
})
