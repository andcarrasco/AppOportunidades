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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.navigation.Screen
import com.example.oportunidades.ui.components.BarraTitulo
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.ui.utils.obtenerWindowSizeClass
import com.example.oportunidades.viewmodel.GestionViewModel
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OportunidadViewModel

@Composable
fun HomeScreen(
    gestion: GestionViewModel,
    oportunidadViewModel: OportunidadViewModel,
    mainViewModel: MainViewModel
) {
    val lista by gestion.oportunidades.collectAsState()
    val gestiones by gestion.seguimientos.collectAsState()
    val resultados by gestion.resultados.collectAsState()
    val ventana = obtenerWindowSizeClass()
    val anchoContenido = obtenerAnchoContenido()

    val total = lista.size
    val avisos = gestion.avisos(lista, gestiones, resultados).size
    val onOportunidades = { mainViewModel.navigateTo(Screen.Oportunidades, singleTop = true) }
    val onPendientes = { mainViewModel.navigateTo(Screen.Pendientes, singleTop = true) }

    Scaffold(
        topBar = { BarraTitulo("Inicio") }
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
                    "Hola, Ejecutivo Demo",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text("Gestiona las oportunidades con anticipación.")

                // Diseño adaptable según el ancho de la pantalla
                when (ventana.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> HomeCompacta(total, avisos, onOportunidades, onPendientes)
                    WindowWidthSizeClass.Medium -> HomeMediana(total, avisos, onOportunidades, onPendientes)
                    else -> HomeExpandida(total, avisos, onOportunidades, onPendientes)
                }

                Button(
                    onClick = {
                        oportunidadViewModel.nuevo()
                        mainViewModel.navigateTo(Screen.Oportunidad, singleTop = true)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar oportunidad")
                }

                Text("Comienza registrando una organización desde Oportunidades. Toda la información se guarda en este dispositivo.")
            }
        }
    }
}

@Composable
private fun Resumen(
    texto: String,
    cantidad: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(
                cantidad.toString(),
                style = MaterialTheme.typography.headlineLarge
            )
            Text(texto)
            TextButton(onClick = onClick) {
                Text("Consultar")
            }
        }
    }
}

@Composable
fun HomeCompacta(total: Int, avisos: Int, onLista: () -> Unit, onAvisos: () -> Unit) {
    Resumen("Oportunidades registradas", total, onLista, Modifier.fillMaxWidth())
    Resumen("Avisos de gestión", avisos, onAvisos, Modifier.fillMaxWidth())
}

@Composable
fun HomeMediana(total: Int, avisos: Int, onLista: () -> Unit, onAvisos: () -> Unit) {
    Row {
        Resumen("Oportunidades", total, onLista, Modifier.weight(1f))
        Resumen("Avisos", avisos, onAvisos, Modifier.weight(1f))
    }
}

@Composable
fun HomeExpandida(total: Int, avisos: Int, onLista: () -> Unit, onAvisos: () -> Unit) {
    Row {
        Column(Modifier.weight(1f)) {
            Text(
                "Resumen comercial",
                style = MaterialTheme.typography.titleLarge
            )
            Text("Revisa prioridades y próximas gestiones.")
        }
        Resumen("Oportunidades", total, onLista, Modifier.weight(1f))
        Resumen("Avisos", avisos, onAvisos, Modifier.weight(1f))
    }
}
