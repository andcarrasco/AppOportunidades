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
import com.example.oportunidades.viewmodel.ContactoViewModel
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OrganizacionViewModel

@Composable
fun ContactoScreen(
    viewModel: ContactoViewModel,
    organizacionViewModel: OrganizacionViewModel,
    mainViewModel: MainViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val organizaciones by organizacionViewModel.registros.collectAsState()
    val anchoContenido = obtenerAnchoContenido()
    var guardado by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { BarraTitulo("Contacto") { mainViewModel.navigateBack() } }
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

                Selector(
                    etiqueta = "Organización",
                    valor = estado.organizacionId,
                    opciones = organizaciones.map { it.id.toString() to it.nombre },
                    error = estado.errores.organizacionId,
                    onChange = viewModel::onOrganizacionIdChange
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

                OutlinedTextField(
                    value = estado.cargo,
                    onValueChange = viewModel::onCargoChange,
                    label = { Text("Cargo") },
                    isError = estado.errores.cargo != null,
                    supportingText = {
                        estado.errores.cargo?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Selector(
                    etiqueta = "Área",
                    valor = estado.area,
                    opciones = Catalogos.areas.map { it to it },
                    error = estado.errores.area,
                    onChange = viewModel::onAreaChange
                )

                OutlinedTextField(
                    value = estado.correo,
                    onValueChange = viewModel::onCorreoChange,
                    label = { Text("Correo ficticio") },
                    isError = estado.errores.correo != null,
                    supportingText = {
                        estado.errores.correo?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.telefono,
                    onValueChange = viewModel::onTelefonoChange,
                    label = { Text("Teléfono ficticio") },
                    isError = estado.errores.telefono != null,
                    supportingText = {
                        estado.errores.telefono?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Selector(
                    etiqueta = "Medio preferente",
                    valor = estado.medio,
                    opciones = Catalogos.medios.map { it to it },
                    error = estado.errores.medio,
                    onChange = viewModel::onMedioChange
                )

                Selector(
                    etiqueta = "Estado del contacto",
                    valor = estado.estado,
                    opciones = Catalogos.estadosContacto.map { it to it },
                    error = estado.errores.estado,
                    onChange = viewModel::onEstadoChange
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
