package com.example.oportunidades.ui.screen
import androidx.compose.runtime.*
import com.example.oportunidades.model.*
import com.example.oportunidades.model.data.local.OrganizacionEntity
import com.example.oportunidades.viewmodel.ResultadoViewModel
import com.example.oportunidades.ui.components.*

@Composable
fun ResultadoScreen(viewModel: ResultadoViewModel, onGuardado: () -> Unit, onVolver: () -> Unit) {
    val estado by viewModel.estado.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    Formulario("Resultado", guardando, mensaje, onVolver, { viewModel.guardar(onGuardado) }) {
        CampoTexto("Fecha de realización (AAAA-MM-DD)", estado.fecha, estado.errores.fecha, viewModel::onFechaChange)
        Selector("Resultado final", estado.estado, listOf("Activación realizada", "Activación cancelada", "Oportunidad cerrada").map { it to it }, estado.errores.estado, viewModel::onEstadoChange)
        CampoTexto("Asistentes estimados", estado.asistentes, estado.errores.asistentes, viewModel::onAsistentesChange)
        CampoTexto("Contactos generados", estado.contactos, estado.errores.contactos, viewModel::onContactosChange)
        CampoTexto("Personas interesadas", estado.interesados, estado.errores.interesados, viewModel::onInteresadosChange)
        CampoTexto("Inscripciones", estado.inscripciones, estado.errores.inscripciones, viewModel::onInscripcionesChange)
        CampoTexto("Observaciones y aprendizajes", estado.observaciones, estado.errores.observaciones, viewModel::onObservacionesChange)
    }
}
