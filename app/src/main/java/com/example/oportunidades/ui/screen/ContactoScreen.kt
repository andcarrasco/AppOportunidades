package com.example.oportunidades.ui.screen
import androidx.compose.runtime.*
import com.example.oportunidades.model.*
import com.example.oportunidades.model.data.local.OrganizacionEntity
import com.example.oportunidades.viewmodel.ContactoViewModel
import com.example.oportunidades.ui.components.*

@Composable
fun ContactoScreen(viewModel: ContactoViewModel, organizaciones: List<OrganizacionEntity>, onGuardado: () -> Unit, onVolver: () -> Unit) {
    val estado by viewModel.estado.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    Formulario("Contacto", guardando, mensaje, onVolver, { viewModel.guardar(onGuardado) }) {
        Selector("Organización", estado.organizacionId, organizaciones.map { it.id.toString() to it.nombre }, estado.errores.organizacionId, viewModel::onOrganizacionIdChange)
        CampoTexto("Nombre ficticio", estado.nombre, estado.errores.nombre, viewModel::onNombreChange)
        CampoTexto("Cargo", estado.cargo, estado.errores.cargo, viewModel::onCargoChange)
        Selector("Área", estado.area, Catalogos.areas.map { it to it }, estado.errores.area, viewModel::onAreaChange)
        CampoTexto("Correo ficticio", estado.correo, estado.errores.correo, viewModel::onCorreoChange)
        CampoTexto("Teléfono ficticio", estado.telefono, estado.errores.telefono, viewModel::onTelefonoChange)
        Selector("Medio preferente", estado.medio, listOf("Correo", "Teléfono", "Reunión").map { it to it }, estado.errores.medio, viewModel::onMedioChange)
        Selector("Estado del contacto", estado.estado, listOf("Pendiente", "Contactado", "Sin respuesta").map { it to it }, estado.errores.estado, viewModel::onEstadoChange)
        CampoTexto("Observaciones", estado.observaciones, estado.errores.observaciones, viewModel::onObservacionesChange)
    }
}
