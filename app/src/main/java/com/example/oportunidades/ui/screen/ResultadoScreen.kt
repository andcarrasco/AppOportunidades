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
import com.example.oportunidades.viewmodel.ResultadoViewModel

@Composable
fun ResultadoScreen(
    viewModel: ResultadoViewModel,
    mainViewModel: MainViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val anchoContenido = obtenerAnchoContenido()
    var guardado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { BarraTitulo("Resultado") { mainViewModel.navigateBack() } }
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
                    value = estado.fecha,
                    onValueChange = viewModel::onFechaChange,
                    label = { Text("Fecha de realización (AAAA-MM-DD)") },
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

                Selector(
                    etiqueta = "Resultado final",
                    valor = estado.estado,
                    opciones = Catalogos.resultados.map { it to it },
                    error = estado.errores.estado,
                    onChange = viewModel::onEstadoChange
                )

                OutlinedTextField(
                    value = estado.asistentes,
                    onValueChange = viewModel::onAsistentesChange,
                    label = { Text("Asistentes estimados") },
                    isError = estado.errores.asistentes != null,
                    supportingText = {
                        estado.errores.asistentes?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.contactos,
                    onValueChange = viewModel::onContactosChange,
                    label = { Text("Contactos generados") },
                    isError = estado.errores.contactos != null,
                    supportingText = {
                        estado.errores.contactos?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.interesados,
                    onValueChange = viewModel::onInteresadosChange,
                    label = { Text("Personas interesadas") },
                    isError = estado.errores.interesados != null,
                    supportingText = {
                        estado.errores.interesados?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.inscripciones,
                    onValueChange = viewModel::onInscripcionesChange,
                    label = { Text("Inscripciones") },
                    isError = estado.errores.inscripciones != null,
                    supportingText = {
                        estado.errores.inscripciones?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.observaciones,
                    onValueChange = viewModel::onObservacionesChange,
                    label = { Text("Observaciones y aprendizajes") },
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

                OutlinedTextField(
                    value = estado.proximasAcciones,
                    onValueChange = viewModel::onProximasAccionesChange,
                    label = { Text("Próximas acciones") },
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
