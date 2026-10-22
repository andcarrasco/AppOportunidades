package com.example.oportunidades.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.oportunidades.model.*
import com.example.oportunidades.model.data.local.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OportunidadViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getDatabase(app)
    private val dao = db.oportunidadDao()
    private val _estado = MutableStateFlow(OportunidadUiState())
    val estado: StateFlow<OportunidadUiState> = _estado
    private val _registros = MutableStateFlow<List<OportunidadEntity>>(emptyList())
    val registros: StateFlow<List<OportunidadEntity>> = _registros
    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando
    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje
    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando
    init {
        viewModelScope.launch {
            try { dao.obtenerTodos().collect { _registros.value = it; _cargando.value = false } }
            catch (e: Exception) { _cargando.value = false; _mensaje.value = "No se pudieron cargar los registros" }
        }
    }
    fun nuevo() { _estado.value = OportunidadUiState(estado = "Evento identificado", prioridad = "Media", fechaDeteccion = Fechas.hoy(), responsable = "Ejecutivo Demo", costos = "0", asistentes = "0"); _mensaje.value = "" }
    fun cargar(registro: OportunidadEntity) {
        _estado.value = OportunidadUiState(id = registro.id, nombre = registro.nombre, tipo = registro.tipo, descripcion = registro.descripcion, fuente = registro.fuente, fechaDeteccion = registro.fechaDeteccion, responsable = registro.responsable, prioridad = registro.prioridad, potencial = registro.potencial, estado = registro.estado, organizacionId = registro.organizacionId, fechaInicio = registro.fechaInicio, fechaTermino = registro.fechaTermino, horario = registro.horario, comuna = registro.comuna, region = registro.region, ubicacion = registro.ubicacion, publico = registro.publico, asistentes = registro.asistentes, condiciones = registro.condiciones, requisitos = registro.requisitos, costos = registro.costos, espacios = registro.espacios, observaciones = registro.observaciones)
        _mensaje.value = ""
    }
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }
    fun onTipoChange(valor: String) {
        _estado.update { it.copy(tipo = valor, errores = it.errores.copy(tipo = null)) }
    }
    fun onDescripcionChange(valor: String) {
        _estado.update { it.copy(descripcion = valor, errores = it.errores.copy(descripcion = null)) }
    }
    fun onFuenteChange(valor: String) {
        _estado.update { it.copy(fuente = valor, errores = it.errores.copy(fuente = null)) }
    }
    fun onFechaDeteccionChange(valor: String) {
        _estado.update { it.copy(fechaDeteccion = valor, errores = it.errores.copy(fechaDeteccion = null)) }
    }
    fun onResponsableChange(valor: String) {
        _estado.update { it.copy(responsable = valor, errores = it.errores.copy(responsable = null)) }
    }
    fun onPrioridadChange(valor: String) {
        _estado.update { it.copy(prioridad = valor, errores = it.errores.copy(prioridad = null)) }
    }
    fun onPotencialChange(valor: String) {
        _estado.update { it.copy(potencial = valor, errores = it.errores.copy(potencial = null)) }
    }
    fun onEstadoChange(valor: String) {
        _estado.update { it.copy(estado = valor, errores = it.errores.copy(estado = null)) }
    }
    fun onOrganizacionIdChange(valor: String) {
        _estado.update { it.copy(organizacionId = valor, errores = it.errores.copy(organizacionId = null)) }
    }
    fun onFechaInicioChange(valor: String) {
        _estado.update { it.copy(fechaInicio = valor, errores = it.errores.copy(fechaInicio = null)) }
    }
    fun onFechaTerminoChange(valor: String) {
        _estado.update { it.copy(fechaTermino = valor, errores = it.errores.copy(fechaTermino = null)) }
    }
    fun onHorarioChange(valor: String) {
        _estado.update { it.copy(horario = valor, errores = it.errores.copy(horario = null)) }
    }
    fun onComunaChange(valor: String) {
        _estado.update { it.copy(comuna = valor, errores = it.errores.copy(comuna = null)) }
    }
    fun onRegionChange(valor: String) {
        _estado.update { it.copy(region = valor, errores = it.errores.copy(region = null)) }
    }
    fun onUbicacionChange(valor: String) {
        _estado.update { it.copy(ubicacion = valor, errores = it.errores.copy(ubicacion = null)) }
    }
    fun onPublicoChange(valor: String) {
        _estado.update { it.copy(publico = valor, errores = it.errores.copy(publico = null)) }
    }
    fun onAsistentesChange(valor: String) {
        _estado.update { it.copy(asistentes = valor, errores = it.errores.copy(asistentes = null)) }
    }
    fun onCondicionesChange(valor: String) {
        _estado.update { it.copy(condiciones = valor, errores = it.errores.copy(condiciones = null)) }
    }
    fun onRequisitosChange(valor: String) {
        _estado.update { it.copy(requisitos = valor, errores = it.errores.copy(requisitos = null)) }
    }
    fun onCostosChange(valor: String) {
        _estado.update { it.copy(costos = valor, errores = it.errores.copy(costos = null)) }
    }
    fun onEspaciosChange(valor: String) {
        _estado.update { it.copy(espacios = valor, errores = it.errores.copy(espacios = null)) }
    }
    fun onObservacionesChange(valor: String) {
        _estado.update { it.copy(observaciones = valor, errores = it.errores.copy(observaciones = null)) }
    }
    fun validarFormulario(): Boolean {
        val errores = validarDatos(_estado.value)
        _estado.update { it.copy(errores = errores) }
        return listOfNotNull(errores.nombre, errores.tipo, errores.descripcion, errores.fuente, errores.fechaDeteccion, errores.responsable, errores.prioridad, errores.potencial, errores.estado, errores.organizacionId, errores.fechaInicio, errores.fechaTermino, errores.horario, errores.comuna, errores.region, errores.ubicacion, errores.publico, errores.asistentes, errores.condiciones, errores.requisitos, errores.costos, errores.espacios, errores.observaciones).isEmpty()
    }
    fun guardar(onListo: () -> Unit) {
        if (_guardando.value || !validarFormulario()) return
        val a = _estado.value
        _guardando.value = true
        _mensaje.value = ""
        viewModelScope.launch {
            try {

                if (db.organizacionDao().obtener(a.organizacionId.toLongOrNull() ?: 0) == null) {
                    _mensaje.value = "Selecciona una organización registrada"; return@launch
                }

                val registro = OportunidadEntity(id = a.id, nombre = a.nombre.trim(), tipo = a.tipo.trim(), descripcion = a.descripcion.trim(), fuente = a.fuente.trim(), fechaDeteccion = a.fechaDeteccion.trim(), responsable = a.responsable.trim(), prioridad = a.prioridad.trim(), potencial = a.potencial.trim(), estado = a.estado.trim(), organizacionId = a.organizacionId.trim(), fechaInicio = a.fechaInicio.trim(), fechaTermino = a.fechaTermino.trim(), horario = a.horario.trim(), comuna = a.comuna.trim(), region = a.region.trim(), ubicacion = a.ubicacion.trim(), publico = a.publico.trim(), asistentes = a.asistentes.trim(), condiciones = a.condiciones.trim(), requisitos = a.requisitos.trim(), costos = a.costos.trim(), espacios = a.espacios.trim(), observaciones = a.observaciones.trim(), actualizacion = Fechas.hoy() + " " + Fechas.hora())
                val guardadoId = db.withTransaction {
                    val anterior = if (a.id == 0L) null else dao.obtener(a.id)
                    val id = if (a.id == 0L) dao.insertar(registro) else {
                        dao.modificar(a.id, registro.nombre, registro.tipo, registro.descripcion, registro.fuente, registro.fechaDeteccion, registro.responsable, registro.prioridad, registro.potencial, registro.estado, registro.organizacionId, registro.fechaInicio, registro.fechaTermino, registro.horario, registro.comuna, registro.region, registro.ubicacion, registro.publico, registro.asistentes, registro.condiciones, registro.requisitos, registro.costos, registro.espacios, registro.observaciones, registro.actualizacion); a.id
                    }
                    if (anterior == null || anterior.estado != registro.estado) {
                        db.seguimientoDao().insertar(SeguimientoEntity(
                            oportunidadId = id.toString(), responsable = registro.responsable,
                            tipo = "Cambio de estado", fecha = Fechas.hoy(), hora = Fechas.hora(),
                            resultado = if (anterior == null) "Registro: ${registro.estado}" else "${anterior.estado} → ${registro.estado}"
                        ))
                    }
                    id
                }

                _estado.update { it.copy(id = guardadoId) }
                _mensaje.value = "Guardado correctamente"
                onListo()
            } catch (e: Exception) { _mensaje.value = "No se pudo completar el guardado. Revisa los datos e intenta nuevamente." }
            finally { _guardando.value = false }
        }
    }
    companion object {
        fun validarDatos(a: OportunidadUiState): OportunidadErrores = OportunidadErrores(
            nombre = if (a.nombre.isBlank()) "Campo obligatorio" else null,
            tipo = if (a.tipo.isBlank()) "Campo obligatorio" else null,
            fuente = if (a.fuente.isBlank()) "Campo obligatorio" else null,
            responsable = if (a.responsable.isBlank()) "Campo obligatorio" else null,
            prioridad = if (a.prioridad.isBlank()) "Campo obligatorio" else null,
            potencial = if (a.potencial.isBlank()) "Campo obligatorio" else null,
            estado = if (a.estado.isBlank()) "Campo obligatorio" else null,
            organizacionId = if (a.organizacionId.isBlank()) "Campo obligatorio" else null,
            comuna = if (a.comuna.isBlank()) "Campo obligatorio" else null,
            region = if (a.region.isBlank()) "Campo obligatorio" else null,
            publico = if (a.publico.isBlank()) "Campo obligatorio" else null,
            fechaDeteccion = if (!Fechas.valida(a.fechaDeteccion)) "Usa AAAA-MM-DD y una fecha válida" else null,
            fechaInicio = if (!Fechas.valida(a.fechaInicio)) "Usa AAAA-MM-DD y una fecha válida" else null,
            fechaTermino = if (!Fechas.valida(a.fechaTermino)) "Usa AAAA-MM-DD y una fecha válida" else if (Fechas.valida(a.fechaInicio) && a.fechaTermino < a.fechaInicio) "El término no puede ser anterior al inicio" else null,
            horario = if (!Fechas.horaValida(a.horario)) "Usa HH:mm y una hora válida" else null,
            asistentes = if (a.asistentes.isNotBlank() && (a.asistentes.toIntOrNull() == null || (a.asistentes.toIntOrNull() ?: -1) < 0)) "Ingresa un entero no negativo" else null,
            costos = if (a.costos.toDoubleOrNull() == null || !a.costos.toDouble().isFinite() || a.costos.toDouble() < 0) "Ingresa un costo no negativo" else null
        )
    }
}
