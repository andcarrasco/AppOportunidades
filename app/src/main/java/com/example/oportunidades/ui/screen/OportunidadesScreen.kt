package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.model.*
import com.example.oportunidades.model.data.local.*
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.navigation.Screen
import com.example.oportunidades.viewmodel.*

@Composable
fun OportunidadCard(o: OportunidadEntity, onAbrir: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(o.nombre, style = MaterialTheme.typography.titleLarge)
            Text("Tipo: ${o.tipo}")
            Text("${o.fechaInicio} · ${o.comuna} · ${o.region}")
            Text("${o.estado} · Prioridad ${o.prioridad}")
            TextButton(onClick = onAbrir) { Text("Ver oportunidad") }
        }
    }
}

@Composable
fun OportunidadesScreen(
    gestion: GestionViewModel,
    oportunidadViewModel: OportunidadViewModel,
    organizacionViewModel: OrganizacionViewModel,
    qrViewModel: QrViewModel,
    mainViewModel: MainViewModel,
) {
    val organizaciones by organizacionViewModel.registros.collectAsState()
    val lista by gestion.oportunidades.collectAsState()
    val filtros by gestion.filtros.collectAsState()
    val error by gestion.error.collectAsState()
    val cargando by gestion.cargando.collectAsState()
    var mostrarFiltros by remember { mutableStateOf(false) }
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(topBar = { BarraTitulo("Oportunidades") }) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier =
                    Modifier.fillMaxWidth(anchoContenido)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
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
                ) { Text("Organizaciones y contactos") }
                CampoTexto("Buscar por nombre", filtros.nombre, null) {
                    gestion.cambiarFiltros(filtros.copy(nombre = it))
                }
                TextButton(onClick = { mostrarFiltros = !mostrarFiltros }) {
                    Text(if (mostrarFiltros) "Ocultar filtros" else "Mostrar filtros")
                }
                if (mostrarFiltros) {
                    CampoTexto(
                        "Fecha de inicio (AAAA-MM-DD)",
                        filtros.fecha,
                        filtros.errores.fecha,
                    ) {
                        gestion.cambiarFiltros(filtros.copy(fecha = it))
                    }
                    Selector(
                        "Estado",
                        filtros.estado,
                        listOf("" to "Todos") + Catalogos.estados.map { it to it },
                    ) {
                        gestion.cambiarFiltros(filtros.copy(estado = it))
                    }
                    Selector(
                        "Tipo",
                        filtros.tipo,
                        listOf("" to "Todos") + Catalogos.eventos.map { it to it },
                    ) {
                        gestion.cambiarFiltros(filtros.copy(tipo = it))
                    }
                    Selector(
                        "Prioridad",
                        filtros.prioridad,
                        listOf("" to "Todas") + Catalogos.prioridades.map { it to it },
                    ) {
                        gestion.cambiarFiltros(filtros.copy(prioridad = it))
                    }
                    Selector(
                        "Organización",
                        filtros.organizacionId,
                        listOf("" to "Todas") + organizaciones.map { it.id.toString() to it.nombre },
                    ) {
                        gestion.cambiarFiltros(filtros.copy(organizacionId = it))
                    }
                    CampoTexto("Comuna", filtros.comuna, null) {
                        gestion.cambiarFiltros(filtros.copy(comuna = it))
                    }
                    CampoTexto("Región", filtros.region, null) {
                        gestion.cambiarFiltros(filtros.copy(region = it))
                    }
                    CampoTexto("Responsable", filtros.responsable, null) {
                        gestion.cambiarFiltros(filtros.copy(responsable = it))
                    }
                    TextButton(onClick = gestion::limpiarFiltros) { Text("Limpiar filtros") }
                }
                if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
                val visibles = gestion.filtrar(lista, filtros)
                Text("${visibles.size} oportunidades")
                if (cargando) CircularProgressIndicator()
                else if (visibles.isEmpty())
                    Text("Sin resultados. Registra una oportunidad o cambia los filtros.")
                visibles.forEach { o ->
                    OportunidadCard(o) {
                        oportunidadViewModel.cargar(o)
                        mainViewModel.navigateTo(Screen.Detalle, singleTop = true)
                    }
                }
            }
        }
    }
}
