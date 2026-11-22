package com.example.oportunidades

import com.example.oportunidades.model.Catalogos
import com.example.oportunidades.model.Credenciales
import com.example.oportunidades.model.Usuario
import com.example.oportunidades.remote.UsuarioApiService
import com.example.oportunidades.repository.UsuarioRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class UsuarioRepositoryTest : StringSpec({

    "login() debe retornar el usuario que entrega el microservicio" {
        val usuario = Usuario(1, "Carla Demo", "ejecutivo@example.com", "Ejecutivo comercial", "Comercial", "Santiago")
        val mockApi = mockk<UsuarioApiService>()
        coEvery { mockApi.login(Credenciales("ejecutivo@example.com", "Demo2026")) } returns usuario

        val repo = UsuarioRepository(mockApi)

        runTest {
            repo.login("ejecutivo@example.com", "Demo2026") shouldBe usuario
        }
    }

    "sin conexión se aceptan solo las cuentas de demostración con su clave" {
        val repo = UsuarioRepository(mockk())

        repo.loginSinConexion("supervisor@example.com", Catalogos.CLAVE_DEMO)?.perfil shouldBe "Supervisor comercial"
        repo.loginSinConexion("activaciones@example.com", Catalogos.CLAVE_DEMO)?.perfil shouldBe "Gestión de activaciones"
        repo.loginSinConexion("supervisor@example.com", "Incorrecta").shouldBeNull()
        repo.loginSinConexion("otro@example.com", Catalogos.CLAVE_DEMO).shouldBeNull()
    }

    "existe una cuenta de prueba para cada perfil" {
        Catalogos.usuariosDemo.map { it.perfil } shouldBe listOf(
            Catalogos.EJECUTIVO,
            Catalogos.SUPERVISOR,
            Catalogos.ACTIVACIONES
        )
    }
})
