package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.navigation.Screen
import com.example.oportunidades.ui.components.BarraTitulo
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.ActivacionViewModel
import com.example.oportunidades.viewmodel.GestionViewModel
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OportunidadViewModel

// Gestión de activaciones: oportunidades listas para coordinar
@Composable
fun ActivacionesScreen(
    gestion: GestionViewModel,
    oportunidadViewModel: OportunidadViewModel,
    activacionViewModel: ActivacionViewModel,
    mainViewModel: MainViewModel
) {
    val lista by gestion.oportunidades.collectAsState()
    val activaciones by activacionViewModel.registros.collectAsState()
    val mensaje by oportunidadViewModel.mensaje.collectAsState()
    val anchoContenido = obtenerAnchoContenido()

    // Oportunidades con propuesta enviada o participación confirmada
    val porCoordinar = lista.filter {
        it.estado == "Propuesta enviada" ||
                it.estado == "En espera de respuesta" ||
                it.estado == "Participación confirmada"
    }

    Scaffold(
        topBar = { BarraTitulo("Activaciones") }
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
                Text("Confirma la participación, registra condiciones, asigna responsable y agrega evidencias.")

                OutlinedButton(onClick = { oportunidadViewModel.sincronizar() }) {
                    Text("Actualizar desde el servidor")
                }

                if (mensaje.isNotBlank()) {
                    Text(
                        mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Text("${porCoordinar.size} oportunidades por coordinar")

                if (porCoordinar.isEmpty()) {
                    Text("Aún no hay propuestas enviadas ni participaciones confirmadas.")
                }

                porCoordinar.forEach { oportunidad ->
                    val activacion = activacionViewModel.deOportunidad(activaciones, oportunidad.id)
                    Text(
                        if (activacion == null) "Sin coordinar"
                        else "Coordinada por ${activacion.responsable}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OportunidadCard(oportunidad) {
                        oportunidadViewModel.cargar(oportunidad)
                        mainViewModel.navigateTo(Screen.Detalle, singleTop = true)
                    }
                }
            }
        }
    }
}
