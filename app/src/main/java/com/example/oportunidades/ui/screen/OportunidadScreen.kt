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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.model.Catalogos
import com.example.oportunidades.navigation.Screen
import com.example.oportunidades.ui.components.BarraTitulo
import com.example.oportunidades.ui.components.Selector
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OportunidadViewModel
import com.example.oportunidades.viewmodel.OrganizacionViewModel

@Composable
fun OportunidadScreen(
    viewModel: OportunidadViewModel,
    organizacionViewModel: OrganizacionViewModel,
    mainViewModel: MainViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val organizaciones by organizacionViewModel.registros.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(
        topBar = { BarraTitulo("Oportunidad") { mainViewModel.navigateBack() } }
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

                // Datos generales de la oportunidad
                OutlinedTextField(
                    value = estado.nombre,
                    onValueChange = viewModel::onNombreChange,
                    label = { Text("Nombre de la oportunidad") },
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
                    etiqueta = "Tipo de evento",
                    valor = estado.tipo,
                    opciones = Catalogos.eventos.map { it to it },
                    error = estado.errores.tipo,
                    onChange = viewModel::onTipoChange
                )

                OutlinedTextField(
                    value = estado.descripcion,
                    onValueChange = viewModel::onDescripcionChange,
                    label = { Text("Descripción") },
                    isError = estado.errores.descripcion != null,
                    supportingText = {
                        estado.errores.descripcion?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.fuente,
                    onValueChange = viewModel::onFuenteChange,
                    label = { Text("Fuente de información") },
                    isError = estado.errores.fuente != null,
                    supportingText = {
                        estado.errores.fuente?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.fechaDeteccion,
                    onValueChange = viewModel::onFechaDeteccionChange,
                    label = { Text("Fecha de detección (AAAA-MM-DD)") },
                    isError = estado.errores.fechaDeteccion != null,
                    supportingText = {
                        estado.errores.fechaDeteccion?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
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

                // Gestión comercial
                Selector(
                    etiqueta = "Prioridad",
                    valor = estado.prioridad,
                    opciones = Catalogos.prioridades.map { it to it },
                    error = estado.errores.prioridad,
                    onChange = viewModel::onPrioridadChange
                )

                Selector(
                    etiqueta = "Potencial comercial",
                    valor = estado.potencial,
                    opciones = Catalogos.potenciales.map { it to it },
                    error = estado.errores.potencial,
                    onChange = viewModel::onPotencialChange
                )

                Selector(
                    etiqueta = "Estado de gestión",
                    valor = estado.estado,
                    opciones = Catalogos.estados.map { it to it },
                    error = estado.errores.estado,
                    onChange = viewModel::onEstadoChange
                )

                Selector(
                    etiqueta = "Organización",
                    valor = estado.organizacionId,
                    opciones = organizaciones.map { it.id.toString() to it.nombre },
                    error = estado.errores.organizacionId,
                    onChange = viewModel::onOrganizacionIdChange
                )

                // Fecha y lugar del evento
                OutlinedTextField(
                    value = estado.fechaInicio,
                    onValueChange = viewModel::onFechaInicioChange,
                    label = { Text("Fecha de inicio (AAAA-MM-DD)") },
                    isError = estado.errores.fechaInicio != null,
                    supportingText = {
                        estado.errores.fechaInicio?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.fechaTermino,
                    onValueChange = viewModel::onFechaTerminoChange,
                    label = { Text("Fecha de término (AAAA-MM-DD)") },
                    isError = estado.errores.fechaTermino != null,
                    supportingText = {
                        estado.errores.fechaTermino?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.horario,
                    onValueChange = viewModel::onHorarioChange,
                    label = { Text("Horario de inicio (HH:mm)") },
                    isError = estado.errores.horario != null,
                    supportingText = {
                        estado.errores.horario?.let {
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
                    value = estado.ubicacion,
                    onValueChange = viewModel::onUbicacionChange,
                    label = { Text("Ubicación general del evento") },
                    isError = estado.errores.ubicacion != null,
                    supportingText = {
                        estado.errores.ubicacion?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Público y condiciones
                Text("Describe el público esperado y el público objetivo. En las observaciones puedes explicar su afinidad.")

                OutlinedTextField(
                    value = estado.publico,
                    onValueChange = viewModel::onPublicoChange,
                    label = { Text("Público esperado y objetivo") },
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
                    value = estado.requisitos,
                    onValueChange = viewModel::onRequisitosChange,
                    label = { Text("Requisitos") },
                    isError = estado.errores.requisitos != null,
                    supportingText = {
                        estado.errores.requisitos?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.costos,
                    onValueChange = viewModel::onCostosChange,
                    label = { Text("Costos referenciales ficticios") },
                    isError = estado.errores.costos != null,
                    supportingText = {
                        estado.errores.costos?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = estado.espacios,
                    onValueChange = viewModel::onEspaciosChange,
                    label = { Text("Espacios disponibles") },
                    isError = estado.errores.espacios != null,
                    supportingText = {
                        estado.errores.espacios?.let {
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

                if (mensaje.isNotBlank()) {
                    Text(
                        mensaje,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Botón: guardar y pasar al resumen
                Button(
                    onClick = {
                        if (viewModel.validarFormulario()) {
                            viewModel.guardar {
                                mainViewModel.navigateTo(
                                    Screen.Resumen,
                                    popUpToRoute = Screen.Oportunidad,
                                    inclusive = true
                                )
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
}
