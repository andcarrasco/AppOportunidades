package com.example.oportunidades.ui.screen
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.model.*
import com.example.oportunidades.model.data.local.*
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.viewmodel.GestionViewModel

@Composable
fun OportunidadCard(o: OportunidadEntity, onAbrir: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(o.nombre, style = MaterialTheme.typography.titleLarge)
            Text("${o.fechaInicio} · ${o.comuna} · ${o.region}")
            Text("${o.estado} · Prioridad ${o.prioridad}")
            TextButton(onClick = onAbrir) { Text("Ver oportunidad") }
        }
    }
}

@Composable
fun OportunidadesScreen(gestion: GestionViewModel, organizaciones: List<OrganizacionEntity>, onNueva: () -> Unit, onOrganizaciones: () -> Unit, onAbrir: (OportunidadEntity) -> Unit) {
    val lista by gestion.oportunidades.collectAsState()
    val filtros by gestion.filtros.collectAsState()
    val error by gestion.error.collectAsState()
    val cargando by gestion.cargando.collectAsState()
    var mostrarFiltros by remember { mutableStateOf(false) }
    Pagina("Oportunidades") {
        Button(onClick = onNueva) { Text("Registrar oportunidad") }
        OutlinedButton(onClick = onOrganizaciones) { Text("Organizaciones y contactos") }
        CampoTexto("Buscar por nombre", filtros.nombre, null) { gestion.cambiarFiltros(filtros.copy(nombre = it)) }
        TextButton(onClick = { mostrarFiltros = !mostrarFiltros }) { Text(if (mostrarFiltros) "Ocultar filtros" else "Mostrar filtros") }
        if (mostrarFiltros) {
            CampoTexto("Fecha de inicio (AAAA-MM-DD)", filtros.fecha, gestion.errorFecha(filtros.fecha)) { gestion.cambiarFiltros(filtros.copy(fecha = it)) }
            Selector("Estado", filtros.estado, listOf("" to "Todos") + Catalogos.estados.map { it to it }) { gestion.cambiarFiltros(filtros.copy(estado = it)) }
            Selector("Tipo", filtros.tipo, listOf("" to "Todos") + Catalogos.eventos.map { it to it }) { gestion.cambiarFiltros(filtros.copy(tipo = it)) }
            Selector("Prioridad", filtros.prioridad, listOf("" to "Todas") + Catalogos.prioridades.map { it to it }) { gestion.cambiarFiltros(filtros.copy(prioridad = it)) }
            Selector("Organización", filtros.organizacionId, listOf("" to "Todas") + organizaciones.map { it.id.toString() to it.nombre }) { gestion.cambiarFiltros(filtros.copy(organizacionId = it)) }
            CampoTexto("Comuna", filtros.comuna, null) { gestion.cambiarFiltros(filtros.copy(comuna = it)) }
            CampoTexto("Región", filtros.region, null) { gestion.cambiarFiltros(filtros.copy(region = it)) }
            CampoTexto("Responsable", filtros.responsable, null) { gestion.cambiarFiltros(filtros.copy(responsable = it)) }
            TextButton(onClick = gestion::limpiarFiltros) { Text("Limpiar filtros") }
        }
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        val visibles = gestion.filtrar(lista, filtros)
        Text("${visibles.size} oportunidades")
        if (cargando) CircularProgressIndicator()
        else if (visibles.isEmpty()) Text("Sin resultados. Registra una oportunidad o cambia los filtros.")
        visibles.forEach { o -> OportunidadCard(o) { onAbrir(o) } }
    }
}
