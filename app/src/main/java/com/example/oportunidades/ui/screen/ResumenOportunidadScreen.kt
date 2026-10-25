package com.example.oportunidades.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.model.OportunidadUiState
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.ui.utils.obtenerAnchoContenido

@Composable
fun ResumenOportunidadScreen(o: OportunidadUiState, onDetalle: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(o.id) { visible = o.id > 0 }
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(topBar = { BarraTitulo("Resumen del registro") }) { padding ->
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
                AnimatedVisibility(visible) {
                    Text(
                        "Guardado correctamente en este dispositivo",
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text("Nombre de la oportunidad: ${o.nombre}")
                Text("Tipo de evento: ${o.tipo}")
                Text("Descripción: ${o.descripcion}")
                Text("Fuente de información: ${o.fuente}")
                Text("Fecha de detección: ${o.fechaDeteccion}")
                Text("Responsable ficticio: ${o.responsable}")
                Text("Prioridad: ${o.prioridad}")
                Text("Potencial comercial: ${o.potencial}")
                Text("Estado de gestión: ${o.estado}")
                Text("Fecha de inicio: ${o.fechaInicio}")
                Text("Fecha de término: ${o.fechaTermino}")
                Text("Horario de inicio: ${o.horario}")
                Text("Comuna: ${o.comuna}")
                Text("Región: ${o.region}")
                Text("Ubicación general del evento: ${o.ubicacion}")
                Text("Público esperado y objetivo: ${o.publico}")
                Text("Asistentes estimados: ${o.asistentes}")
                Text("Condiciones de participación: ${o.condiciones}")
                Text("Requisitos: ${o.requisitos}")
                Text("Costos referenciales ficticios: ${o.costos}")
                Text("Espacios disponibles: ${o.espacios}")
                Text("Observaciones: ${o.observaciones}")
                Button(onClick = onDetalle) { Text("Ir a la ficha guardada") }
            }
        }
    }
}
