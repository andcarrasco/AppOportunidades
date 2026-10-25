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
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.OrganizacionViewModel

@Composable
fun OrganizacionScreen(
    viewModel: OrganizacionViewModel,
    onGuardado: () -> Unit,
    onVolver: () -> Unit,
) {
    val estado by viewModel.estado.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val anchoContenido = obtenerAnchoContenido()
    var guardado by remember { mutableStateOf(false) }

    Scaffold(topBar = { BarraTitulo("Organización", onVolver) }) { padding ->
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
                CampoTexto(
                    "Nombre ficticio",
                    estado.nombre,
                    estado.errores.nombre,
                    viewModel::onNombreChange,
                )
                Selector(
                    "Tipo de organización",
                    estado.tipo,
                    Catalogos.organizaciones.map { it to it },
                    estado.errores.tipo,
                    viewModel::onTipoChange,
                )
                CampoTexto("Rubro", estado.rubro, estado.errores.rubro, viewModel::onRubroChange)
                CampoTexto(
                    "Comuna",
                    estado.comuna,
                    estado.errores.comuna,
                    viewModel::onComunaChange,
                )
                CampoTexto(
                    "Región",
                    estado.region,
                    estado.errores.region,
                    viewModel::onRegionChange,
                )
                CampoTexto(
                    "Sitio web público o ficticio",
                    estado.sitioWeb,
                    estado.errores.sitioWeb,
                    viewModel::onSitioWebChange,
                )
                CampoTexto(
                    "Público asociado",
                    estado.publico,
                    estado.errores.publico,
                    viewModel::onPublicoChange,
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
