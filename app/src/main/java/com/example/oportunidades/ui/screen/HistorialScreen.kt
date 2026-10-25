package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.model.data.local.SeguimientoEntity
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.ui.utils.obtenerAnchoContenido

@Composable
fun HistorialScreen(id: Long, seguimientos: List<SeguimientoEntity>, onVolver: () -> Unit) {
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(topBar = { BarraTitulo("Historial de la oportunidad", onVolver) }) { padding ->
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
                val gestiones = seguimientos.filter { it.oportunidadId == id.toString() }
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
