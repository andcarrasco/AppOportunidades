package com.example.oportunidades.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.oportunidades.model.Catalogos
import com.example.oportunidades.model.Fechas
import com.example.oportunidades.model.OportunidadErrores
import com.example.oportunidades.model.OportunidadUiState
import com.example.oportunidades.model.data.local.AppDatabase
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.model.data.local.OrganizacionEntity
import com.example.oportunidades.model.data.local.SeguimientoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OportunidadViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.getDatabase(app)
    private val dao = db.oportunidadDao()

    // Estado interno mutable
    private val _estado = MutableStateFlow(OportunidadUiState())

    // Estado expuesto para la UI
    val estado: StateFlow<OportunidadUiState> = _estado

    // Oportunidades guardadas en Room
    private val _registros = MutableStateFlow<List<OportunidadEntity>>(emptyList())
    val registros: StateFlow<List<OportunidadEntity>> = _registros

    // Ficha guardada de la oportunidad seleccionada, tal como está en Room
    private val _ficha = MutableStateFlow<OportunidadEntity?>(null)
    val ficha: StateFlow<OportunidadEntity?> = _ficha

    init {
        viewModelScope.launch {
            dao.obtenerTodos().collect { lista ->
                _registros.value = lista
                actualizarFicha()
            }
        }
    }

    private fun actualizarFicha() {
        val id = _estado.value.id
        _ficha.value = if (id == 0L) null else _registros.value.find { it.id == id }
    }

    // Limpia el formulario con valores iniciales para una oportunidad nueva
    fun nuevo() {
        _estado.value = OportunidadUiState(
            estado = "Evento identificado",
            prioridad = "Media",
            fechaDeteccion = Fechas.hoy(),
            responsable = "Ejecutivo Demo",
            costos = "0",
            asistentes = "0"
        )
        actualizarFicha()
    }

    // Carga un registro guardado para editarlo
    fun cargar(registro: OportunidadEntity) {
        _estado.value = OportunidadUiState(
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
            observaciones = registro.observaciones
        )
        actualizarFicha()
    }

    // Vuelve a cargar en el formulario la ficha guardada, para editarla
    fun editarFicha() {
        _ficha.value?.let { cargar(it) }
    }

    // Prepara una oportunidad nueva con los datos leídos desde un código QR.
    // Los selectores solo aceptan valores del catálogo; el resto se completa en el formulario.
    fun cargarDesdeQr(campos: Map<String, String>, organizaciones: List<OrganizacionEntity>) {
        nuevo()

        fun catalogo(valor: String?, opciones: List<String>): String? {
            return opciones.find { it.equals(valor?.trim(), ignoreCase = true) }
        }

        val organizacion = organizaciones.find {
            it.nombre.equals(campos["organizacion"]?.trim(), ignoreCase = true)
        }

        _estado.update { a ->
            a.copy(
                nombre = campos["nombre"] ?: a.nombre,
                tipo = catalogo(campos["tipo"], Catalogos.eventos) ?: a.tipo,
                descripcion = campos["descripcion"] ?: a.descripcion,
                fuente = campos["fuente"] ?: "Código QR",
                fechaDeteccion = campos["fechaDeteccion"] ?: a.fechaDeteccion,
                responsable = campos["responsable"] ?: a.responsable,
                prioridad = catalogo(campos["prioridad"], Catalogos.prioridades) ?: a.prioridad,
                potencial = catalogo(campos["potencial"], Catalogos.potenciales) ?: a.potencial,
                estado = catalogo(campos["estado"], Catalogos.estados) ?: a.estado,
                organizacionId = organizacion?.id?.toString() ?: a.organizacionId,
                fechaInicio = campos["fechaInicio"] ?: a.fechaInicio,
                fechaTermino = campos["fechaTermino"] ?: campos["fechaInicio"] ?: a.fechaTermino,
                horario = campos["horario"] ?: a.horario,
                comuna = campos["comuna"] ?: a.comuna,
                region = campos["region"] ?: a.region,
                ubicacion = campos["ubicacion"] ?: a.ubicacion,
                publico = campos["publico"] ?: a.publico,
                asistentes = campos["asistentes"] ?: a.asistentes,
                condiciones = campos["condiciones"] ?: a.condiciones,
                requisitos = campos["requisitos"] ?: a.requisitos,
                costos = campos["costos"] ?: a.costos,
                espacios = campos["espacios"] ?: a.espacios,
                observaciones = campos["observaciones"] ?: a.observaciones
            )
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

    fun onDescripcionChange(valor: String) {
        _estado.update {
            it.copy(
                descripcion = valor,
                errores = it.errores.copy(descripcion = null)
            )
        }
    }

    fun onFuenteChange(valor: String) {
        _estado.update {
            it.copy(
                fuente = valor,
                errores = it.errores.copy(fuente = null)
            )
        }
    }

    fun onFechaDeteccionChange(valor: String) {
        _estado.update {
            it.copy(
                fechaDeteccion = valor,
                errores = it.errores.copy(fechaDeteccion = null)
            )
        }
    }

    fun onResponsableChange(valor: String) {
        _estado.update {
            it.copy(
                responsable = valor,
                errores = it.errores.copy(responsable = null)
            )
        }
    }

    fun onPrioridadChange(valor: String) {
        _estado.update {
            it.copy(
                prioridad = valor,
                errores = it.errores.copy(prioridad = null)
            )
        }
    }

    fun onPotencialChange(valor: String) {
        _estado.update {
            it.copy(
                potencial = valor,
                errores = it.errores.copy(potencial = null)
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

    fun onOrganizacionIdChange(valor: String) {
        _estado.update {
            it.copy(
                organizacionId = valor,
                errores = it.errores.copy(organizacionId = null)
            )
        }
    }

    fun onFechaInicioChange(valor: String) {
        _estado.update {
            it.copy(
                fechaInicio = valor,
                errores = it.errores.copy(fechaInicio = null)
            )
        }
    }

    fun onFechaTerminoChange(valor: String) {
        _estado.update {
            it.copy(
                fechaTermino = valor,
                errores = it.errores.copy(fechaTermino = null)
            )
        }
    }

    fun onHorarioChange(valor: String) {
        _estado.update {
            it.copy(
                horario = valor,
                errores = it.errores.copy(horario = null)
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

    fun onUbicacionChange(valor: String) {
        _estado.update {
            it.copy(
                ubicacion = valor,
                errores = it.errores.copy(ubicacion = null)
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

    fun onAsistentesChange(valor: String) {
        _estado.update {
            it.copy(
                asistentes = valor,
                errores = it.errores.copy(asistentes = null)
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

    fun onRequisitosChange(valor: String) {
        _estado.update {
            it.copy(
                requisitos = valor,
                errores = it.errores.copy(requisitos = null)
            )
        }
    }

    fun onCostosChange(valor: String) {
        _estado.update {
            it.copy(
                costos = valor,
                errores = it.errores.copy(costos = null)
            )
        }
    }

    fun onEspaciosChange(valor: String) {
        _estado.update {
            it.copy(
                espacios = valor,
                errores = it.errores.copy(espacios = null)
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
        val asistentes = estadoActual.asistentes.toIntOrNull()
        val costos = estadoActual.costos.toDoubleOrNull()

        val errores = OportunidadErrores(
            nombre = if (estadoActual.nombre.isBlank())
                "Campo obligatorio"
            else null,

            tipo = if (estadoActual.tipo.isBlank())
                "Campo obligatorio"
            else null,

            descripcion = if (estadoActual.descripcion.isBlank())
                "Campo obligatorio"
            else null,

            fuente = if (estadoActual.fuente.isBlank())
                "Campo obligatorio"
            else null,

            fechaDeteccion = if (!Fechas.valida(estadoActual.fechaDeteccion))
                "Usa AAAA-MM-DD y una fecha válida"
            else null,

            responsable = if (estadoActual.responsable.isBlank())
                "Campo obligatorio"
            else null,

            prioridad = if (estadoActual.prioridad.isBlank())
                "Campo obligatorio"
            else null,

            potencial = if (estadoActual.potencial !in Catalogos.potenciales)
                "Selecciona Bajo, Medio o Alto"
            else null,

            estado = if (estadoActual.estado.isBlank())
                "Campo obligatorio"
            else null,

            organizacionId = if (estadoActual.organizacionId.isBlank())
                "Campo obligatorio"
            else null,

            fechaInicio = if (!Fechas.valida(estadoActual.fechaInicio))
                "Usa AAAA-MM-DD y una fecha válida"
            else null,

            fechaTermino = if (!Fechas.valida(estadoActual.fechaTermino))
                "Usa AAAA-MM-DD y una fecha válida"
            else if (Fechas.valida(estadoActual.fechaInicio) &&
                estadoActual.fechaTermino < estadoActual.fechaInicio
            )
                "El término no puede ser anterior al inicio"
            else null,

            horario = if (!Fechas.horaValida(estadoActual.horario))
                "Usa HH:mm y una hora válida"
            else null,

            comuna = if (estadoActual.comuna.isBlank())
                "Campo obligatorio"
            else null,

            region = if (estadoActual.region.isBlank())
                "Campo obligatorio"
            else null,

            publico = if (estadoActual.publico.isBlank())
                "Campo obligatorio"
            else null,

            asistentes = if (estadoActual.asistentes.isNotBlank() &&
                (asistentes == null || asistentes < 0)
            )
                "Ingresa un número entero no negativo"
            else null,

            costos = if (costos == null || !costos.isFinite() || costos < 0)
                "Ingresa un costo no negativo"
            else null
        )

        val hayErrores = listOfNotNull(
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
            errores.observaciones
        ).isNotEmpty()

        _estado.update {
            it.copy(errores = errores)
        }

        return !hayErrores
    }

    // Guarda en Room: inserta si es nueva o modifica si ya existe.
    // Si cambia el estado de gestión, se registra en el historial.
    fun guardar(onListo: () -> Unit) {
        val a = _estado.value
        val oportunidad = OportunidadEntity(
            id = a.id,
            nombre = a.nombre,
            tipo = a.tipo,
            descripcion = a.descripcion,
            fuente = a.fuente,
            fechaDeteccion = a.fechaDeteccion,
            responsable = a.responsable,
            prioridad = a.prioridad,
            potencial = a.potencial,
            estado = a.estado,
            organizacionId = a.organizacionId,
            fechaInicio = a.fechaInicio,
            fechaTermino = a.fechaTermino,
            horario = a.horario,
            comuna = a.comuna,
            region = a.region,
            ubicacion = a.ubicacion,
            publico = a.publico,
            asistentes = a.asistentes,
            condiciones = a.condiciones,
            requisitos = a.requisitos,
            costos = a.costos,
            espacios = a.espacios,
            observaciones = a.observaciones,
            actualizacion = Fechas.hoy() + " " + Fechas.hora()
        )

        viewModelScope.launch {
            // La ficha y su historial se guardan juntos en una transacción
            val id = db.withTransaction {
                val anterior = if (a.id == 0L) null else dao.obtener(a.id)
                val guardadoId = if (a.id == 0L) {
                    dao.insertar(oportunidad)
                } else {
                    dao.modificar(oportunidad)
                    a.id
                }

                if (anterior == null || anterior.estado != oportunidad.estado) {
                    db.seguimientoDao().insertar(
                        SeguimientoEntity(
                            oportunidadId = guardadoId.toString(),
                            responsable = oportunidad.responsable,
                            tipo = "Cambio de estado",
                            fecha = Fechas.hoy(),
                            hora = Fechas.hora(),
                            resultado = if (anterior == null)
                                "Registro: ${oportunidad.estado}"
                            else "${anterior.estado} → ${oportunidad.estado}"
                        )
                    )
                }
                guardadoId
            }

            _estado.update { it.copy(id = id) }
            actualizarFicha()
            onListo()
        }
    }
}
