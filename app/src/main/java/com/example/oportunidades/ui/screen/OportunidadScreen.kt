package com.example.oportunidades.ui.screen
import androidx.compose.runtime.*
import com.example.oportunidades.model.*
import com.example.oportunidades.model.data.local.OrganizacionEntity
import com.example.oportunidades.viewmodel.OportunidadViewModel
import com.example.oportunidades.ui.components.*

@Composable
fun OportunidadScreen(viewModel: OportunidadViewModel, organizaciones: List<OrganizacionEntity>, onGuardado: () -> Unit, onVolver: () -> Unit) {
    val estado by viewModel.estado.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    Formulario("Oportunidad", guardando, mensaje, onVolver, { viewModel.guardar(onGuardado) }) {
        CampoTexto("Nombre de la oportunidad", estado.nombre, estado.errores.nombre, viewModel::onNombreChange)
        Selector("Tipo de evento", estado.tipo, Catalogos.eventos.map { it to it }, estado.errores.tipo, viewModel::onTipoChange)
        CampoTexto("Descripción", estado.descripcion, estado.errores.descripcion, viewModel::onDescripcionChange)
        CampoTexto("Fuente de información", estado.fuente, estado.errores.fuente, viewModel::onFuenteChange)
        CampoTexto("Fecha de detección (AAAA-MM-DD)", estado.fechaDeteccion, estado.errores.fechaDeteccion, viewModel::onFechaDeteccionChange)
        CampoTexto("Responsable ficticio", estado.responsable, estado.errores.responsable, viewModel::onResponsableChange)
        Selector("Prioridad", estado.prioridad, Catalogos.prioridades.map { it to it }, estado.errores.prioridad, viewModel::onPrioridadChange)
        Selector("Potencial comercial", estado.potencial, listOf("Bajo", "Medio", "Alto").map { it to it }, estado.errores.potencial, viewModel::onPotencialChange)
        Selector("Estado de gestión", estado.estado, Catalogos.estados.map { it to it }, estado.errores.estado, viewModel::onEstadoChange)
        Selector("Organización", estado.organizacionId, organizaciones.map { it.id.toString() to it.nombre }, estado.errores.organizacionId, viewModel::onOrganizacionIdChange)
        CampoTexto("Fecha de inicio (AAAA-MM-DD)", estado.fechaInicio, estado.errores.fechaInicio, viewModel::onFechaInicioChange)
        CampoTexto("Fecha de término (AAAA-MM-DD)", estado.fechaTermino, estado.errores.fechaTermino, viewModel::onFechaTerminoChange)
        CampoTexto("Horario de inicio (HH:mm)", estado.horario, estado.errores.horario, viewModel::onHorarioChange)
        CampoTexto("Comuna", estado.comuna, estado.errores.comuna, viewModel::onComunaChange)
        CampoTexto("Región", estado.region, estado.errores.region, viewModel::onRegionChange)
        CampoTexto("Ubicación general del evento", estado.ubicacion, estado.errores.ubicacion, viewModel::onUbicacionChange)
        CampoTexto("Público objetivo", estado.publico, estado.errores.publico, viewModel::onPublicoChange)
        CampoTexto("Asistentes estimados", estado.asistentes, estado.errores.asistentes, viewModel::onAsistentesChange)
        CampoTexto("Condiciones de participación", estado.condiciones, estado.errores.condiciones, viewModel::onCondicionesChange)
        CampoTexto("Requisitos", estado.requisitos, estado.errores.requisitos, viewModel::onRequisitosChange)
        CampoTexto("Costos referenciales ficticios", estado.costos, estado.errores.costos, viewModel::onCostosChange)
        CampoTexto("Espacios disponibles", estado.espacios, estado.errores.espacios, viewModel::onEspaciosChange)
        CampoTexto("Observaciones", estado.observaciones, estado.errores.observaciones, viewModel::onObservacionesChange)
    }
}
