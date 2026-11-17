package com.example.oportunidades.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.model.Fechas
import com.example.oportunidades.model.SeguimientoErrores
import com.example.oportunidades.model.SeguimientoUiState
import com.example.oportunidades.model.data.local.AppDatabase
import com.example.oportunidades.model.data.local.SeguimientoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SeguimientoViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getDatabase(app).seguimientoDao()

    // Estado interno mutable
    private val _estado = MutableStateFlow(SeguimientoUiState())

    // Estado expuesto para la UI
    val estado: StateFlow<SeguimientoUiState> = _estado

    // Seguimientos guardados en Room
    private val _registros = MutableStateFlow<List<SeguimientoEntity>>(emptyList())
    val registros: StateFlow<List<SeguimientoEntity>> = _registros

    init {
        viewModelScope.launch {
            dao.obtenerTodos().collect { lista ->
                _registros.value = lista
            }
        }
    }

    // Limpia el formulario con valores iniciales para una gestión nueva
    fun nuevo(oportunidadId: String = "") {
        _estado.value = SeguimientoUiState(
            oportunidadId = oportunidadId,
            responsable = "Ejecutivo Demo",
            fecha = Fechas.hoy(),
            hora = "09:00"
        )
    }

    // Carga un registro guardado para editarlo
    fun cargar(registro: SeguimientoEntity) {
        _estado.value = SeguimientoUiState(
            id = registro.id,
            oportunidadId = registro.oportunidadId,
            responsable = registro.responsable,
            tipo = registro.tipo,
            fecha = registro.fecha,
            hora = registro.hora,
            resultado = registro.resultado,
            proximaAccion = registro.proximaAccion,
            proximaFecha = registro.proximaFecha,
            observaciones = registro.observaciones
        )
    }

    // Historial de gestiones de una oportunidad
    fun deOportunidad(lista: List<SeguimientoEntity>, oportunidadId: Long): List<SeguimientoEntity> {
        return lista.filter { it.oportunidadId == oportunidadId.toString() }
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

    fun onTipoChange(valor: String) {
        _estado.update {
            it.copy(
                tipo = valor,
                errores = it.errores.copy(tipo = null)
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

    fun onHoraChange(valor: String) {
        _estado.update {
            it.copy(
                hora = valor,
                errores = it.errores.copy(hora = null)
            )
        }
    }

    fun onResultadoChange(valor: String) {
        _estado.update {
            it.copy(
                resultado = valor,
                errores = it.errores.copy(resultado = null)
            )
        }
    }

    fun onProximaAccionChange(valor: String) {
        _estado.update {
            it.copy(
                proximaAccion = valor,
                errores = it.errores.copy(proximaAccion = null)
            )
        }
    }

    fun onProximaFechaChange(valor: String) {
        _estado.update {
            it.copy(
                proximaFecha = valor,
                errores = it.errores.copy(proximaFecha = null)
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

    // Validación global del formulario
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value

        val errores = SeguimientoErrores(
            oportunidadId = if (estadoActual.oportunidadId.isBlank())
                "Campo obligatorio"
            else null,

            responsable = if (estadoActual.responsable.isBlank())
                "Campo obligatorio"
            else null,

            tipo = if (estadoActual.tipo.isBlank())
                "Campo obligatorio"
            else null,

            fecha = if (!Fechas.valida(estadoActual.fecha))
                "Usa AAAA-MM-DD y una fecha válida"
            else null,

            hora = if (!Fechas.horaValida(estadoActual.hora))
                "Usa HH:mm y una hora válida"
            else null,

            resultado = if (estadoActual.resultado.isBlank())
                "Campo obligatorio"
            else null,

            // Si hay fecha de próximo seguimiento, se debe describir la acción
            proximaAccion = if (estadoActual.proximaFecha.isNotBlank() &&
                estadoActual.proximaAccion.isBlank()
            )
                "Describe la próxima acción"
            else null,

            proximaFecha = if (estadoActual.proximaAccion.isNotBlank() &&
                !Fechas.valida(estadoActual.proximaFecha)
            )
                "Indica una fecha válida"
            else if (estadoActual.proximaFecha.isNotBlank() &&
                !Fechas.valida(estadoActual.proximaFecha)
            )
                "Usa AAAA-MM-DD"
            else if (estadoActual.proximaFecha.isNotBlank() &&
                estadoActual.proximaFecha < estadoActual.fecha
            )
                "Debe ser igual o posterior a la gestión"
            else null
        )

        val hayErrores = listOfNotNull(
            errores.oportunidadId,
            errores.responsable,
            errores.tipo,
            errores.fecha,
            errores.hora,
            errores.resultado,
            errores.proximaAccion,
            errores.proximaFecha,
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
        val seguimiento = SeguimientoEntity(
            id = a.id,
            oportunidadId = a.oportunidadId,
            responsable = a.responsable,
            tipo = a.tipo,
            fecha = a.fecha,
            hora = a.hora,
            resultado = a.resultado,
            proximaAccion = a.proximaAccion,
            proximaFecha = a.proximaFecha,
            observaciones = a.observaciones
        )

        viewModelScope.launch {
            if (a.id == 0L) {
                val id = dao.insertar(seguimiento)
                _estado.update { it.copy(id = id) }
            } else {
                dao.modificar(seguimiento)
            }
            onListo()
        }
    }
}
