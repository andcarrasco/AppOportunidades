package com.example.oportunidades

import com.example.oportunidades.model.Fechas
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.repository.OportunidadRepository
import com.example.oportunidades.viewmodel.OportunidadViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
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
class OportunidadViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    // Oportunidad con todos los datos obligatorios correctos
    val valida = OportunidadEntity(
        id = 3,
        nombre = "Feria Demo",
        tipo = "Feria empresarial",
        descripcion = "Feria de proveedores",
        fuente = "Sitio web",
        fechaDeteccion = "2026-11-20",
        responsable = "Carla Demo",
        prioridad = "Alta",
        potencial = "Alto",
        estado = "Evento identificado",
        organizacionId = "1",
        fechaInicio = "2026-12-15",
        fechaTermino = "2026-12-16",
        horario = "10:00",
        comuna = "Maipú",
        region = "Metropolitana",
        ubicacion = "Centro de eventos",
        publico = "Trabajadores",
        asistentes = "1500",
        condiciones = "Stand 3x3",
        requisitos = "Carta",
        costos = "120000",
        espacios = "10 stands",
        observaciones = "Ficticio"
    )

    fun repositorio(lista: List<OportunidadEntity> = listOf(valida)): OportunidadRepository {
        val fakeRepository = mockk<OportunidadRepository>()
        every { fakeRepository.obtenerTodos() } returns flowOf(lista)
        coEvery { fakeRepository.sincronizar() } returns lista
        return fakeRepository
    }

    "al iniciar carga la copia local y sincroniza con el microservicio" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()

            val viewModel = OportunidadViewModel(fakeRepository)
            advanceUntilIdle()

            viewModel.registros.value shouldContainExactly listOf(valida)
            viewModel.mensaje.value shouldBe ""
            coVerify { fakeRepository.sincronizar() }
        }
    }

    "si el microservicio no responde se avisa que se muestran los datos guardados" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            coEvery { fakeRepository.sincronizar() } throws IOException()

            val viewModel = OportunidadViewModel(fakeRepository)
            advanceUntilIdle()

            viewModel.mensaje.value shouldBe "Sin conexión con el microservicio: se muestran los datos guardados"
            viewModel.registros.value shouldContainExactly listOf(valida)

            viewModel.limpiarMensaje()
            viewModel.mensaje.value shouldBe ""

            viewModel.mensaje("Faltan datos en el código")
            viewModel.mensaje.value shouldBe "Faltan datos en el código"
        }
    }

    "nuevo deja valores iniciales y usa al usuario de la sesión como responsable" {
        runTest(dispatcher) {
            val viewModel = OportunidadViewModel(repositorio())
            advanceUntilIdle()

            viewModel.usuario("Carla Demo")
            viewModel.nuevo()

            val estado = viewModel.estado.value
            estado.id shouldBe 0L
            estado.estado shouldBe "Evento identificado"
            estado.prioridad shouldBe "Media"
            estado.responsable shouldBe "Carla Demo"
            estado.fechaDeteccion shouldBe Fechas.hoy()
            estado.costos shouldBe "0"
            estado.asistentes shouldBe "0"
            viewModel.ficha.value.shouldBeNull()
        }
    }

    "cargar copia la oportunidad al formulario y muestra su ficha" {
        runTest(dispatcher) {
            val viewModel = OportunidadViewModel(repositorio())
            advanceUntilIdle()

            viewModel.cargar(valida)

            viewModel.estado.value.nombre shouldBe "Feria Demo"
            viewModel.estado.value.observaciones shouldBe "Ficticio"
            viewModel.ficha.value shouldBe valida

            viewModel.nuevo()
            viewModel.editarFicha()
            viewModel.estado.value.id shouldBe 0L

            viewModel.cargar(valida)
            viewModel.onNombreChange("Otro nombre")
            viewModel.editarFicha()
            viewModel.estado.value.nombre shouldBe "Feria Demo"
        }
    }

    "cada campo se actualiza y limpia su error" {
        runTest(dispatcher) {
            val viewModel = OportunidadViewModel(repositorio())
            advanceUntilIdle()
            viewModel.cargar(OportunidadEntity(costos = "-1"))
            viewModel.validarFormulario() shouldBe false

            viewModel.onNombreChange("Nombre")
            viewModel.onTipoChange("Feria empresarial")
            viewModel.onDescripcionChange("Descripción")
            viewModel.onFuenteChange("Fuente")
            viewModel.onFechaDeteccionChange("2026-11-20")
            viewModel.onResponsableChange("Carla Demo")
            viewModel.onPrioridadChange("Alta")
            viewModel.onPotencialChange("Alto")
            viewModel.onEstadoChange("Contacto pendiente")
            viewModel.onOrganizacionIdChange("1")
            viewModel.onFechaInicioChange("2026-12-15")
            viewModel.onFechaTerminoChange("2026-12-16")
            viewModel.onHorarioChange("10:00")
            viewModel.onComunaChange("Maipú")
            viewModel.onRegionChange("Metropolitana")
            viewModel.onUbicacionChange("Centro")
            viewModel.onPublicoChange("Trabajadores")
            viewModel.onAsistentesChange("100")
            viewModel.onCondicionesChange("Condiciones")
            viewModel.onRequisitosChange("Requisitos")
            viewModel.onCostosChange("0")
            viewModel.onEspaciosChange("Espacios")
            viewModel.onObservacionesChange("Observaciones")

            val estado = viewModel.estado.value
            estado.nombre shouldBe "Nombre"
            estado.tipo shouldBe "Feria empresarial"
            estado.descripcion shouldBe "Descripción"
            estado.fuente shouldBe "Fuente"
            estado.fechaDeteccion shouldBe "2026-11-20"
            estado.responsable shouldBe "Carla Demo"
            estado.prioridad shouldBe "Alta"
            estado.potencial shouldBe "Alto"
            estado.estado shouldBe "Contacto pendiente"
            estado.organizacionId shouldBe "1"
            estado.fechaInicio shouldBe "2026-12-15"
            estado.fechaTermino shouldBe "2026-12-16"
            estado.horario shouldBe "10:00"
            estado.comuna shouldBe "Maipú"
            estado.region shouldBe "Metropolitana"
            estado.ubicacion shouldBe "Centro"
            estado.publico shouldBe "Trabajadores"
            estado.asistentes shouldBe "100"
            estado.condiciones shouldBe "Condiciones"
            estado.requisitos shouldBe "Requisitos"
            estado.costos shouldBe "0"
            estado.espacios shouldBe "Espacios"
            estado.observaciones shouldBe "Observaciones"
            estado.errores.nombre.shouldBeNull()
            estado.errores.costos.shouldBeNull()
            estado.errores.horario.shouldBeNull()
            viewModel.validarFormulario() shouldBe true
        }
    }

    "una oportunidad completa pasa la validación" {
        runTest(dispatcher) {
            val viewModel = OportunidadViewModel(repositorio())
            advanceUntilIdle()

            viewModel.cargar(valida)

            viewModel.validarFormulario() shouldBe true
        }
    }

    "el formulario vacío marca los campos obligatorios" {
        runTest(dispatcher) {
            val viewModel = OportunidadViewModel(repositorio())
            advanceUntilIdle()

            viewModel.cargar(OportunidadEntity())

            viewModel.validarFormulario() shouldBe false
            val errores = viewModel.estado.value.errores
            errores.nombre shouldBe "Campo obligatorio"
            errores.tipo shouldBe "Campo obligatorio"
            errores.descripcion shouldBe "Campo obligatorio"
            errores.fuente shouldBe "Campo obligatorio"
            errores.fechaDeteccion shouldBe "Usa AAAA-MM-DD y una fecha válida"
            errores.responsable shouldBe "Campo obligatorio"
            errores.prioridad shouldBe "Campo obligatorio"
            errores.potencial shouldBe "Selecciona Bajo, Medio o Alto"
            errores.estado shouldBe "Campo obligatorio"
            errores.organizacionId shouldBe "Campo obligatorio"
            errores.fechaInicio shouldBe "Usa AAAA-MM-DD y una fecha válida"
            errores.fechaTermino shouldBe "Usa AAAA-MM-DD y una fecha válida"
            errores.horario shouldBe "Usa HH:mm y una hora válida"
            errores.comuna shouldBe "Campo obligatorio"
            errores.region shouldBe "Campo obligatorio"
            errores.publico shouldBe "Campo obligatorio"
            errores.costos shouldBe "Ingresa un costo no negativo"
            errores.asistentes.shouldBeNull()
        }
    }

    "valida fechas, horas, potencial, asistentes y costos" {
        runTest(dispatcher) {
            val viewModel = OportunidadViewModel(repositorio())
            advanceUntilIdle()

            viewModel.cargar(
                valida.copy(
                    fechaInicio = "2026-12-20",
                    fechaTermino = "2026-12-10",
                    horario = "25:00",
                    potencial = "Muy alto",
                    asistentes = "-5",
                    costos = "abc"
                )
            )

            viewModel.validarFormulario() shouldBe false
            val errores = viewModel.estado.value.errores
            errores.fechaTermino shouldBe "El término no puede ser anterior al inicio"
            errores.horario shouldBe "Usa HH:mm y una hora válida"
            errores.potencial shouldBe "Selecciona Bajo, Medio o Alto"
            errores.asistentes shouldBe "Ingresa un número entero no negativo"
            errores.costos shouldBe "Ingresa un costo no negativo"
            errores.fechaInicio.shouldBeNull()
        }
    }

    "cargarDesdeQr completa el formulario con los datos del código" {
        runTest(dispatcher) {
            val viewModel = OportunidadViewModel(repositorio())
            advanceUntilIdle()
            viewModel.usuario("Carla Demo")

            viewModel.cargarDesdeQr(
                mapOf(
                    "nombre" to "Feria QR",
                    "tipo" to "feria EMPRESARIAL",
                    "potencial" to "alto",
                    "prioridad" to "No existe",
                    "fechaInicio" to "2026-12-15",
                    "comuna" to "Maipú"
                ),
                "7"
            )

            val estado = viewModel.estado.value
            estado.nombre shouldBe "Feria QR"
            estado.tipo shouldBe "Feria empresarial"
            estado.potencial shouldBe "Alto"
            estado.prioridad shouldBe "Media"
            estado.fuente shouldBe "Código QR"
            estado.fechaTermino shouldBe "2026-12-15"
            estado.organizacionId shouldBe "7"
            estado.responsable shouldBe "Carla Demo"
            estado.comuna shouldBe "Maipú"
        }
    }

    "guardar envía la oportunidad al microservicio y deja su ficha" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            val guardada = valida.copy(id = 10)
            coEvery { fakeRepository.guardar(any()) } returns guardada

            val viewModel = OportunidadViewModel(fakeRepository)
            advanceUntilIdle()
            viewModel.cargar(valida.copy(id = 0))
            var listo = false

            viewModel.guardar { listo = true }
            advanceUntilIdle()

            listo shouldBe true
            viewModel.estado.value.id shouldBe 10L
            viewModel.ficha.value shouldBe guardada
            coVerify { fakeRepository.guardar(match { it.nombre == "Feria Demo" && it.id == 0L }) }
        }
    }

    "si guardar falla se muestra un mensaje y no se continúa" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            coEvery { fakeRepository.guardar(any()) } throws IOException()

            val viewModel = OportunidadViewModel(fakeRepository)
            advanceUntilIdle()
            viewModel.cargar(valida)
            var listo = false

            viewModel.guardar { listo = true }
            advanceUntilIdle()

            listo shouldBe false
            viewModel.mensaje.value shouldBe "No se pudo guardar: revisa que el microservicio esté activo"
        }
    }

    "cambiarEstado actualiza la ficha con la respuesta del microservicio" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            val confirmada = valida.copy(estado = "Participación confirmada")
            coEvery { fakeRepository.cambiarEstado(valida, "Participación confirmada") } returns confirmada

            val viewModel = OportunidadViewModel(fakeRepository)
            advanceUntilIdle()
            viewModel.cargar(valida)
            var listo = false

            viewModel.cambiarEstado("Participación confirmada") { listo = true }
            advanceUntilIdle()

            listo shouldBe true
            viewModel.ficha.value shouldBe confirmada
        }
    }

    "cambiarEstado sin ficha no hace nada y si falla muestra un mensaje" {
        runTest(dispatcher) {
            val fakeRepository = repositorio(emptyList())
            coEvery { fakeRepository.cambiarEstado(any(), any()) } throws IOException()

            val viewModel = OportunidadViewModel(fakeRepository)
            advanceUntilIdle()
            var listo = false

            viewModel.cambiarEstado("Oportunidad descartada") { listo = true }
            advanceUntilIdle()
            listo shouldBe false
            coVerify(exactly = 0) { fakeRepository.cambiarEstado(any(), any()) }

            val conFicha = OportunidadViewModel(repositorio().also {
                coEvery { it.cambiarEstado(any(), any()) } throws IOException()
            })
            advanceUntilIdle()
            conFicha.cargar(valida)
            conFicha.cambiarEstado("Oportunidad descartada") { listo = true }
            advanceUntilIdle()

            listo shouldBe false
            conFicha.mensaje.value shouldBe "No se pudo cambiar el estado: revisa que el microservicio esté activo"
        }
    }

    "eliminar borra la oportunidad y limpia el formulario" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            coEvery { fakeRepository.eliminar(valida) } returns Unit

            val viewModel = OportunidadViewModel(fakeRepository)
            advanceUntilIdle()
            viewModel.cargar(valida)
            var listo = false

            viewModel.eliminar { listo = true }
            advanceUntilIdle()

            listo shouldBe true
            viewModel.ficha.value.shouldBeNull()
            viewModel.estado.value.id shouldBe 0L
            coVerify { fakeRepository.eliminar(valida) }
        }
    }

    "si eliminar falla se conserva la ficha y se muestra un mensaje" {
        runTest(dispatcher) {
            val fakeRepository = repositorio()
            coEvery { fakeRepository.eliminar(any()) } throws IOException()

            val viewModel = OportunidadViewModel(fakeRepository)
            advanceUntilIdle()
            viewModel.cargar(valida)

            viewModel.eliminar { }
            advanceUntilIdle()

            viewModel.ficha.value.shouldNotBeNull()
            viewModel.mensaje.value shouldBe "No se pudo eliminar: revisa que el microservicio esté activo"
        }
    }

    "eliminar sin ficha no llama al repositorio" {
        runTest(dispatcher) {
            val fakeRepository = repositorio(emptyList())

            val viewModel = OportunidadViewModel(fakeRepository)
            advanceUntilIdle()

            viewModel.eliminar { }
            advanceUntilIdle()

            coVerify(exactly = 0) { fakeRepository.eliminar(any()) }
        }
    }
})
