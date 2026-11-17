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
import com.example.oportunidades.viewmodel.OrganizacionViewModel

@Composable
fun OrganizacionScreen(
    viewModel: OrganizacionViewModel,
    mainViewModel: MainViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val anchoContenido = obtenerAnchoContenido()
    var guardado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { BarraTitulo("Organización") { mainViewModel.navigateBack() } }
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
                    value = estado.nombre,
                    onValueChange = viewModel::onNombreChange,
                    label = { Text("Nombre ficticio") },
                    isError = estado.errores.nombre != null,
                    supportingText = {
                        estado.errores.nombre?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Selector(
                    etiqueta = "Tipo de organización",
                    valor = estado.tipo,
                    opciones = Catalogos.organizaciones.map { it to it },
                    error = estado.errores.tipo,
                    onChange = viewModel::onTipoChange
                )

                OutlinedTextField(
                    value = estado.rubro,
                    onValueChange = viewModel::onRubroChange,
                    label = { Text("Rubro") },
                    isError = estado.errores.rubro != null,
                    supportingText = {
                        estado.errores.rubro?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.comuna,
                    onValueChange = viewModel::onComunaChange,
                    label = { Text("Comuna") },
                    isError = estado.errores.comuna != null,
                    supportingText = {
                        estado.errores.comuna?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.region,
                    onValueChange = viewModel::onRegionChange,
                    label = { Text("Región") },
                    isError = estado.errores.region != null,
                    supportingText = {
                        estado.errores.region?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.sitioWeb,
                    onValueChange = viewModel::onSitioWebChange,
                    label = { Text("Sitio web público o ficticio") },
                    isError = estado.errores.sitioWeb != null,
                    supportingText = {
                        estado.errores.sitioWeb?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.publico,
                    onValueChange = viewModel::onPublicoChange,
                    label = { Text("Público asociado") },
                    isError = estado.errores.publico != null,
                    supportingText = {
                        estado.errores.publico?.let {
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
