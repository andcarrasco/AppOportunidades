package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.model.Catalogos
import com.example.oportunidades.ui.components.BarraTitulo
import com.example.oportunidades.ui.components.ConfirmacionGuardado
import com.example.oportunidades.ui.components.Selector
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OportunidadViewModel

// Supervisor comercial: decide el estado, la prioridad y el responsable de la oportunidad
@Composable
fun EstadoGestionScreen(
    viewModel: OportunidadViewModel,
    mainViewModel: MainViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val anchoContenido = obtenerAnchoContenido()
    var guardado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { BarraTitulo("Gestionar estado") { mainViewModel.navigateBack() } }
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
                Text(
                    estado.nombre,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text("Estado actual: ${estado.estado}")

                Text(
                    "Decisión",
                    style = MaterialTheme.typography.titleMedium
                )

                // Opciones del diagrama: Pendiente, En gestión, Confirmada y Descartada
                Catalogos.decisiones.forEach { (texto, valor) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = estado.estado == valor,
                            onClick = { viewModel.onEstadoChange(valor) }
                        )
                        Text("$texto ($valor)")
                    }
                }

                Selector(
                    etiqueta = "Prioridad",
                    valor = estado.prioridad,
                    opciones = Catalogos.prioridades.map { it to it },
                    error = estado.errores.prioridad,
                    onChange = viewModel::onPrioridadChange
                )

                // Asignar la oportunidad a un integrante del equipo
                Selector(
                    etiqueta = "Responsable asignado",
                    valor = estado.responsable,
                    opciones = Catalogos.responsables.map { it to it },
                    error = estado.errores.responsable,
                    onChange = viewModel::onResponsableChange
                )

                OutlinedTextField(
                    value = estado.observaciones,
                    onValueChange = viewModel::onObservacionesChange,
                    label = { Text("Observaciones del supervisor") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (mensaje.isNotBlank()) {
                    Text(
                        mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Botón: guardar (PUT en el microservicio)
                Button(
                    onClick = {
                        if (viewModel.validarFormulario()) {
                            viewModel.guardar {
                                guardado = true
                            }
                        } else {
                            viewModel.mensaje("La ficha tiene datos incompletos. Pide al ejecutivo que la complete.")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar decisión")
                }
            }
        }
    }

    ConfirmacionGuardado(
        visible = guardado,
        onContinuar = {
            guardado = false
            mainViewModel.navigateBack()
        }
    )
}
