package com.example.oportunidades

import com.example.oportunidades.model.Usuario
import com.example.oportunidades.repository.UsuarioRepository
import com.example.oportunidades.viewmodel.UsuarioViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(dispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    val supervisor = Usuario(2, "Rodrigo Demo", "supervisor@example.com", "Supervisor comercial", "Comercial", "Santiago")

    "el formulario vacío muestra errores en correo y clave" {
        val viewModel = UsuarioViewModel(mockk())

        viewModel.validarFormulario() shouldBe false
        viewModel.estado.value.errores.correo shouldBe "Correo inválido"
        viewModel.estado.value.errores.clave shouldBe "Debe tener al menos 6 caracteres"
    }

    "escribir en un campo limpia su error" {
        val viewModel = UsuarioViewModel(mockk())
        viewModel.validarFormulario()

        viewModel.onCorreoChange("supervisor@example.com")
        viewModel.onClaveChange("Demo2026")

        viewModel.estado.value.errores.correo.shouldBeNull()
        viewModel.estado.value.errores.clave.shouldBeNull()
        viewModel.validarFormulario() shouldBe true
    }

    "usarCuenta completa el correo y limpia los errores" {
        val viewModel = UsuarioViewModel(mockk())
        viewModel.validarFormulario()

        viewModel.usarCuenta("activaciones@example.com")

        viewModel.estado.value.correo shouldBe "activaciones@example.com"
        viewModel.estado.value.errores.correo.shouldBeNull()
        viewModel.estado.value.errores.clave.shouldBeNull()
    }

    "ingresar con el microservicio inicia sesión con el perfil del usuario" {
        runTest(dispatcher) {
            val fakeRepository = mockk<UsuarioRepository>()
            coEvery { fakeRepository.login("supervisor@example.com", "Demo2026") } returns supervisor

            val viewModel = UsuarioViewModel(fakeRepository)
            viewModel.onCorreoChange(" supervisor@example.com ")
            viewModel.onClaveChange("Demo2026")
            var listo = false

            viewModel.ingresar { listo = true }
            advanceUntilIdle()

            listo shouldBe true
            viewModel.estado.value.activo shouldBe true
            viewModel.estado.value.nombre shouldBe "Rodrigo Demo"
            viewModel.estado.value.perfil shouldBe "Supervisor comercial"
            viewModel.estado.value.clave shouldBe ""
            viewModel.estado.value.mensaje shouldBe ""
        }
    }

    "si el microservicio rechaza la clave se muestra un error" {
        runTest(dispatcher) {
            val fakeRepository = mockk<UsuarioRepository>()
            coEvery { fakeRepository.login(any(), any()) } throws
                    HttpException(Response.error<Any>(401, ResponseBody.create(null, "")))

            val viewModel = UsuarioViewModel(fakeRepository)
            viewModel.onCorreoChange("supervisor@example.com")
            viewModel.onClaveChange("Incorrecta")
            var listo = false

            viewModel.ingresar { listo = true }
            advanceUntilIdle()

            listo shouldBe false
            viewModel.estado.value.activo shouldBe false
            viewModel.estado.value.errores.clave shouldBe "Correo o clave incorrectos"
        }
    }

    "un usuario inactivo no puede ingresar" {
        runTest(dispatcher) {
            val fakeRepository = mockk<UsuarioRepository>()
            coEvery { fakeRepository.login(any(), any()) } throws
                    HttpException(Response.error<Any>(403, ResponseBody.create(null, "")))

            val viewModel = UsuarioViewModel(fakeRepository)
            viewModel.onCorreoChange("supervisor@example.com")
            viewModel.onClaveChange("Demo2026")

            viewModel.ingresar { }
            advanceUntilIdle()

            viewModel.estado.value.activo shouldBe false
            viewModel.estado.value.errores.clave shouldBe "Usuario inactivo"
        }
    }

    "otro error del microservicio muestra su código" {
        runTest(dispatcher) {
            val fakeRepository = mockk<UsuarioRepository>()
            coEvery { fakeRepository.login(any(), any()) } throws
                    HttpException(Response.error<Any>(500, ResponseBody.create(null, "")))

            val viewModel = UsuarioViewModel(fakeRepository)
            viewModel.onCorreoChange("supervisor@example.com")
            viewModel.onClaveChange("Demo2026")

            viewModel.ingresar { }
            advanceUntilIdle()

            viewModel.estado.value.activo shouldBe false
            viewModel.estado.value.errores.clave shouldBe "No se pudo iniciar sesión (error 500)"
        }
    }

    "sin conexión se ingresa con las cuentas de demostración" {
        runTest(dispatcher) {
            val fakeRepository = mockk<UsuarioRepository>()
            coEvery { fakeRepository.login(any(), any()) } throws IOException()
            every { fakeRepository.loginSinConexion("supervisor@example.com", "Demo2026") } returns supervisor

            val viewModel = UsuarioViewModel(fakeRepository)
            viewModel.onCorreoChange("supervisor@example.com")
            viewModel.onClaveChange("Demo2026")
            var listo = false

            viewModel.ingresar { listo = true }
            advanceUntilIdle()

            listo shouldBe true
            viewModel.estado.value.perfil shouldBe "Supervisor comercial"
            viewModel.estado.value.mensaje shouldBe "Ingreso sin conexión con el microservicio de usuarios"
        }
    }

    "sin conexión y con datos incorrectos no se inicia sesión" {
        runTest(dispatcher) {
            val fakeRepository = mockk<UsuarioRepository>()
            coEvery { fakeRepository.login(any(), any()) } throws IOException()
            every { fakeRepository.loginSinConexion(any(), any()) } returns null

            val viewModel = UsuarioViewModel(fakeRepository)
            viewModel.onCorreoChange("otro@example.com")
            viewModel.onClaveChange("Demo2026")

            viewModel.ingresar { }
            advanceUntilIdle()

            viewModel.estado.value.activo shouldBe false
            viewModel.estado.value.errores.clave.shouldNotBeNull()
        }
    }

    "salir limpia la sesión para cambiar de perfil" {
        runTest(dispatcher) {
            val fakeRepository = mockk<UsuarioRepository>()
            coEvery { fakeRepository.login(any(), any()) } returns supervisor

            val viewModel = UsuarioViewModel(fakeRepository)
            viewModel.onCorreoChange("supervisor@example.com")
            viewModel.onClaveChange("Demo2026")
            viewModel.ingresar { }
            advanceUntilIdle()

            viewModel.salir()

            viewModel.estado.value.activo shouldBe false
            viewModel.estado.value.perfil shouldBe ""
            viewModel.estado.value.correo shouldBe ""
        }
    }
})
