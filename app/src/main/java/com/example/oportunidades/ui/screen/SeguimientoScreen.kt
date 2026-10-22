package com.example.oportunidades.ui.screen
import androidx.compose.runtime.*
import com.example.oportunidades.model.*
import com.example.oportunidades.model.data.local.OrganizacionEntity
import com.example.oportunidades.viewmodel.SeguimientoViewModel
import com.example.oportunidades.ui.components.*

@Composable
fun SeguimientoScreen(viewModel: SeguimientoViewModel, onGuardado: () -> Unit, onVolver: () -> Unit) {
    val estado by viewModel.estado.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    Formulario("Seguimiento", guardando, mensaje, onVolver, { viewModel.guardar(onGuardado) }) {
        CampoTexto("Responsable ficticio", estado.responsable, estado.errores.responsable, viewModel::onResponsableChange)
        Selector("Tipo de acción", estado.tipo, Catalogos.acciones.map { it to it }, estado.errores.tipo, viewModel::onTipoChange)
        CampoTexto("Fecha de la acción (AAAA-MM-DD)", estado.fecha, estado.errores.fecha, viewModel::onFechaChange)
        CampoTexto("Hora (HH:mm)", estado.hora, estado.errores.hora, viewModel::onHoraChange)
        CampoTexto("Resultado de la gestión", estado.resultado, estado.errores.resultado, viewModel::onResultadoChange)
        CampoTexto("Próxima acción", estado.proximaAccion, estado.errores.proximaAccion, viewModel::onProximaAccionChange)
        CampoTexto("Fecha de próximo seguimiento (AAAA-MM-DD)", estado.proximaFecha, estado.errores.proximaFecha, viewModel::onProximaFechaChange)
        CampoTexto("Observaciones", estado.observaciones, estado.errores.observaciones, viewModel::onObservacionesChange)
    }
}
