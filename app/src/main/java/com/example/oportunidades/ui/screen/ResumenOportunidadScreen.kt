package com.example.oportunidades.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.navigation.Screen
import com.example.oportunidades.ui.components.BarraTitulo
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OportunidadViewModel

// Observa el mismo ViewModel del formulario, como ResumenScreen de la guía 11
@Composable
fun ResumenOportunidadScreen(
    viewModel: OportunidadViewModel,
    mainViewModel: MainViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val anchoContenido = obtenerAnchoContenido()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(estado.id) {
        visible = estado.id > 0
    }

    Scaffold(
        topBar = { BarraTitulo("Resumen del registro") }
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
                AnimatedVisibility(visible) {
                    Text(
                        "Guardado correctamente en el servidor",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text("Nombre de la oportunidad: ${estado.nombre}")
                Text("Tipo de evento: ${estado.tipo}")
                Text("Descripción: ${estado.descripcion}")
                Text("Fuente de información: ${estado.fuente}")
                Text("Fecha de detección: ${estado.fechaDeteccion}")
                Text("Responsable ficticio: ${estado.responsable}")
                Text("Prioridad: ${estado.prioridad}")
                Text("Potencial comercial: ${estado.potencial}")
                Text("Estado de gestión: ${estado.estado}")
                Text("Fecha de inicio: ${estado.fechaInicio}")
                Text("Fecha de término: ${estado.fechaTermino}")
                Text("Horario de inicio: ${estado.horario}")
                Text("Comuna: ${estado.comuna}")
                Text("Región: ${estado.region}")
                Text("Ubicación general del evento: ${estado.ubicacion}")
                Text("Público esperado y objetivo: ${estado.publico}")
                Text("Asistentes estimados: ${estado.asistentes}")
                Text("Condiciones de participación: ${estado.condiciones}")
                Text("Requisitos: ${estado.requisitos}")
                Text("Costos referenciales ficticios: ${estado.costos}")
                Text("Espacios disponibles: ${estado.espacios}")
                Text("Observaciones: ${estado.observaciones}")

                Button(
                    onClick = {
                        mainViewModel.navigateTo(
                            Screen.Detalle,
                            popUpToRoute = Screen.Resumen,
                            inclusive = true
                        )
                    }
                ) {
                    Text("Ir a la ficha guardada")
                }
            }
        }
    }
}
