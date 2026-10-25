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
import com.example.oportunidades.viewmodel.SeguimientoViewModel

@Composable
fun SeguimientoScreen(
    viewModel: SeguimientoViewModel,
    onGuardado: () -> Unit,
    onVolver: () -> Unit,
) {
    val estado by viewModel.estado.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val anchoContenido = obtenerAnchoContenido()
    var guardado by remember { mutableStateOf(false) }

    Scaffold(topBar = { BarraTitulo("Seguimiento", onVolver) }) { padding ->
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
                    "Responsable ficticio",
                    estado.responsable,
                    estado.errores.responsable,
                    viewModel::onResponsableChange,
                )
                Selector(
                    "Tipo de acción",
                    estado.tipo,
                    Catalogos.acciones.map { it to it },
                    estado.errores.tipo,
                    viewModel::onTipoChange,
                )
                CampoTexto(
                    "Fecha de la acción (AAAA-MM-DD)",
                    estado.fecha,
                    estado.errores.fecha,
                    viewModel::onFechaChange,
                )
                CampoTexto(
                    "Hora (HH:mm)",
                    estado.hora,
                    estado.errores.hora,
                    viewModel::onHoraChange,
                )
                CampoTexto(
                    "Resultado de la gestión",
                    estado.resultado,
                    estado.errores.resultado,
                    viewModel::onResultadoChange,
                )
                CampoTexto(
                    "Próxima acción",
                    estado.proximaAccion,
                    estado.errores.proximaAccion,
                    viewModel::onProximaAccionChange,
                )
                CampoTexto(
                    "Fecha de próximo seguimiento (AAAA-MM-DD)",
                    estado.proximaFecha,
                    estado.errores.proximaFecha,
                    viewModel::onProximaFechaChange,
                )
                Text(
                    "Si hubo un contacto, indica su nombre ficticio en las observaciones para reconocerlo en el historial."
                )
                CampoTexto(
                    "Observaciones del contacto y la gestión",
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
