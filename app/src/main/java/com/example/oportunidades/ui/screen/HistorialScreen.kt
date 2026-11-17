package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.ui.components.BarraTitulo
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OportunidadViewModel
import com.example.oportunidades.viewmodel.SeguimientoViewModel

@Composable
fun HistorialScreen(
    oportunidadViewModel: OportunidadViewModel,
    viewModel: SeguimientoViewModel,
    mainViewModel: MainViewModel
) {
    val seleccionada by oportunidadViewModel.estado.collectAsState()
    val seguimientos by viewModel.registros.collectAsState()
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(
        topBar = { BarraTitulo("Historial de la oportunidad") { mainViewModel.navigateBack() } }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                Modifier
                    .fillMaxWidth(anchoContenido)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val gestiones = viewModel.deOportunidad(seguimientos, seleccionada.id)

                if (gestiones.isEmpty()) {
                    Text("Sin gestiones registradas.")
                }

                gestiones.forEach { seguimiento ->
                    Text(
                        seguimiento.tipo,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text("${seguimiento.fecha} ${seguimiento.hora} · ${seguimiento.responsable}")
                    Text(seguimiento.resultado)
                    if (seguimiento.proximaAccion.isNotBlank()) {
                        Text("Próxima acción: ${seguimiento.proximaFecha} · ${seguimiento.proximaAccion}")
                    }
                    Text(seguimiento.observaciones)
                    HorizontalDivider()
                }
            }
        }
    }
}
