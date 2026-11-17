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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.navigation.Screen
import com.example.oportunidades.ui.components.BarraTitulo
import com.example.oportunidades.ui.components.Selector
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.GestionViewModel
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OportunidadViewModel
import com.example.oportunidades.viewmodel.OrganizacionViewModel
import com.example.oportunidades.viewmodel.QrViewModel

@Composable
fun OportunidadCard(oportunidad: OportunidadEntity, onAbrir: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                oportunidad.nombre,
                style = MaterialTheme.typography.titleLarge
            )
            Text("Tipo: ${oportunidad.tipo}")
            Text("${oportunidad.fechaInicio} · ${oportunidad.comuna} · ${oportunidad.region}")
            Text("${oportunidad.estado} · Prioridad ${oportunidad.prioridad}")
            TextButton(onClick = onAbrir) {
                Text("Ver oportunidad")
            }
        }
    }
}

@Composable
fun OportunidadesScreen(
    gestion: GestionViewModel,
    oportunidadViewModel: OportunidadViewModel,
    organizacionViewModel: OrganizacionViewModel,
    qrViewModel: QrViewModel,
    mainViewModel: MainViewModel
) {
    val lista by gestion.oportunidades.collectAsState()
    val filtros by gestion.filtros.collectAsState()
    val cargando by gestion.cargando.collectAsState()
    val organizaciones by organizacionViewModel.registros.collectAsState()
    val anchoContenido = obtenerAnchoContenido()
    var mostrarFiltros by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { BarraTitulo("Oportunidades") }
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
                Button(
                    onClick = {
                        oportunidadViewModel.nuevo()
                        mainViewModel.navigateTo(Screen.Oportunidad, singleTop = true)
                    }
                ) {
                    Text("Registrar oportunidad")
                }

                OutlinedButton(
                    onClick = {
                        qrViewModel.clearResult()
                        mainViewModel.navigateTo(Screen.Qr, singleTop = true)
                    }
                ) {
                    Text("Escanear QR de oportunidad")
                }

                OutlinedButton(
                    onClick = {
                        mainViewModel.navigateTo(Screen.Organizaciones, singleTop = true)
                    }
                ) {
                    Text("Organizaciones y contactos")
                }

                // Búsqueda y filtros
                OutlinedTextField(
                    value = filtros.nombre,
                    onValueChange = { gestion.onFiltrosChange(filtros.copy(nombre = it)) },
                    label = { Text("Buscar por nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(onClick = { mostrarFiltros = !mostrarFiltros }) {
                    Text(if (mostrarFiltros) "Ocultar filtros" else "Mostrar filtros")
                }

                if (mostrarFiltros) {
                    OutlinedTextField(
                        value = filtros.fecha,
                        onValueChange = { gestion.onFiltrosChange(filtros.copy(fecha = it)) },
                        label = { Text("Fecha de inicio (AAAA-MM-DD)") },
                        isError = filtros.errores.fecha != null,
                        supportingText = {
                            filtros.errores.fecha?.let {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Selector(
                        etiqueta = "Estado",
                        valor = filtros.estado,
                        opciones = listOf("" to "Todos") + Catalogos.estados.map { it to it },
                        onChange = { gestion.onFiltrosChange(filtros.copy(estado = it)) }
                    )

                    Selector(
                        etiqueta = "Tipo",
                        valor = filtros.tipo,
                        opciones = listOf("" to "Todos") + Catalogos.eventos.map { it to it },
                        onChange = { gestion.onFiltrosChange(filtros.copy(tipo = it)) }
                    )

                    Selector(
                        etiqueta = "Prioridad",
                        valor = filtros.prioridad,
                        opciones = listOf("" to "Todas") + Catalogos.prioridades.map { it to it },
                        onChange = { gestion.onFiltrosChange(filtros.copy(prioridad = it)) }
                    )

                    Selector(
                        etiqueta = "Organización",
                        valor = filtros.organizacionId,
                        opciones = listOf("" to "Todas") + organizaciones.map { it.id.toString() to it.nombre },
                        onChange = { gestion.onFiltrosChange(filtros.copy(organizacionId = it)) }
                    )

                    OutlinedTextField(
                        value = filtros.comuna,
                        onValueChange = { gestion.onFiltrosChange(filtros.copy(comuna = it)) },
                        label = { Text("Comuna") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = filtros.region,
                        onValueChange = { gestion.onFiltrosChange(filtros.copy(region = it)) },
                        label = { Text("Región") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = filtros.responsable,
                        onValueChange = { gestion.onFiltrosChange(filtros.copy(responsable = it)) },
                        label = { Text("Responsable") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextButton(onClick = { gestion.limpiarFiltros() }) {
                        Text("Limpiar filtros")
                    }
                }

                // Lista de oportunidades filtradas
                val visibles = gestion.filtrar(lista, filtros)
                Text("${visibles.size} oportunidades")

                if (cargando) {
                    CircularProgressIndicator()
                } else if (visibles.isEmpty()) {
                    Text("Sin resultados. Registra una oportunidad o cambia los filtros.")
                }

                visibles.forEach { oportunidad ->
                    OportunidadCard(oportunidad) {
                        oportunidadViewModel.cargar(oportunidad)
                        mainViewModel.navigateTo(Screen.Detalle, singleTop = true)
                    }
                }
            }
        }
    }
}
