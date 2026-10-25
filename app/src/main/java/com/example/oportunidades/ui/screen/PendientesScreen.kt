package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.GestionViewModel

@Composable
fun PendientesScreen(
    gestion: GestionViewModel,
    mostrarAvisos: Boolean,
    onAbrir: (OportunidadEntity) -> Unit,
) {
    val lista by gestion.oportunidades.collectAsState()
    val gestiones by gestion.seguimientos.collectAsState()
    val resultados by gestion.resultados.collectAsState()
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(topBar = { BarraTitulo("Pendientes y próximos eventos") }) { padding ->
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
                if (!mostrarAvisos) Text("Los avisos están desactivados. Actívalos desde Perfil.")
                else {
                    val avisos = gestion.avisos(lista, gestiones, resultados)
                    Text(
                        "Eventos próximos: siguientes siete días. Cada oportunidad usa su última gestión para el próximo seguimiento."
                    )
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
    }
}
