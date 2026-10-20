package com.example.oportunidades.ui.screen
import androidx.compose.runtime.*
import com.example.oportunidades.model.*
import com.example.oportunidades.model.data.local.OrganizacionEntity
import com.example.oportunidades.viewmodel.OrganizacionViewModel
import com.example.oportunidades.ui.components.*

@Composable
fun OrganizacionScreen(viewModel: OrganizacionViewModel, onGuardado: () -> Unit, onVolver: () -> Unit) {
    val estado by viewModel.estado.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    Formulario("Organizacion", guardando, mensaje, onVolver, { viewModel.guardar(onGuardado) }) {
        CampoTexto("Nombre ficticio", estado.nombre, estado.errores.nombre, viewModel::onNombreChange)
        Selector("Tipo de organización", estado.tipo, Catalogos.organizaciones.map { it to it }, estado.errores.tipo, viewModel::onTipoChange)
        CampoTexto("Rubro", estado.rubro, estado.errores.rubro, viewModel::onRubroChange)
        CampoTexto("Comuna", estado.comuna, estado.errores.comuna, viewModel::onComunaChange)
        CampoTexto("Región", estado.region, estado.errores.region, viewModel::onRegionChange)
        CampoTexto("Sitio web público o ficticio", estado.sitioWeb, estado.errores.sitioWeb, viewModel::onSitioWebChange)
        CampoTexto("Público asociado", estado.publico, estado.errores.publico, viewModel::onPublicoChange)
        CampoTexto("Observaciones", estado.observaciones, estado.errores.observaciones, viewModel::onObservacionesChange)
    }
}
