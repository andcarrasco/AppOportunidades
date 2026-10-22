package com.example.oportunidades.ui.screen
import androidx.compose.runtime.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.*
import com.example.oportunidades.model.OportunidadUiState
import com.example.oportunidades.ui.components.Pagina
@Composable
fun ResumenOportunidadScreen(o: OportunidadUiState, onDetalle: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(o.id) { visible = o.id > 0 }
    Pagina("Resumen del registro") {
        AnimatedVisibility(visible) { Text("Guardado correctamente en este dispositivo", color = MaterialTheme.colorScheme.primary) }
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
        Text("Público objetivo: ${o.publico}")
        Text("Asistentes estimados: ${o.asistentes}")
        Text("Condiciones de participación: ${o.condiciones}")
        Text("Requisitos: ${o.requisitos}")
        Text("Costos referenciales ficticios: ${o.costos}")
        Text("Espacios disponibles: ${o.espacios}")
        Text("Observaciones: ${o.observaciones}")
        Button(onClick = onDetalle) { Text("Ir a la ficha guardada") }
    }
}
