package com.example.oportunidades.ui.screen
import androidx.compose.runtime.*
import androidx.compose.material3.*
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.ui.components.Pagina
import com.example.oportunidades.viewmodel.GestionViewModel
@Composable
fun PendientesScreen(gestion: GestionViewModel, mostrarAvisos: Boolean, onAbrir: (OportunidadEntity) -> Unit) {
    val lista by gestion.oportunidades.collectAsState()
    val gestiones by gestion.seguimientos.collectAsState()
    val resultados by gestion.resultados.collectAsState()
    Pagina("Pendientes y próximos eventos") {
        if (!mostrarAvisos) Text("Los avisos están desactivados. Actívalos desde Perfil.")
        else {
            val avisos = gestion.avisos(lista, gestiones, resultados)
            Text("Eventos próximos: siguientes siete días. Cada oportunidad usa su última gestión para el próximo seguimiento.")
            if (avisos.isEmpty()) Text("No hay avisos de gestión.")
            avisos.forEach { (o, texto) ->
                Text(o.nombre, style = MaterialTheme.typography.titleMedium)
                Text(texto)
                TextButton(onClick = { onAbrir(o) }) { Text("Abrir oportunidad") }
                HorizontalDivider()
            }
        }
    }
}
