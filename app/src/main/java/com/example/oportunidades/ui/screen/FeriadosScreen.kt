package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.ui.components.BarraTitulo
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.FeriadoViewModel
import com.example.oportunidades.viewmodel.GestionViewModel
import com.example.oportunidades.viewmodel.MainViewModel

// API externa: feriados de Chile, útiles para planificar los eventos
@Composable
fun FeriadosScreen(
    viewModel: FeriadoViewModel,
    gestion: GestionViewModel,
    mainViewModel: MainViewModel
) {
    // Observamos el flujo de datos del ViewModel
    val feriados by viewModel.feriados.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val oportunidades by gestion.oportunidades.collectAsState()
    val anchoContenido = obtenerAnchoContenido()

    val proximos = viewModel.proximos(feriados)

    Scaffold(
        topBar = { BarraTitulo("Feriados de Chile") { mainViewModel.navigateBack() } }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(anchoContenido)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("Fuente: date.nager.at (API pública).")
                    Text("Revisa los feriados antes de fijar la fecha de un evento.")
                }

                item {
                    OutlinedButton(onClick = { viewModel.fetchFeriados() }) {
                        Text("Actualizar")
                    }
                }

                if (cargando) {
                    item { CircularProgressIndicator() }
                }

                if (mensaje.isNotBlank()) {
                    item {
                        Text(
                            mensaje,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Lista de feriados
                items(proximos) { feriado ->
                    val delDia = viewModel.oportunidadesDelDia(feriado, oportunidades)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = feriado.localName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = feriado.date,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            delDia.forEach {
                                Text(
                                    text = "Evento ese día: ${it.nombre}",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
