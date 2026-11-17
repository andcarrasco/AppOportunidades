package com.example.oportunidades.viewmodel

import androidx.lifecycle.ViewModel
import com.example.oportunidades.model.UsuarioErrores
import com.example.oportunidades.model.UsuarioUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class UsuarioViewModel : ViewModel() {

    // Estado interno mutable
    private val _estado = MutableStateFlow(UsuarioUiState())

    // Estado expuesto para la UI
    val estado: StateFlow<UsuarioUiState> = _estado

    // Actualiza el campo correo y limpia su error
    fun onCorreoChange(valor: String) {
        _estado.update {
            it.copy(
                correo = valor,
                errores = it.errores.copy(correo = null)
            )
        }
    }

    fun onClaveChange(valor: String) {
        _estado.update {
            it.copy(
                clave = valor,
                errores = it.errores.copy(clave = null)
            )
        }
    }

    // Validación global del formulario (cuenta de demostración)
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value

        val errores = UsuarioErrores(
            correo = if (!estadoActual.correo.contains("@"))
                "Correo inválido"
            else if (estadoActual.correo.trim() != "ejecutivo@example.com")
                "Usa la cuenta ficticia indicada"
            else null,

            clave = if (estadoActual.clave.length < 6)
                "Debe tener al menos 6 caracteres"
            else if (estadoActual.clave != "Demo2026")
                "Clave de demostración incorrecta"
            else null
        )

        val hayErrores = listOfNotNull(
            errores.correo,
            errores.clave
        ).isNotEmpty()

        // Si los datos son correctos se inicia la sesión y se borra la clave
        _estado.update {
            it.copy(
                errores = errores,
                activo = !hayErrores,
                clave = if (hayErrores) it.clave else ""
            )
        }

        return !hayErrores
    }

    // Cierra la sesión y limpia el formulario
    fun salir() {
        _estado.value = UsuarioUiState()
    }
}
