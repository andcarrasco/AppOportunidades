package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.navigation.Screen
import com.example.oportunidades.ui.components.BarraTitulo
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.GestionViewModel
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OportunidadViewModel
import com.example.oportunidades.viewmodel.PerfilViewModel

@Composable
fun PendientesScreen(
    gestion: GestionViewModel,
    perfilViewModel: PerfilViewModel,
    oportunidadViewModel: OportunidadViewModel,
    mainViewModel: MainViewModel
) {
    val mostrarAvisos by perfilViewModel.avisos.collectAsState()
    val lista by gestion.oportunidades.collectAsState()
    val gestiones by gestion.seguimientos.collectAsState()
    val resultados by gestion.resultados.collectAsState()
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(
        topBar = { BarraTitulo("Pendientes y próximos eventos") }
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
                // Los avisos se pueden desactivar desde Perfil
                if (!mostrarAvisos) {
                    Text("Los avisos están desactivados. Actívalos desde Perfil.")
                } else {
                    val avisos = gestion.avisos(lista, gestiones, resultados)
                    Text("Eventos próximos: siguientes siete días. Cada oportunidad usa su última gestión para el próximo seguimiento.")

                    if (avisos.isEmpty()) {
                        Text("No hay avisos de gestión.")
                    }

                    avisos.forEach { (oportunidad, texto) ->
                        Text(
                            oportunidad.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(texto)
                        TextButton(
                            onClick = {
                                oportunidadViewModel.cargar(oportunidad)
                                mainViewModel.navigateTo(Screen.Detalle, singleTop = true)
                            }
                        ) {
                            Text("Abrir oportunidad")
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
