package com.example.oportunidades.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.model.Fechas
import com.example.oportunidades.model.FiltrosErrores
import com.example.oportunidades.model.FiltrosUiState
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.model.data.local.ResultadoEntity
import com.example.oportunidades.model.data.local.SeguimientoEntity
import com.example.oportunidades.repository.OportunidadRepository
import com.example.oportunidades.repository.ResultadoRepository
import com.example.oportunidades.repository.SeguimientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Consulta, filtros y avisos de las oportunidades
class GestionViewModel(
    private val oportunidadRepository: OportunidadRepository = OportunidadRepository(),
    private val seguimientoRepository: SeguimientoRepository = SeguimientoRepository(),
    private val resultadoRepository: ResultadoRepository = ResultadoRepository()
) : ViewModel() {

    // Estado interno mutable de los filtros
    private val _filtros = MutableStateFlow(FiltrosUiState())

    // Estado expuesto para la UI
    val filtros: StateFlow<FiltrosUiState> = _filtros

    // Datos guardados en Room
    private val _oportunidades = MutableStateFlow<List<OportunidadEntity>>(emptyList())
    val oportunidades: StateFlow<List<OportunidadEntity>> = _oportunidades

    private val _seguimientos = MutableStateFlow<List<SeguimientoEntity>>(emptyList())
    val seguimientos: StateFlow<List<SeguimientoEntity>> = _seguimientos

    private val _resultados = MutableStateFlow<List<ResultadoEntity>>(emptyList())
    val resultados: StateFlow<List<ResultadoEntity>> = _resultados

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    init {
        viewModelScope.launch {
            oportunidadRepository.obtenerTodos().collect { lista ->
                _oportunidades.value = lista
                _cargando.value = false
            }
        }
        viewModelScope.launch {
            seguimientoRepository.obtenerTodos().collect { lista ->
                _seguimientos.value = lista
            }
        }
        viewModelScope.launch {
            resultadoRepository.obtenerTodos().collect { lista ->
                _resultados.value = lista
            }
        }
    }

    // Actualiza los filtros y valida la fecha, igual que en los formularios
    fun onFiltrosChange(valor: FiltrosUiState) {
        _filtros.update {
            valor.copy(
                errores = FiltrosErrores(
                    fecha = if (valor.fecha.isNotBlank() && !Fechas.valida(valor.fecha))
                        "Usa una fecha válida AAAA-MM-DD"
                    else null
                )
            )
        }
    }

    fun limpiarFiltros() {
        _filtros.value = FiltrosUiState()
    }

    // Oportunidades que cumplen con todos los filtros
    fun filtrar(lista: List<OportunidadEntity>, filtros: FiltrosUiState): List<OportunidadEntity> {
        return lista.filter {
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
    }

    // Avisos de gestión de las oportunidades que siguen abiertas
    fun avisos(
        lista: List<OportunidadEntity>,
        gestiones: List<SeguimientoEntity>,
        cierres: List<ResultadoEntity>
    ): List<Pair<OportunidadEntity, String>> {
        val avisos = mutableListOf<Pair<OportunidadEntity, String>>()
        val hoy = Fechas.hoy()

        lista.forEach { oportunidad ->
            val tieneResultado = cierres.any { it.oportunidadId == oportunidad.id.toString() }

            if (oportunidad.estado != "Oportunidad descartada" && !tieneResultado) {
                val ultima = ultimaGestion(oportunidad.id.toString(), gestiones)

                if (Fechas.proxima(oportunidad.fechaInicio)) {
                    avisos.add(oportunidad to "Evento próximo: ${oportunidad.fechaInicio}")
                }
                if (oportunidad.estado == "Evento identificado" ||
                    oportunidad.estado == "Contacto pendiente"
                ) {
                    avisos.add(oportunidad to "Contacto pendiente")
                }
                if (oportunidad.estado == "Propuesta enviada" ||
                    oportunidad.estado == "En espera de respuesta"
                ) {
                    avisos.add(oportunidad to "Propuesta sin respuesta")
                }
                if (ultima == null) {
                    avisos.add(oportunidad to "Sin acciones de seguimiento")
                } else if (ultima.proximaFecha.isNotBlank()) {
                    val tipo = if (ultima.proximaFecha < hoy)
                        "Seguimiento atrasado"
                    else "Seguimiento pendiente"
                    avisos.add(oportunidad to "$tipo: ${ultima.proximaFecha} · ${ultima.proximaAccion}")
                }
            }
        }
        return avisos
    }

    // Última gestión registrada de una oportunidad (sin contar los cambios de estado)
    fun ultimaGestion(oportunidadId: String, gestiones: List<SeguimientoEntity>): SeguimientoEntity? {
        var ultima: SeguimientoEntity? = null

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
