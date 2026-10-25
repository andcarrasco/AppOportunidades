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
        _estado.value =
            OportunidadUiState(
                estado = "Evento identificado",
                prioridad = "Media",
                fechaDeteccion = Fechas.hoy(),
                responsable = "Ejecutivo Demo",
                costos = "0",
                asistentes = "0",
            )
        _mensaje.value = ""
    }

    fun cargar(registro: OportunidadEntity) {
        _estado.value =
            OportunidadUiState(
                id = registro.id,
                nombre = registro.nombre,
                tipo = registro.tipo,
                descripcion = registro.descripcion,
                fuente = registro.fuente,
                fechaDeteccion = registro.fechaDeteccion,
                responsable = registro.responsable,
                prioridad = registro.prioridad,
                potencial = registro.potencial,
                estado = registro.estado,
                organizacionId = registro.organizacionId,
                fechaInicio = registro.fechaInicio,
                fechaTermino = registro.fechaTermino,
                horario = registro.horario,
                comuna = registro.comuna,
                region = registro.region,
                ubicacion = registro.ubicacion,
                publico = registro.publico,
                asistentes = registro.asistentes,
                condiciones = registro.condiciones,
                requisitos = registro.requisitos,
                costos = registro.costos,
                espacios = registro.espacios,
                observaciones = registro.observaciones,
            )
        _mensaje.value = ""
    }

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onTipoChange(valor: String) {
        _estado.update { it.copy(tipo = valor, errores = it.errores.copy(tipo = null)) }
    }

    fun onDescripcionChange(valor: String) {
        _estado.update {
            it.copy(descripcion = valor, errores = it.errores.copy(descripcion = null))
        }
    }

    fun onFuenteChange(valor: String) {
        _estado.update { it.copy(fuente = valor, errores = it.errores.copy(fuente = null)) }
    }

    fun onFechaDeteccionChange(valor: String) {
        _estado.update {
            it.copy(fechaDeteccion = valor, errores = it.errores.copy(fechaDeteccion = null))
        }
    }

    fun onResponsableChange(valor: String) {
        _estado.update {
            it.copy(responsable = valor, errores = it.errores.copy(responsable = null))
        }
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
        _estado.update {
            it.copy(organizacionId = valor, errores = it.errores.copy(organizacionId = null))
        }
    }

    fun onFechaInicioChange(valor: String) {
        _estado.update {
            it.copy(fechaInicio = valor, errores = it.errores.copy(fechaInicio = null))
        }
    }

    fun onFechaTerminoChange(valor: String) {
        _estado.update {
            it.copy(fechaTermino = valor, errores = it.errores.copy(fechaTermino = null))
        }
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
        _estado.update {
            it.copy(condiciones = valor, errores = it.errores.copy(condiciones = null))
        }
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
        _estado.update {
            it.copy(observaciones = valor, errores = it.errores.copy(observaciones = null))
        }
    }

    fun validarFormulario(): Boolean {
        val errores = validarDatos(_estado.value)
        _estado.update { it.copy(errores = errores) }
        return listOfNotNull(
                errores.nombre,
                errores.tipo,
                errores.descripcion,
                errores.fuente,
                errores.fechaDeteccion,
                errores.responsable,
                errores.prioridad,
                errores.potencial,
                errores.estado,
                errores.organizacionId,
                errores.fechaInicio,
                errores.fechaTermino,
                errores.horario,
                errores.comuna,
                errores.region,
                errores.ubicacion,
                errores.publico,
                errores.asistentes,
                errores.condiciones,
                errores.requisitos,
                errores.costos,
                errores.espacios,
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
                    db.organizacionDao().obtener(estadoActual.organizacionId.toLongOrNull() ?: 0) ==
                        null
                ) {
                    _mensaje.value = "Selecciona una organización registrada"
                    return@launch
                }

                val registro =
                    OportunidadEntity(
                        id = estadoActual.id,
                        nombre = estadoActual.nombre.trim(),
                        tipo = estadoActual.tipo.trim(),
                        descripcion = estadoActual.descripcion.trim(),
                        fuente = estadoActual.fuente.trim(),
                        fechaDeteccion = estadoActual.fechaDeteccion.trim(),
                        responsable = estadoActual.responsable.trim(),
                        prioridad = estadoActual.prioridad.trim(),
                        potencial = estadoActual.potencial.trim(),
                        estado = estadoActual.estado.trim(),
                        organizacionId = estadoActual.organizacionId.trim(),
                        fechaInicio = estadoActual.fechaInicio.trim(),
                        fechaTermino = estadoActual.fechaTermino.trim(),
                        horario = estadoActual.horario.trim(),
                        comuna = estadoActual.comuna.trim(),
                        region = estadoActual.region.trim(),
                        ubicacion = estadoActual.ubicacion.trim(),
                        publico = estadoActual.publico.trim(),
                        asistentes = estadoActual.asistentes.trim(),
                        condiciones = estadoActual.condiciones.trim(),
                        requisitos = estadoActual.requisitos.trim(),
                        costos = estadoActual.costos.trim(),
                        espacios = estadoActual.espacios.trim(),
                        observaciones = estadoActual.observaciones.trim(),
                        actualizacion = Fechas.hoy() + " " + Fechas.hora(),
                    )
                val guardadoId = guardarConHistorial(registro)

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

    // Room guarda la ficha y su historial juntos. Si algo falla, revierte ambos.
    private suspend fun guardarConHistorial(registro: OportunidadEntity): Long {
        return db.withTransaction {
            val anterior = if (registro.id == 0L) null else dao.obtener(registro.id)
            var id = registro.id

            if (id == 0L) {
                id = dao.insertar(registro)
            } else {
                modificar(registro)
            }

            if (anterior == null || anterior.estado != registro.estado) {
                var descripcion = "Registro: ${registro.estado}"
                if (anterior != null) {
                    descripcion = "${anterior.estado} → ${registro.estado}"
                }

                val cambio =
                    SeguimientoEntity(
                        oportunidadId = id.toString(),
                        responsable = registro.responsable,
                        tipo = "Cambio de estado",
                        fecha = Fechas.hoy(),
                        hora = Fechas.hora(),
                        resultado = descripcion,
                    )
                db.seguimientoDao().insertar(cambio)
            }

            id
        }
    }

    private suspend fun modificar(registro: OportunidadEntity) {
        dao.modificar(
            id = registro.id,
            nombre = registro.nombre,
            tipo = registro.tipo,
            descripcion = registro.descripcion,
            fuente = registro.fuente,
            fechaDeteccion = registro.fechaDeteccion,
            responsable = registro.responsable,
            prioridad = registro.prioridad,
            potencial = registro.potencial,
            estado = registro.estado,
            organizacionId = registro.organizacionId,
            fechaInicio = registro.fechaInicio,
            fechaTermino = registro.fechaTermino,
            horario = registro.horario,
            comuna = registro.comuna,
            region = registro.region,
            ubicacion = registro.ubicacion,
            publico = registro.publico,
            asistentes = registro.asistentes,
            condiciones = registro.condiciones,
            requisitos = registro.requisitos,
            costos = registro.costos,
            espacios = registro.espacios,
            observaciones = registro.observaciones,
            actualizacion = registro.actualizacion,
        )
    }

    companion object {
        fun validarDatos(estadoActual: OportunidadUiState): OportunidadErrores {
            val asistentes = estadoActual.asistentes.toIntOrNull()
            val costos = estadoActual.costos.toDoubleOrNull()

            return OportunidadErrores(
                nombre = if (estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
                tipo = if (estadoActual.tipo.isBlank()) "Campo obligatorio" else null,
                fuente = if (estadoActual.fuente.isBlank()) "Campo obligatorio" else null,
                responsable = if (estadoActual.responsable.isBlank()) "Campo obligatorio" else null,
                prioridad = if (estadoActual.prioridad.isBlank()) "Campo obligatorio" else null,
                potencial = if (estadoActual.potencial.isBlank()) "Campo obligatorio" else null,
                estado = if (estadoActual.estado.isBlank()) "Campo obligatorio" else null,
                organizacionId =
                    if (estadoActual.organizacionId.isBlank()) "Campo obligatorio" else null,
                comuna = if (estadoActual.comuna.isBlank()) "Campo obligatorio" else null,
                region = if (estadoActual.region.isBlank()) "Campo obligatorio" else null,
                publico = if (estadoActual.publico.isBlank()) "Campo obligatorio" else null,
                fechaDeteccion =
                    if (!Fechas.valida(estadoActual.fechaDeteccion))
                        "Usa AAAA-MM-DD y una fecha válida"
                    else null,
                fechaInicio =
                    if (!Fechas.valida(estadoActual.fechaInicio))
                        "Usa AAAA-MM-DD y una fecha válida"
                    else null,
                fechaTermino =
                    if (!Fechas.valida(estadoActual.fechaTermino))
                        "Usa AAAA-MM-DD y una fecha válida"
                    else if (
                        Fechas.valida(estadoActual.fechaInicio) &&
                            estadoActual.fechaTermino < estadoActual.fechaInicio
                    )
                        "El término no puede ser anterior al inicio"
                    else null,
                horario =
                    if (!Fechas.horaValida(estadoActual.horario)) "Usa HH:mm y una hora válida"
                    else null,
                asistentes =
                    if (
                        estadoActual.asistentes.isNotBlank() &&
                            (asistentes == null || asistentes < 0)
                    )
                        "Ingresa un entero no negativo"
                    else null,
                costos =
                    if (costos == null || !costos.isFinite() || costos < 0)
                        "Ingresa un costo no negativo"
                    else null,
            )
        }
    }
}
