package com.example.oportunidades.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.model.OrganizacionErrores
import com.example.oportunidades.model.OrganizacionUiState
import com.example.oportunidades.model.data.local.OrganizacionEntity
import com.example.oportunidades.repository.OrganizacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrganizacionViewModel(
    private val repository: OrganizacionRepository = OrganizacionRepository()
) : ViewModel() {

    // Estado interno mutable
    private val _estado = MutableStateFlow(OrganizacionUiState())

    // Estado expuesto para la UI
    val estado: StateFlow<OrganizacionUiState> = _estado

    // Organizaciones guardadas en Room
    private val _registros = MutableStateFlow<List<OrganizacionEntity>>(emptyList())
    val registros: StateFlow<List<OrganizacionEntity>> = _registros

    init {
        viewModelScope.launch {
            repository.obtenerTodos().collect { lista ->
                _registros.value = lista
            }
        }
    }

    // Limpia el formulario para un registro nuevo
    fun nuevo() {
        _estado.value = OrganizacionUiState()
    }

    // Carga un registro guardado para editarlo
    fun cargar(registro: OrganizacionEntity) {
        _estado.value = OrganizacionUiState(
            id = registro.id,
            nombre = registro.nombre,
            tipo = registro.tipo,
            rubro = registro.rubro,
            comuna = registro.comuna,
            region = registro.region,
            sitioWeb = registro.sitioWeb,
            publico = registro.publico,
            observaciones = registro.observaciones
        )
    }

    // Nombre de una organización, para mostrarlo en otras pantallas
    fun nombre(lista: List<OrganizacionEntity>, organizacionId: String): String {
        return lista.find { it.id.toString() == organizacionId }?.nombre ?: "Sin organización"
    }

    // Busca la organización por nombre y la registra si no existe (lectura de QR)
    fun buscarOCrear(nombre: String, comuna: String, region: String, onListo: (String) -> Unit) {
        val existente = _registros.value.find { it.nombre.equals(nombre.trim(), ignoreCase = true) }
        if (existente != null) {
            onListo(existente.id.toString())
            return
        }

        viewModelScope.launch {
            val id = repository.guardar(
                OrganizacionEntity(
                    nombre = nombre.trim(),
                    tipo = "Otra entidad",
                    comuna = comuna,
                    region = region,
                    observaciones = "Registrada desde un código QR"
                )
            )
            onListo(id.toString())
        }
    }

    // Actualiza el campo nombre y limpia su error
    fun onNombreChange(valor: String) {
        _estado.update {
            it.copy(
                nombre = valor,
                errores = it.errores.copy(nombre = null)
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

    fun onRubroChange(valor: String) {
        _estado.update {
            it.copy(
                rubro = valor,
                errores = it.errores.copy(rubro = null)
            )
        }
    }

    fun onComunaChange(valor: String) {
        _estado.update {
            it.copy(
                comuna = valor,
                errores = it.errores.copy(comuna = null)
            )
        }
    }

    fun onRegionChange(valor: String) {
        _estado.update {
            it.copy(
                region = valor,
                errores = it.errores.copy(region = null)
            )
        }
    }

    fun onSitioWebChange(valor: String) {
        _estado.update {
            it.copy(
                sitioWeb = valor,
                errores = it.errores.copy(sitioWeb = null)
            )
        }
    }

    fun onPublicoChange(valor: String) {
        _estado.update {
            it.copy(
                publico = valor,
                errores = it.errores.copy(publico = null)
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

        val errores = OrganizacionErrores(
            nombre = if (estadoActual.nombre.isBlank())
                "Campo obligatorio"
            else null,

            tipo = if (estadoActual.tipo.isBlank())
                "Campo obligatorio"
            else null,

            comuna = if (estadoActual.comuna.isBlank())
                "Campo obligatorio"
            else null,

            region = if (estadoActual.region.isBlank())
                "Campo obligatorio"
            else null
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.tipo,
            errores.rubro,
            errores.comuna,
            errores.region,
            errores.sitioWeb,
            errores.publico,
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
        val organizacion = OrganizacionEntity(
            id = a.id,
            nombre = a.nombre,
            tipo = a.tipo,
            rubro = a.rubro,
            comuna = a.comuna,
            region = a.region,
            sitioWeb = a.sitioWeb,
            publico = a.publico,
            observaciones = a.observaciones
        )

        viewModelScope.launch {
            val id = repository.guardar(organizacion)
            _estado.update { it.copy(id = id) }
            onListo()
        }
    }
}
