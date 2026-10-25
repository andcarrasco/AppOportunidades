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
import com.example.oportunidades.viewmodel.ResultadoViewModel

@Composable
fun ResultadoScreen(viewModel: ResultadoViewModel, onGuardado: () -> Unit, onVolver: () -> Unit) {
    val estado by viewModel.estado.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val anchoContenido = obtenerAnchoContenido()
    var guardado by remember { mutableStateOf(false) }

    Scaffold(topBar = { BarraTitulo("Resultado", onVolver) }) { padding ->
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
                    "Fecha de realización (AAAA-MM-DD)",
                    estado.fecha,
                    estado.errores.fecha,
                    viewModel::onFechaChange,
                )
                Selector(
                    "Resultado final",
                    estado.estado,
                    listOf("Activación realizada", "Activación cancelada", "Oportunidad cerrada")
                        .map { it to it },
                    estado.errores.estado,
                    viewModel::onEstadoChange,
                )
                CampoTexto(
                    "Asistentes estimados",
                    estado.asistentes,
                    estado.errores.asistentes,
                    viewModel::onAsistentesChange,
                )
                CampoTexto(
                    "Contactos generados",
                    estado.contactos,
                    estado.errores.contactos,
                    viewModel::onContactosChange,
                )
                CampoTexto(
                    "Personas interesadas",
                    estado.interesados,
                    estado.errores.interesados,
                    viewModel::onInteresadosChange,
                )
                CampoTexto(
                    "Inscripciones",
                    estado.inscripciones,
                    estado.errores.inscripciones,
                    viewModel::onInscripcionesChange,
                )
                CampoTexto(
                    "Observaciones y aprendizajes",
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
