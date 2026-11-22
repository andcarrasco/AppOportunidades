package com.example.oportunidades.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.model.Fechas
import com.example.oportunidades.model.ResultadoErrores
import com.example.oportunidades.model.ResultadoUiState
import com.example.oportunidades.model.data.local.ResultadoEntity
import com.example.oportunidades.repository.ResultadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultadoViewModel(
    private val repository: ResultadoRepository = ResultadoRepository()
) : ViewModel() {

    // Estado interno mutable
    private val _estado = MutableStateFlow(ResultadoUiState())

    // Estado expuesto para la UI
    val estado: StateFlow<ResultadoUiState> = _estado

    // Resultados guardados en Room
    private val _registros = MutableStateFlow<List<ResultadoEntity>>(emptyList())
    val registros: StateFlow<List<ResultadoEntity>> = _registros

    init {
        viewModelScope.launch {
            repository.obtenerTodos().collect { lista ->
                _registros.value = lista
            }
        }
    }

    // Limpia el formulario con valores iniciales para un resultado nuevo
    fun nuevo(oportunidadId: String = "") {
        _estado.value = ResultadoUiState(
            oportunidadId = oportunidadId,
            fecha = Fechas.hoy(),
            estado = "Activación realizada"
        )
    }

    // Carga un registro guardado para editarlo
    fun cargar(registro: ResultadoEntity) {
        _estado.value = ResultadoUiState(
            id = registro.id,
            oportunidadId = registro.oportunidadId,
            fecha = registro.fecha,
            estado = registro.estado,
            asistentes = registro.asistentes,
            contactos = registro.contactos,
            interesados = registro.interesados,
            inscripciones = registro.inscripciones,
            observaciones = registro.observaciones,
            proximasAcciones = registro.proximasAcciones
        )
    }

    // Cada oportunidad tiene un resultado: se edita el registrado o se prepara uno nuevo
    fun abrir(oportunidadId: Long) {
        val previo = _registros.value.find { it.oportunidadId == oportunidadId.toString() }
        if (previo != null) {
            cargar(previo)
        } else {
            nuevo(oportunidadId.toString())
        }
    }

    // Resultados registrados para una oportunidad
    fun deOportunidad(lista: List<ResultadoEntity>, oportunidadId: Long): List<ResultadoEntity> {
        return lista.filter { it.oportunidadId == oportunidadId.toString() }
    }

    // Actualiza el campo fecha y limpia su error
    fun onFechaChange(valor: String) {
        _estado.update {
            it.copy(
                fecha = valor,
                errores = it.errores.copy(fecha = null)
            )
        }
    }

    fun onEstadoChange(valor: String) {
        _estado.update {
            it.copy(
                estado = valor,
                errores = it.errores.copy(estado = null)
            )
        }
    }

    fun onAsistentesChange(valor: String) {
        _estado.update {
            it.copy(
                asistentes = valor,
                errores = it.errores.copy(asistentes = null)
            )
        }
    }

    fun onContactosChange(valor: String) {
        _estado.update {
            it.copy(
                contactos = valor,
                errores = it.errores.copy(contactos = null)
            )
        }
    }

    fun onInteresadosChange(valor: String) {
        _estado.update {
            it.copy(
                interesados = valor,
                errores = it.errores.copy(interesados = null)
            )
        }
    }

    fun onInscripcionesChange(valor: String) {
        _estado.update {
            it.copy(
                inscripciones = valor,
                errores = it.errores.copy(inscripciones = null)
            )
        }
    }

    fun onObservacionesChange(valor: String) {
        _estado.update {
            it.copy(
                observaciones = valor,
                errores = it.errores.copy(observaciones = null)
            )
        }
    }

    fun onProximasAccionesChange(valor: String) {
        _estado.update {
            it.copy(proximasAcciones = valor)
        }
    }

    // Indica si un número es inválido (vacío está permitido)
    private fun numeroInvalido(valor: String): Boolean {
        val numero = valor.toIntOrNull()
        return valor.isNotBlank() && (numero == null || numero < 0)
    }

    // Validación global del formulario
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value

        val errores = ResultadoErrores(
            oportunidadId = if (estadoActual.oportunidadId.isBlank())
                "Campo obligatorio"
            else null,

            fecha = if (!Fechas.valida(estadoActual.fecha))
                "Usa AAAA-MM-DD y una fecha válida"
            else null,

            estado = if (estadoActual.estado.isBlank())
                "Campo obligatorio"
            else null,

            asistentes = if (numeroInvalido(estadoActual.asistentes))
                "Ingresa un número entero no negativo"
            else null,

            contactos = if (numeroInvalido(estadoActual.contactos))
                "Ingresa un número entero no negativo"
            else null,

            interesados = if (numeroInvalido(estadoActual.interesados))
                "Ingresa un número entero no negativo"
            else null,

            inscripciones = if (numeroInvalido(estadoActual.inscripciones))
                "Ingresa un número entero no negativo"
            else null,

            observaciones = if (estadoActual.observaciones.isBlank())
                "Campo obligatorio"
            else null
        )

        val hayErrores = listOfNotNull(
            errores.oportunidadId,
            errores.fecha,
            errores.estado,
            errores.asistentes,
            errores.contactos,
            errores.interesados,
            errores.inscripciones,
            errores.observaciones
        ).isNotEmpty()

        _estado.update {
            it.copy(errores = errores)
        }

        return !hayErrores
    }

    // Guarda en Room: inserta si es nuevo o modifica si ya existe
    fun guardar(onListo: () -> Unit) {
        val a = _estado.value
        val resultado = ResultadoEntity(
            id = a.id,
            oportunidadId = a.oportunidadId,
            fecha = a.fecha,
            estado = a.estado,
            asistentes = a.asistentes,
            contactos = a.contactos,
            interesados = a.interesados,
            inscripciones = a.inscripciones,
            observaciones = a.observaciones,
            proximasAcciones = a.proximasAcciones
        )

        viewModelScope.launch {
            val id = repository.guardar(resultado)
            _estado.update { it.copy(id = id) }
            onListo()
        }
    }
}
