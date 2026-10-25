package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.model.*
import com.example.oportunidades.model.data.local.OrganizacionEntity
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.ContactoViewModel

@Composable
fun ContactoScreen(
    viewModel: ContactoViewModel,
    organizaciones: List<OrganizacionEntity>,
    onGuardado: () -> Unit,
    onVolver: () -> Unit,
) {
    val estado by viewModel.estado.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val anchoContenido = obtenerAnchoContenido()
    var guardado by remember { mutableStateOf(false) }

    Scaffold(topBar = { BarraTitulo("Contacto", onVolver) }) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier =
                    Modifier.fillMaxWidth(anchoContenido)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    "Utiliza únicamente información ficticia.",
                    style = MaterialTheme.typography.bodySmall,
                )
                Selector(
                    "Organización",
                    estado.organizacionId,
                    organizaciones.map { it.id.toString() to it.nombre },
                    estado.errores.organizacionId,
                    viewModel::onOrganizacionIdChange,
                )
                CampoTexto(
                    "Nombre ficticio",
                    estado.nombre,
                    estado.errores.nombre,
                    viewModel::onNombreChange,
                )
                CampoTexto("Cargo", estado.cargo, estado.errores.cargo, viewModel::onCargoChange)
                Selector(
                    "Área",
                    estado.area,
                    Catalogos.areas.map { it to it },
                    estado.errores.area,
                    viewModel::onAreaChange,
                )
                CampoTexto(
                    "Correo ficticio",
                    estado.correo,
                    estado.errores.correo,
                    viewModel::onCorreoChange,
                )
                CampoTexto(
                    "Teléfono ficticio",
                    estado.telefono,
                    estado.errores.telefono,
                    viewModel::onTelefonoChange,
                )
                Selector(
                    "Medio preferente",
                    estado.medio,
                    listOf("Correo", "Teléfono", "Reunión").map { it to it },
                    estado.errores.medio,
                    viewModel::onMedioChange,
                )
                Selector(
                    "Estado del contacto",
                    estado.estado,
                    listOf("Pendiente", "Contactado", "Sin respuesta").map { it to it },
                    estado.errores.estado,
                    viewModel::onEstadoChange,
                )
                CampoTexto(
                    "Observaciones",
                    estado.observaciones,
                    estado.errores.observaciones,
                    viewModel::onObservacionesChange,
                )

                BotonGuardar(
                    guardando = guardando,
                    mensaje = mensaje,
                    onGuardar = { viewModel.guardar({ guardado = true }) },
                )
            }
        }
    }

    ConfirmacionGuardado(visible = guardado, onContinuar = onGuardado)
}
