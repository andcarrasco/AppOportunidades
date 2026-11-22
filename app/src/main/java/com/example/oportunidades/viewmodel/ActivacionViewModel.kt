package com.example.oportunidades.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.model.ActivacionErrores
import com.example.oportunidades.model.ActivacionUiState
import com.example.oportunidades.model.Fechas
import com.example.oportunidades.model.data.local.ActivacionEntity
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.repository.ActivacionRepository
import com.example.oportunidades.repository.OportunidadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ActivacionViewModel(
    private val repository: ActivacionRepository = ActivacionRepository(),
    private val oportunidadRepository: OportunidadRepository = OportunidadRepository()
) : ViewModel() {

    // Estado interno mutable
    private val _estado = MutableStateFlow(ActivacionUiState())

    // Estado expuesto para la UI
    val estado: StateFlow<ActivacionUiState> = _estado

    // Activaciones guardadas en Room
    private val _registros = MutableStateFlow<List<ActivacionEntity>>(emptyList())
    val registros: StateFlow<List<ActivacionEntity>> = _registros

    // Mensaje para la UI cuando el microservicio no responde
    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    init {
        viewModelScope.launch {
            repository.obtenerTodos().collect { lista ->
                _registros.value = lista
            }
        }
    }

    // Limpia el formulario para coordinar una activación nueva
    fun nuevo(oportunidadId: String = "", responsable: String = "") {
        _estado.value = ActivacionUiState(
            oportunidadId = oportunidadId,
            responsable = responsable,
            fecha = Fechas.hoy()
        )
    }

    // Carga un registro guardado para editarlo
    fun cargar(registro: ActivacionEntity) {
        _estado.value = ActivacionUiState(
            id = registro.id,
            oportunidadId = registro.oportunidadId,
            confirmada = registro.confirmada,
            responsable = registro.responsable,
            fecha = registro.fecha,
            condiciones = registro.condiciones,
            observaciones = registro.observaciones
        )
    }

    // Cada oportunidad tiene una activación: se edita la registrada o se prepara una nueva
    fun abrir(oportunidadId: Long, responsable: String) {
        val previa = _registros.value.find { it.oportunidadId == oportunidadId.toString() }
        if (previa != null) {
            cargar(previa)
        } else {
            nuevo(oportunidadId.toString(), responsable)
        }
    }

    // Activación registrada para una oportunidad
    fun deOportunidad(lista: List<ActivacionEntity>, oportunidadId: Long): ActivacionEntity? {
        return lista.find { it.oportunidadId == oportunidadId.toString() }
    }

    fun onConfirmadaChange(valor: Boolean) {
        _estado.update {
            it.copy(confirmada = valor)
        }
    }

    // Actualiza el campo responsable y limpia su error
    fun onResponsableChange(valor: String) {
        _estado.update {
            it.copy(
                responsable = valor,
                errores = it.errores.copy(responsable = null)
            )
        }
    }

    fun onFechaChange(valor: String) {
        _estado.update {
            it.copy(
                fecha = valor,
                errores = it.errores.copy(fecha = null)
            )
        }
    }

    fun onCondicionesChange(valor: String) {
        _estado.update {
            it.copy(
                condiciones = valor,
                errores = it.errores.copy(condiciones = null)
            )
        }
    }

    fun onObservacionesChange(valor: String) {
        _estado.update {
            it.copy(observaciones = valor)
        }
    }

    // Validación global del formulario
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value

        val errores = ActivacionErrores(
            oportunidadId = if (estadoActual.oportunidadId.isBlank())
                "Campo obligatorio"
            else null,

            responsable = if (estadoActual.responsable.isBlank())
                "Asigna un responsable"
            else null,

            fecha = if (!Fechas.valida(estadoActual.fecha))
                "Usa AAAA-MM-DD y una fecha válida"
            else null,

            condiciones = if (estadoActual.condiciones.isBlank())
                "Registra las condiciones de participación"
            else null
        )

        val hayErrores = listOfNotNull(
            errores.oportunidadId,
            errores.responsable,
            errores.fecha,
            errores.condiciones
        ).isNotEmpty()

        _estado.update {
            it.copy(errores = errores)
        }

        return !hayErrores
    }

    // Si se confirma la participación, primero cambia el estado de la oportunidad
    // en el microservicio (PATCH) y después guarda la activación en Room.
    fun guardar(oportunidad: OportunidadEntity?, onListo: () -> Unit) {
        val a = _estado.value
        val activacion = ActivacionEntity(
            id = a.id,
            oportunidadId = a.oportunidadId,
            confirmada = a.confirmada,
            responsable = a.responsable,
            fecha = a.fecha,
            condiciones = a.condiciones,
            observaciones = a.observaciones
        )

        _mensaje.value = ""
        viewModelScope.launch {
            try {
                if (a.confirmada && oportunidad != null && oportunidad.estado != "Participación confirmada") {
                    oportunidadRepository.cambiarEstado(oportunidad, "Participación confirmada")
                }
                val id = repository.guardar(activacion)
                _estado.update { it.copy(id = id) }
                onListo()
            } catch (e: Exception) {
                _mensaje.value = "No se pudo guardar: revisa que el microservicio esté activo"
            }
        }
    }
}
