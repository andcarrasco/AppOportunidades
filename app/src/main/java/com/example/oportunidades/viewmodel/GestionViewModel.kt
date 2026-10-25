package com.example.oportunidades.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.model.*
import com.example.oportunidades.model.data.local.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Lógica de consulta, filtros y avisos fuera de los componentes visuales.
class GestionViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getDatabase(app)
    private val _filtros = MutableStateFlow(FiltrosUiState())
    val filtros: StateFlow<FiltrosUiState> = _filtros
    private val _oportunidades = MutableStateFlow<List<OportunidadEntity>>(emptyList())
    val oportunidades: StateFlow<List<OportunidadEntity>> = _oportunidades
    private val _seguimientos = MutableStateFlow<List<SeguimientoEntity>>(emptyList())
    val seguimientos: StateFlow<List<SeguimientoEntity>> = _seguimientos
    private val _resultados = MutableStateFlow<List<ResultadoEntity>>(emptyList())
    val resultados: StateFlow<List<ResultadoEntity>> = _resultados
    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error
    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    init {
        viewModelScope.launch {
            try {
                db.oportunidadDao().obtenerTodos().collect {
                    _oportunidades.value = it
                    _cargando.value = false
                }
            } catch (e: Exception) {
                _cargando.value = false
                _error.value = "No se pudieron cargar las oportunidades"
            }
        }
        viewModelScope.launch {
            try {
                db.seguimientoDao().obtenerTodos().collect { _seguimientos.value = it }
            } catch (e: Exception) {
                _error.value = "No se pudo cargar el historial"
            }
        }
        viewModelScope.launch {
            try {
                db.resultadoDao().obtenerTodos().collect { _resultados.value = it }
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar los resultados"
            }
        }
    }

    fun cambiarFiltros(valor: FiltrosUiState) {
        _filtros.value = valor
    }

    fun limpiarFiltros() {
        _filtros.value = FiltrosUiState()
    }

    fun errorFecha(valor: String): String? =
        if (valor.isNotBlank() && !Fechas.valida(valor)) "Usa una fecha válida AAAA-MM-DD" else null

    fun filtrar(lista: List<OportunidadEntity>, filtros: FiltrosUiState): List<OportunidadEntity> =
        lista.filter {
            it.nombre.contains(filtros.nombre, true) &&
                (filtros.fecha.isBlank() || it.fechaInicio == filtros.fecha) &&
                (filtros.estado.isBlank() || it.estado == filtros.estado) &&
                (filtros.tipo.isBlank() || it.tipo == filtros.tipo) &&
                it.comuna.contains(filtros.comuna, true) &&
                it.region.contains(filtros.region, true) &&
                (filtros.prioridad.isBlank() || it.prioridad == filtros.prioridad) &&
                it.responsable.contains(filtros.responsable, true) &&
                (filtros.organizacionId.isBlank() || it.organizacionId == filtros.organizacionId)
        }

    fun avisos(
        lista: List<OportunidadEntity>,
        gestiones: List<SeguimientoEntity>,
        cierres: List<ResultadoEntity>,
    ): List<Pair<OportunidadEntity, String>> {
        val avisos = mutableListOf<Pair<OportunidadEntity, String>>()
        val hoy = Fechas.hoy()
        lista.forEach { oportunidad ->
            var tieneResultado = false
            cierres.forEach { resultado ->
                if (resultado.oportunidadId == oportunidad.id.toString()) {
                    tieneResultado = true
                }
            }

            if (oportunidad.estado != "Oportunidad descartada" && !tieneResultado) {
                val ultima = ultimaGestion(oportunidad.id.toString(), gestiones)
                if (Fechas.proxima(oportunidad.fechaInicio)) {
                    avisos.add(oportunidad to "Evento próximo: ${oportunidad.fechaInicio}")
                }
                if (
                    oportunidad.estado == "Evento identificado" ||
                        oportunidad.estado == "Contacto pendiente"
                ) {
                    avisos.add(oportunidad to "Contacto pendiente")
                }
                if (
                    oportunidad.estado == "Propuesta enviada" ||
                        oportunidad.estado == "En espera de respuesta"
                ) {
                    avisos.add(oportunidad to "Propuesta sin respuesta")
                }
                if (ultima == null) {
                    avisos.add(oportunidad to "Sin acciones de seguimiento")
                } else if (ultima.proximaFecha.isNotBlank()) {
                    val tipo =
                        if (ultima.proximaFecha < hoy) {
                            "Seguimiento atrasado"
                        } else {
                            "Seguimiento pendiente"
                        }
                    avisos.add(
                        oportunidad to "$tipo: ${ultima.proximaFecha} · ${ultima.proximaAccion}"
                    )
                }
            }
        }
        return avisos
    }

    companion object {
        fun ultimaGestion(
            oportunidadId: String,
            gestiones: List<SeguimientoEntity>,
        ): SeguimientoEntity? {
            var ultima: SeguimientoEntity? = null

            // Registrar una gestión antigua no la convierte en la más reciente.
            gestiones.forEach { gestion ->
                if (gestion.oportunidadId == oportunidadId && gestion.tipo != "Cambio de estado") {
                    val anterior = ultima
                    if (anterior == null || gestion.fecha > anterior.fecha) {
                        ultima = gestion
                    } else if (gestion.fecha == anterior.fecha) {
                        if (gestion.hora > anterior.hora) {
                            ultima = gestion
                        } else if (gestion.hora == anterior.hora && gestion.id > anterior.id) {
                            ultima = gestion
                        }
                    }
                }
            }
            return ultima
        }
    }
}
