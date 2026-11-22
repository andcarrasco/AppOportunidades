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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import com.example.oportunidades.ui.components.EvidenciasOportunidad
import com.example.oportunidades.ui.components.Selector
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.ActivacionViewModel
import com.example.oportunidades.viewmodel.EvidenciaViewModel
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OportunidadViewModel

// Coordinar activación: confirmar participación, condiciones, responsable y evidencias
@Composable
fun ActivacionScreen(
    viewModel: ActivacionViewModel,
    oportunidadViewModel: OportunidadViewModel,
    evidenciaViewModel: EvidenciaViewModel,
    mainViewModel: MainViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val ficha by oportunidadViewModel.ficha.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val anchoContenido = obtenerAnchoContenido()
    var guardado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { BarraTitulo("Coordinar activación") { mainViewModel.navigateBack() } }
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
                    ficha?.nombre ?: "",
                    style = MaterialTheme.typography.headlineMedium
                )

                // Confirmar participación
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = estado.confirmada,
                        onCheckedChange = viewModel::onConfirmadaChange
                    )
                    Text("Participación confirmada")
                }

                // Asignar responsable
                Selector(
                    etiqueta = "Responsable",
                    valor = estado.responsable,
                    opciones = Catalogos.responsables.map { it to it },
                    error = estado.errores.responsable,
                    onChange = viewModel::onResponsableChange
                )

                OutlinedTextField(
                    value = estado.fecha,
                    onValueChange = viewModel::onFechaChange,
                    label = { Text("Fecha de la activación (AAAA-MM-DD)") },
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

                // Registrar condiciones
                OutlinedTextField(
                    value = estado.condiciones,
                    onValueChange = viewModel::onCondicionesChange,
                    label = { Text("Condiciones de participación") },
                    isError = estado.errores.condiciones != null,
                    supportingText = {
                        estado.errores.condiciones?.let {
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
                    label = { Text("Observaciones") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (mensaje.isNotBlank()) {
                    Text(
                        mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Botón: guardar
                Button(
                    onClick = {
                        if (viewModel.validarFormulario()) {
                            viewModel.guardar(ficha) {
                                guardado = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar")
                }

                // Agregar evidencias (foto)
                ficha?.let {
                    HorizontalDivider()
                    EvidenciasOportunidad(it.id, evidenciaViewModel)
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
