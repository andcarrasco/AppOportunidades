package com.example.oportunidades.ui.screen

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
import androidx.compose.material3.OutlinedTextField
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
import com.example.oportunidades.viewmodel.SeguimientoViewModel

@Composable
fun SeguimientoScreen(
    viewModel: SeguimientoViewModel,
    mainViewModel: MainViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val anchoContenido = obtenerAnchoContenido()
    var guardado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { BarraTitulo("Seguimiento") { mainViewModel.navigateBack() } }
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
                    "Utiliza únicamente información ficticia.",
                    style = MaterialTheme.typography.bodySmall
                )

                OutlinedTextField(
                    value = estado.responsable,
                    onValueChange = viewModel::onResponsableChange,
                    label = { Text("Responsable ficticio") },
                    isError = estado.errores.responsable != null,
                    supportingText = {
                        estado.errores.responsable?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Selector(
                    etiqueta = "Tipo de acción",
                    valor = estado.tipo,
                    opciones = Catalogos.acciones.map { it to it },
                    error = estado.errores.tipo,
                    onChange = viewModel::onTipoChange
                )

                OutlinedTextField(
                    value = estado.fecha,
                    onValueChange = viewModel::onFechaChange,
                    label = { Text("Fecha de la acción (AAAA-MM-DD)") },
                    isError = estado.errores.fecha != null,
                    supportingText = {
                        estado.errores.fecha?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.hora,
                    onValueChange = viewModel::onHoraChange,
                    label = { Text("Hora (HH:mm)") },
                    isError = estado.errores.hora != null,
                    supportingText = {
                        estado.errores.hora?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.resultado,
                    onValueChange = viewModel::onResultadoChange,
                    label = { Text("Resultado de la gestión") },
                    isError = estado.errores.resultado != null,
                    supportingText = {
                        estado.errores.resultado?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.proximaAccion,
                    onValueChange = viewModel::onProximaAccionChange,
                    label = { Text("Próxima acción") },
                    isError = estado.errores.proximaAccion != null,
                    supportingText = {
                        estado.errores.proximaAccion?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.proximaFecha,
                    onValueChange = viewModel::onProximaFechaChange,
                    label = { Text("Fecha de próximo seguimiento (AAAA-MM-DD)") },
                    isError = estado.errores.proximaFecha != null,
                    supportingText = {
                        estado.errores.proximaFecha?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Si hubo un contacto, indica su nombre ficticio en las observaciones para reconocerlo en el historial.")

                OutlinedTextField(
                    value = estado.observaciones,
                    onValueChange = viewModel::onObservacionesChange,
                    label = { Text("Observaciones del contacto y la gestión") },
                    isError = estado.errores.observaciones != null,
                    supportingText = {
                        estado.errores.observaciones?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Botón: guardar
                Button(
                    onClick = {
                        if (viewModel.validarFormulario()) {
                            viewModel.guardar {
                                guardado = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar")
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
