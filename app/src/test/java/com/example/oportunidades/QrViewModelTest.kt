package com.example.oportunidades

import com.example.oportunidades.model.QrResult
import com.example.oportunidades.viewmodel.QrViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class QrViewModelTest : StringSpec({

    "al detectar un código se guarda el resultado y sus campos" {
        val viewModel = QrViewModel()

        viewModel.onQrDetected("Nombre: Feria QR\nComuna: Maipú")

        viewModel.qrResult.value shouldBe QrResult("Nombre: Feria QR\nComuna: Maipú")
        viewModel.mensaje.value shouldBe "QR detectado"
        viewModel.campos() shouldBe mapOf("nombre" to "Feria QR", "comuna" to "Maipú")
    }

    "clearResult limpia el resultado y el mensaje" {
        val viewModel = QrViewModel()
        viewModel.onQrDetected("Nombre: Feria QR")

        viewModel.clearResult()

        viewModel.qrResult.value.shouldBeNull()
        viewModel.mensaje.value shouldBe ""
        viewModel.campos().shouldBeEmpty()
    }

    "mensaje actualiza el texto que ve el usuario" {
        val viewModel = QrViewModel()

        viewModel.mensaje("Se necesita permiso de cámara")

        viewModel.mensaje.value shouldBe "Se necesita permiso de cámara"
    }
})
