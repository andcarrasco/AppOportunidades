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

class OrganizacionViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getDatabase(app).organizacionDao()
    private val _estado = MutableStateFlow(OrganizacionUiState())
    val estado: StateFlow<OrganizacionUiState> = _estado
    private val _registros = MutableStateFlow<List<OrganizacionEntity>>(emptyList())
    val registros: StateFlow<List<OrganizacionEntity>> = _registros
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

    fun nuevo() {
        _estado.value = OrganizacionUiState()
        _mensaje.value = ""
    }

    fun cargar(registro: OrganizacionEntity) {
        _estado.value =
            OrganizacionUiState(
                id = registro.id,
                nombre = registro.nombre,
                tipo = registro.tipo,
                rubro = registro.rubro,
                comuna = registro.comuna,
                region = registro.region,
                sitioWeb = registro.sitioWeb,
                publico = registro.publico,
                observaciones = registro.observaciones,
            )
        _mensaje.value = ""
    }

    // Nombre de la organización asociada a un registro, para mostrarlo en otras pantallas.
    fun nombre(lista: List<OrganizacionEntity>, organizacionId: String): String =
        lista.find { it.id.toString() == organizacionId }?.nombre ?: "Sin organización"

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onTipoChange(valor: String) {
        _estado.update { it.copy(tipo = valor, errores = it.errores.copy(tipo = null)) }
    }

    fun onRubroChange(valor: String) {
        _estado.update { it.copy(rubro = valor, errores = it.errores.copy(rubro = null)) }
    }

    fun onComunaChange(valor: String) {
        _estado.update { it.copy(comuna = valor, errores = it.errores.copy(comuna = null)) }
    }

    fun onRegionChange(valor: String) {
        _estado.update { it.copy(region = valor, errores = it.errores.copy(region = null)) }
    }

    fun onSitioWebChange(valor: String) {
        _estado.update { it.copy(sitioWeb = valor, errores = it.errores.copy(sitioWeb = null)) }
    }

    fun onPublicoChange(valor: String) {
        _estado.update { it.copy(publico = valor, errores = it.errores.copy(publico = null)) }
    }

    fun onObservacionesChange(valor: String) {
        _estado.update {
            it.copy(observaciones = valor, errores = it.errores.copy(observaciones = null))
        }
    }

    // Validación global del formulario
    fun validarFormulario(): Boolean {
        val errores = validarDatos(_estado.value)
        val hayErrores =
            listOfNotNull(
                    errores.nombre,
                    errores.tipo,
                    errores.rubro,
                    errores.comuna,
                    errores.region,
                    errores.sitioWeb,
                    errores.publico,
                    errores.observaciones,
                )
                .isNotEmpty()
        _estado.update { it.copy(errores = errores) }
        return !hayErrores
    }

    // Guarda en Room. La pantalla ejecuta validarFormulario() antes de llamar a guardar().
    fun guardar(onListo: () -> Unit) {
        if (_guardando.value) return
        val a = _estado.value
        _guardando.value = true
        _mensaje.value = ""
        viewModelScope.launch {
            try {
                val registro =
                    OrganizacionEntity(
                        id = a.id,
                        nombre = a.nombre.trim(),
                        tipo = a.tipo.trim(),
                        rubro = a.rubro.trim(),
                        comuna = a.comuna.trim(),
                        region = a.region.trim(),
                        sitioWeb = a.sitioWeb.trim(),
                        publico = a.publico.trim(),
                        observaciones = a.observaciones.trim(),
                    )
                val guardadoId =
                    if (a.id == 0L) dao.insertar(registro)
                    else {
                        dao.modificar(
                            id = a.id,
                            nombre = registro.nombre,
                            tipo = registro.tipo,
                            rubro = registro.rubro,
                            comuna = registro.comuna,
                            region = registro.region,
                            sitioWeb = registro.sitioWeb,
                            publico = registro.publico,
                            observaciones = registro.observaciones,
                        )
                        a.id
                    }

                _estado.update { it.copy(id = guardadoId) }
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
        fun validarDatos(estadoActual: OrganizacionUiState): OrganizacionErrores =
            OrganizacionErrores(
                nombre = if (estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
                tipo = if (estadoActual.tipo.isBlank()) "Campo obligatorio" else null,
                comuna = if (estadoActual.comuna.isBlank()) "Campo obligatorio" else null,
                region = if (estadoActual.region.isBlank()) "Campo obligatorio" else null,
            )
    }
}
