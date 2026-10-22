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
        viewModelScope.launch { try { db.oportunidadDao().obtenerTodos().collect { _oportunidades.value = it; _cargando.value = false } } catch (e: Exception) { _cargando.value = false; _error.value = "No se pudieron cargar las oportunidades" } }
        viewModelScope.launch { try { db.seguimientoDao().obtenerTodos().collect { _seguimientos.value = it } } catch (e: Exception) { _error.value = "No se pudo cargar el historial" } }
        viewModelScope.launch { try { db.resultadoDao().obtenerTodos().collect { _resultados.value = it } } catch (e: Exception) { _error.value = "No se pudieron cargar los resultados" } }
    }
    fun cambiarFiltros(valor: FiltrosUiState) { _filtros.value = valor }
    fun limpiarFiltros() { _filtros.value = FiltrosUiState() }
    fun errorFecha(valor: String): String? = if (valor.isNotBlank() && !Fechas.valida(valor)) "Usa una fecha válida AAAA-MM-DD" else null
    fun filtrar(lista: List<OportunidadEntity>, f: FiltrosUiState): List<OportunidadEntity> = lista.filter {
        it.nombre.contains(f.nombre, true) && (f.fecha.isBlank() || it.fechaInicio == f.fecha) &&
        (f.estado.isBlank() || it.estado == f.estado) && (f.tipo.isBlank() || it.tipo == f.tipo) &&
        it.comuna.contains(f.comuna, true) && it.region.contains(f.region, true) &&
        (f.prioridad.isBlank() || it.prioridad == f.prioridad) && it.responsable.contains(f.responsable, true) &&
        (f.organizacionId.isBlank() || it.organizacionId == f.organizacionId)
    }
    fun avisos(lista: List<OportunidadEntity>, gestiones: List<SeguimientoEntity>, cierres: List<ResultadoEntity>): List<Pair<OportunidadEntity, String>> {
        val avisos = mutableListOf<Pair<OportunidadEntity, String>>()
        lista.filter { it.estado != "Oportunidad descartada" && cierres.none { r -> r.oportunidadId == it.id.toString() } }.forEach { o ->
            val ultima = gestiones
                .filter { it.oportunidadId == o.id.toString() && it.tipo != "Cambio de estado" }
                .maxWithOrNull(compareBy<SeguimientoEntity> { it.fecha }.thenBy { it.hora }.thenBy { it.id })
            if (Fechas.proxima(o.fechaInicio)) avisos.add(o to "Evento próximo: ${o.fechaInicio}")
            if (o.estado == "Evento identificado" || o.estado == "Contacto pendiente") avisos.add(o to "Contacto pendiente")
            if (o.estado == "Propuesta enviada" || o.estado == "En espera de respuesta") avisos.add(o to "Propuesta sin respuesta")
            if (ultima == null) avisos.add(o to "Sin acciones de seguimiento")
            else if (ultima.proximaFecha.isNotBlank()) {
                val tipo = if (ultima.proximaFecha < Fechas.hoy()) "Seguimiento atrasado" else "Seguimiento pendiente"
                avisos.add(o to "$tipo: ${ultima.proximaFecha} · ${ultima.proximaAccion}")
            }
        }
        return avisos
    }
}
