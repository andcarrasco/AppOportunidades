package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.*

@Composable
fun HistorialScreen(
    oportunidadViewModel: OportunidadViewModel,
    viewModel: SeguimientoViewModel,
    mainViewModel: MainViewModel,
) {
    val seleccionado by oportunidadViewModel.estado.collectAsState()
    val seguimientos by viewModel.registros.collectAsState()
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(
        topBar = { BarraTitulo("Historial de la oportunidad") { mainViewModel.navigateBack() } }
    ) { padding ->
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
                val gestiones = viewModel.deOportunidad(seguimientos, seleccionado.id)
                if (gestiones.isEmpty()) Text("Sin gestiones registradas.")
                gestiones.forEach { s ->
                    Text(s.tipo, style = MaterialTheme.typography.titleLarge)
                    Text("${s.fecha} ${s.hora} · ${s.responsable}")
                    Text(s.resultado)
                    if (s.proximaAccion.isNotBlank())
                        Text("Próxima acción: ${s.proximaFecha} · ${s.proximaAccion}")
                    Text(s.observaciones)
                    HorizontalDivider()
                }
            }
        }
    }
}
