package com.example.oportunidades.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.model.*
import com.example.oportunidades.model.data.local.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultadoViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getDatabase(app)
    private val dao = db.resultadoDao()
    private val _estado = MutableStateFlow(ResultadoUiState())
    val estado: StateFlow<ResultadoUiState> = _estado
    private val _registros = MutableStateFlow<List<ResultadoEntity>>(emptyList())
    val registros: StateFlow<List<ResultadoEntity>> = _registros
    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando
    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje
    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    init {
        viewModelScope.launch {
            try {
                dao.obtenerTodos().collect {
                    _registros.value = it
                    _cargando.value = false
                }
            } catch (e: Exception) {
                _cargando.value = false
                _mensaje.value = "No se pudieron cargar los registros"
            }
        }
    }

    fun nuevo(oportunidadId: String = "") {
        _estado.value =
            ResultadoUiState(
                fecha = Fechas.hoy(),
                oportunidadId = oportunidadId,
                estado = "Activación realizada",
            )
        _mensaje.value = ""
    }

    fun cargar(registro: ResultadoEntity) {
        _estado.value =
            ResultadoUiState(
                id = registro.id,
                oportunidadId = registro.oportunidadId,
                fecha = registro.fecha,
                estado = registro.estado,
                asistentes = registro.asistentes,
                contactos = registro.contactos,
                interesados = registro.interesados,
                inscripciones = registro.inscripciones,
                observaciones = registro.observaciones,
            )
        _mensaje.value = ""
    }

    fun onOportunidadIdChange(valor: String) {
        _estado.update {
            it.copy(oportunidadId = valor, errores = it.errores.copy(oportunidadId = null))
        }
    }

    fun onFechaChange(valor: String) {
        _estado.update { it.copy(fecha = valor, errores = it.errores.copy(fecha = null)) }
    }

    fun onEstadoChange(valor: String) {
        _estado.update { it.copy(estado = valor, errores = it.errores.copy(estado = null)) }
    }

    fun onAsistentesChange(valor: String) {
        _estado.update { it.copy(asistentes = valor, errores = it.errores.copy(asistentes = null)) }
    }

    fun onContactosChange(valor: String) {
        _estado.update { it.copy(contactos = valor, errores = it.errores.copy(contactos = null)) }
    }

    fun onInteresadosChange(valor: String) {
        _estado.update {
            it.copy(interesados = valor, errores = it.errores.copy(interesados = null))
        }
    }

    fun onInscripcionesChange(valor: String) {
        _estado.update {
            it.copy(inscripciones = valor, errores = it.errores.copy(inscripciones = null))
        }
    }

    fun onObservacionesChange(valor: String) {
        _estado.update {
            it.copy(observaciones = valor, errores = it.errores.copy(observaciones = null))
        }
    }

    fun validarFormulario(): Boolean {
        val errores = validarDatos(_estado.value)
        _estado.update { it.copy(errores = errores) }
        return listOfNotNull(
                errores.oportunidadId,
                errores.fecha,
                errores.estado,
                errores.asistentes,
                errores.contactos,
                errores.interesados,
                errores.inscripciones,
                errores.observaciones,
            )
            .isEmpty()
    }

    fun guardar(onListo: () -> Unit) {
        if (_guardando.value || !validarFormulario()) return
        val estadoActual = _estado.value
        _guardando.value = true
        _mensaje.value = ""
        viewModelScope.launch {
            try {

                if (
                    db.oportunidadDao().obtener(estadoActual.oportunidadId.toLongOrNull() ?: 0) ==
                        null
                ) {
                    _mensaje.value = "La oportunidad ya no está disponible"
                    return@launch
                }

                val registro =
                    ResultadoEntity(
                        id = estadoActual.id,
                        oportunidadId = estadoActual.oportunidadId.trim(),
                        fecha = estadoActual.fecha.trim(),
                        estado = estadoActual.estado.trim(),
                        asistentes = estadoActual.asistentes.trim(),
                        contactos = estadoActual.contactos.trim(),
                        interesados = estadoActual.interesados.trim(),
                        inscripciones = estadoActual.inscripciones.trim(),
                        observaciones = estadoActual.observaciones.trim(),
                    )
                val guardadoId =
                    if (estadoActual.id == 0L) dao.insertar(registro)
                    else {
                        dao.modificar(
                            id = estadoActual.id,
                            oportunidadId = registro.oportunidadId,
                            fecha = registro.fecha,
                            estado = registro.estado,
                            asistentes = registro.asistentes,
                            contactos = registro.contactos,
                            interesados = registro.interesados,
                            inscripciones = registro.inscripciones,
                            observaciones = registro.observaciones,
                        )
                        estadoActual.id
                    }

                _estado.update { it.copy(id = guardadoId) }
                _mensaje.value = ""
                onListo()
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo completar el guardado. Revisa los datos e intenta nuevamente."
            } finally {
                _guardando.value = false
            }
        }
    }

    companion object {
        fun validarDatos(estadoActual: ResultadoUiState): ResultadoErrores {
            val asistentes = estadoActual.asistentes.toIntOrNull()
            val contactos = estadoActual.contactos.toIntOrNull()
            val interesados = estadoActual.interesados.toIntOrNull()
            val inscripciones = estadoActual.inscripciones.toIntOrNull()

            return ResultadoErrores(
                oportunidadId =
                    if (estadoActual.oportunidadId.isBlank()) "Campo obligatorio" else null,
                estado = if (estadoActual.estado.isBlank()) "Campo obligatorio" else null,
                observaciones =
                    if (estadoActual.observaciones.isBlank()) "Campo obligatorio" else null,
                fecha =
                    if (!Fechas.valida(estadoActual.fecha)) "Usa AAAA-MM-DD y una fecha válida"
                    else null,
                asistentes =
                    if (
                        estadoActual.asistentes.isNotBlank() &&
                            (asistentes == null || asistentes < 0)
                    )
                        "Ingresa un entero no negativo"
                    else null,
                contactos =
                    if (estadoActual.contactos.isNotBlank() && (contactos == null || contactos < 0))
                        "Ingresa un entero no negativo"
                    else null,
                interesados =
                    if (
                        estadoActual.interesados.isNotBlank() &&
                            (interesados == null || interesados < 0)
                    )
                        "Ingresa un entero no negativo"
                    else null,
                inscripciones =
                    if (
                        estadoActual.inscripciones.isNotBlank() &&
                            (inscripciones == null || inscripciones < 0)
                    )
                        "Ingresa un entero no negativo"
                    else null,
            )
        }
    }
}
