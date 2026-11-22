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
import androidx.compose.material3.OutlinedButton
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
import com.example.oportunidades.model.Catalogos
import com.example.oportunidades.navigation.Screen
import com.example.oportunidades.ui.components.BarraTitulo
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.ui.utils.obtenerWindowSizeClass
import com.example.oportunidades.viewmodel.GestionViewModel
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OportunidadViewModel
import com.example.oportunidades.viewmodel.QrViewModel

@Composable
fun HomeScreen(
    nombre: String,
    perfil: String,
    aviso: String,
    gestion: GestionViewModel,
    oportunidadViewModel: OportunidadViewModel,
    qrViewModel: QrViewModel,
    mainViewModel: MainViewModel,
    onSalir: () -> Unit
) {
    val lista by gestion.oportunidades.collectAsState()
    val gestiones by gestion.seguimientos.collectAsState()
    val resultados by gestion.resultados.collectAsState()
    val ventana = obtenerWindowSizeClass()
    val anchoContenido = obtenerAnchoContenido()

    // El perfil de gestión de activaciones trabaja con las participaciones confirmadas
    val titulo = if (perfil == Catalogos.ACTIVACIONES)
        "Participaciones confirmadas"
    else "Oportunidades registradas"
    val total = if (perfil == Catalogos.ACTIVACIONES)
        lista.count { it.estado == "Participación confirmada" }
    else lista.size
    val avisos = gestion.avisos(lista, gestiones, resultados).size

    val onLista = {
        val destino = if (perfil == Catalogos.ACTIVACIONES) Screen.Activaciones else Screen.Oportunidades
        mainViewModel.navigateTo(destino, singleTop = true)
    }
    val onPendientes = { mainViewModel.navigateTo(Screen.Pendientes, singleTop = true) }

    Scaffold(
        topBar = { BarraTitulo("Inicio", onSalir = onSalir) }
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
                    "Hola, $nombre",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    perfil,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                if (aviso.isNotBlank()) {
                    Text(
                        aviso,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                // Diseño adaptable según el ancho de la pantalla
                when (ventana.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> HomeCompacta(titulo, total, avisos, onLista, onPendientes)
                    WindowWidthSizeClass.Medium -> HomeMediana(titulo, total, avisos, onLista, onPendientes)
                    else -> HomeExpandida(titulo, total, avisos, onLista, onPendientes)
                }

                // Acciones de cada perfil, según el flujo del caso
                when (perfil) {
                    Catalogos.EJECUTIVO -> {
                        Text("Registra oportunidades y las gestiones con cada organización.")

                        Button(
                            onClick = {
                                oportunidadViewModel.nuevo()
                                mainViewModel.navigateTo(Screen.Oportunidad, singleTop = true)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Registrar oportunidad")
                        }

                        OutlinedButton(
                            onClick = {
                                qrViewModel.clearResult()
                                mainViewModel.navigateTo(Screen.Qr, singleTop = true)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Escanear QR de oportunidad")
                        }

                        OutlinedButton(
                            onClick = { mainViewModel.navigateTo(Screen.Organizaciones, singleTop = true) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Organizaciones y contactos")
                        }
                    }

                    Catalogos.SUPERVISOR -> {
                        Text("Revisa las oportunidades del equipo y decide su estado.")

                        // Indicadores generales por decisión
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    "Indicadores",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Catalogos.decisiones.forEach { (texto, estado) ->
                                    Text("$texto: ${lista.count { it.estado == estado }}")
                                }
                            }
                        }

                        Button(
                            onClick = onLista,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Lista de oportunidades")
                        }
                    }

                    Catalogos.ACTIVACIONES -> {
                        Text("Coordina las activaciones confirmadas y registra sus resultados.")

                        Button(
                            onClick = onLista,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Coordinar activaciones")
                        }
                    }
                }

                OutlinedButton(
                    onClick = { mainViewModel.navigateTo(Screen.Feriados, singleTop = true) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Feriados de Chile (API externa)")
                }
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
fun HomeCompacta(titulo: String, total: Int, avisos: Int, onLista: () -> Unit, onAvisos: () -> Unit) {
    Resumen(titulo, total, onLista, Modifier.fillMaxWidth())
    Resumen("Avisos de gestión", avisos, onAvisos, Modifier.fillMaxWidth())
}

@Composable
fun HomeMediana(titulo: String, total: Int, avisos: Int, onLista: () -> Unit, onAvisos: () -> Unit) {
    Row {
        Resumen(titulo, total, onLista, Modifier.weight(1f))
        Resumen("Avisos", avisos, onAvisos, Modifier.weight(1f))
    }
}

@Composable
fun HomeExpandida(titulo: String, total: Int, avisos: Int, onLista: () -> Unit, onAvisos: () -> Unit) {
    Row {
        Column(Modifier.weight(1f)) {
            Text(
                "Resumen comercial",
                style = MaterialTheme.typography.titleLarge
            )
            Text("Revisa prioridades y próximas gestiones.")
        }
        Resumen(titulo, total, onLista, Modifier.weight(1f))
        Resumen("Avisos", avisos, onAvisos, Modifier.weight(1f))
    }
}
