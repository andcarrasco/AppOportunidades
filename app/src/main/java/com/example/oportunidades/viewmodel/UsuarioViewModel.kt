package com.example.oportunidades.viewmodel

import androidx.lifecycle.ViewModel
import com.example.oportunidades.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class UsuarioViewModel : ViewModel() {
    private val _estado = MutableStateFlow(UsuarioUiState())
    val estado: StateFlow<UsuarioUiState> = _estado

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun ingresar(): Boolean {
        val estadoActual = _estado.value
        val errores =
            UsuarioErrores(
                correo =
                    if (!estadoActual.correo.contains("@")) "Correo inválido"
                    else if (estadoActual.correo.trim() != "ejecutivo@example.com")
                        "Usa la cuenta ficticia indicada"
                    else null,
                clave =
                    if (estadoActual.clave.length < 6) "Mínimo seis caracteres"
                    else if (estadoActual.clave != "Demo2026") "Clave de demostración incorrecta"
                    else null,
            )
        val valido = errores.correo == null && errores.clave == null
        _estado.update {
            it.copy(errores = errores, activo = valido, clave = if (valido) "" else it.clave)
        }
        return valido
    }

    fun salir() {
        _estado.value = UsuarioUiState()
    }
}
