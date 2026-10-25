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

class SeguimientoViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getDatabase(app)
    private val dao = db.seguimientoDao()
    private val _estado = MutableStateFlow(SeguimientoUiState())
    val estado: StateFlow<SeguimientoUiState> = _estado
    private val _registros = MutableStateFlow<List<SeguimientoEntity>>(emptyList())
    val registros: StateFlow<List<SeguimientoEntity>> = _registros
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
            SeguimientoUiState(
                fecha = Fechas.hoy(),
                hora = "09:00",
                responsable = "Ejecutivo Demo",
                oportunidadId = oportunidadId,
            )
        _mensaje.value = ""
    }

    fun cargar(registro: SeguimientoEntity) {
        _estado.value =
            SeguimientoUiState(
                id = registro.id,
                oportunidadId = registro.oportunidadId,
                responsable = registro.responsable,
                tipo = registro.tipo,
                fecha = registro.fecha,
                hora = registro.hora,
                resultado = registro.resultado,
                proximaAccion = registro.proximaAccion,
                proximaFecha = registro.proximaFecha,
                observaciones = registro.observaciones,
            )
        _mensaje.value = ""
    }

    fun onOportunidadIdChange(valor: String) {
        _estado.update {
            it.copy(oportunidadId = valor, errores = it.errores.copy(oportunidadId = null))
        }
    }

    fun onResponsableChange(valor: String) {
        _estado.update {
            it.copy(responsable = valor, errores = it.errores.copy(responsable = null))
        }
    }

    fun onTipoChange(valor: String) {
        _estado.update { it.copy(tipo = valor, errores = it.errores.copy(tipo = null)) }
    }

    fun onFechaChange(valor: String) {
        _estado.update { it.copy(fecha = valor, errores = it.errores.copy(fecha = null)) }
    }

    fun onHoraChange(valor: String) {
        _estado.update { it.copy(hora = valor, errores = it.errores.copy(hora = null)) }
    }

    fun onResultadoChange(valor: String) {
        _estado.update { it.copy(resultado = valor, errores = it.errores.copy(resultado = null)) }
    }

    fun onProximaAccionChange(valor: String) {
        _estado.update {
            it.copy(proximaAccion = valor, errores = it.errores.copy(proximaAccion = null))
        }
    }

    fun onProximaFechaChange(valor: String) {
        _estado.update {
            it.copy(proximaFecha = valor, errores = it.errores.copy(proximaFecha = null))
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
                errores.responsable,
                errores.tipo,
                errores.fecha,
                errores.hora,
                errores.resultado,
                errores.proximaAccion,
                errores.proximaFecha,
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
                    SeguimientoEntity(
                        id = estadoActual.id,
                        oportunidadId = estadoActual.oportunidadId.trim(),
                        responsable = estadoActual.responsable.trim(),
                        tipo = estadoActual.tipo.trim(),
                        fecha = estadoActual.fecha.trim(),
                        hora = estadoActual.hora.trim(),
                        resultado = estadoActual.resultado.trim(),
                        proximaAccion = estadoActual.proximaAccion.trim(),
                        proximaFecha = estadoActual.proximaFecha.trim(),
                        observaciones = estadoActual.observaciones.trim(),
                    )
                val guardadoId =
                    if (estadoActual.id == 0L) dao.insertar(registro)
                    else {
                        dao.modificar(
                            id = estadoActual.id,
                            oportunidadId = registro.oportunidadId,
                            responsable = registro.responsable,
                            tipo = registro.tipo,
                            fecha = registro.fecha,
                            hora = registro.hora,
                            resultado = registro.resultado,
                            proximaAccion = registro.proximaAccion,
                            proximaFecha = registro.proximaFecha,
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
        fun validarDatos(estadoActual: SeguimientoUiState): SeguimientoErrores =
            SeguimientoErrores(
                oportunidadId =
                    if (estadoActual.oportunidadId.isBlank()) "Campo obligatorio" else null,
                responsable = if (estadoActual.responsable.isBlank()) "Campo obligatorio" else null,
                tipo = if (estadoActual.tipo.isBlank()) "Campo obligatorio" else null,
                resultado = if (estadoActual.resultado.isBlank()) "Campo obligatorio" else null,
                fecha =
                    if (!Fechas.valida(estadoActual.fecha)) "Usa AAAA-MM-DD y una fecha válida"
                    else null,
                hora =
                    if (!Fechas.horaValida(estadoActual.hora)) "Usa HH:mm y una hora válida"
                    else null,
                proximaFecha =
                    if (
                        estadoActual.proximaAccion.isNotBlank() &&
                            !Fechas.valida(estadoActual.proximaFecha)
                    )
                        "Indica una fecha válida"
                    else if (
                        estadoActual.proximaFecha.isNotBlank() &&
                            !Fechas.valida(estadoActual.proximaFecha)
                    )
                        "Usa AAAA-MM-DD"
                    else if (
                        Fechas.valida(estadoActual.proximaFecha) &&
                            Fechas.valida(estadoActual.fecha) &&
                            estadoActual.proximaFecha < estadoActual.fecha
                    )
                        "Debe ser igual o posterior a la gestión"
                    else null,
                proximaAccion =
                    if (
                        estadoActual.proximaFecha.isNotBlank() &&
                            estadoActual.proximaAccion.isBlank()
                    )
                        "Describe la próxima acción"
                    else null,
            )
    }
}
