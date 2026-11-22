package com.example.oportunidades.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.model.Usuario
import com.example.oportunidades.model.UsuarioErrores
import com.example.oportunidades.model.UsuarioUiState
import com.example.oportunidades.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UsuarioViewModel(
    private val repository: UsuarioRepository = UsuarioRepository()
) : ViewModel() {

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

    // Completa el correo de una de las cuentas de prueba
    fun usarCuenta(correo: String) {
        _estado.update {
            it.copy(
                correo = correo,
                errores = UsuarioErrores()
            )
        }
    }

    // Validación global del formulario
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value

        val errores = UsuarioErrores(
            correo = if (!estadoActual.correo.contains("@"))
                "Correo inválido"
            else null,

            clave = if (estadoActual.clave.length < 6)
                "Debe tener al menos 6 caracteres"
            else null
        )

        val hayErrores = listOfNotNull(
            errores.correo,
            errores.clave
        ).isNotEmpty()

        _estado.update {
            it.copy(errores = errores)
        }

        return !hayErrores
    }

    // Valida las credenciales en el microservicio de usuarios.
    // Si el microservicio no responde se usan las cuentas de demostración.
    fun ingresar(onListo: () -> Unit) {
        val correo = _estado.value.correo.trim()
        val clave = _estado.value.clave

        viewModelScope.launch {
            var mensaje = ""
            var error = "Correo o clave incorrectos"
            val usuario: Usuario? = try {
                repository.login(correo, clave)
            } catch (e: HttpException) {
                // El microservicio respondió con un error
                if (e.code() == 403) {
                    error = "Usuario inactivo"
                } else if (e.code() != 401) {
                    error = "No se pudo iniciar sesión (error ${e.code()})"
                }
                null
            } catch (e: Exception) {
                // El microservicio no responde: se usan las cuentas de prueba
                mensaje = "Ingreso sin conexión con el microservicio de usuarios"
                repository.loginSinConexion(correo, clave)
            }

            if (usuario == null) {
                _estado.update {
                    it.copy(errores = it.errores.copy(clave = error))
                }
            } else {
                _estado.update {
                    it.copy(
                        activo = true,
                        nombre = usuario.nombre,
                        perfil = usuario.perfil,
                        clave = "",
                        mensaje = mensaje
                    )
                }
                onListo()
            }
        }
    }

    // Cierra la sesión y limpia el formulario para ingresar con otro perfil
    fun salir() {
        _estado.value = UsuarioUiState()
    }
}
