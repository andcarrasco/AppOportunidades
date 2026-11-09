package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.navigation.Screen
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.*

@Composable
fun OrganizacionesScreen(
    viewModel: OrganizacionViewModel,
    contactoViewModel: ContactoViewModel,
    mainViewModel: MainViewModel,
) {
    val organizaciones by viewModel.registros.collectAsState()
    val contactos by contactoViewModel.registros.collectAsState()
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(
        topBar = { BarraTitulo("Organizaciones y contactos") { mainViewModel.navigateBack() } }
    ) { padding ->
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
                        viewModel.nuevo()
                        mainViewModel.navigateTo(Screen.Organizacion, singleTop = true)
                    }
                ) {
                    Text("Registrar organización")
                }
                if (organizaciones.isEmpty())
                    Text("Registra una organización ficticia para asociarla a las oportunidades.")
                organizaciones.forEach { o ->
                    Text(o.nombre, style = MaterialTheme.typography.titleLarge)
                    Text("${o.tipo} · ${o.comuna} · ${o.region}")
                    Text("${o.rubro}\n${o.sitioWeb}\n${o.publico}\n${o.observaciones}")
                    TextButton(
                        onClick = {
                            viewModel.cargar(o)
                            mainViewModel.navigateTo(Screen.Organizacion, singleTop = true)
                        }
                    ) {
                        Text("Editar organización")
                    }
                    OutlinedButton(
                        onClick = {
                            contactoViewModel.nuevo(o.id.toString())
                            mainViewModel.navigateTo(Screen.Contacto, singleTop = true)
                        }
                    ) {
                        Text("Agregar contacto ficticio")
                    }
                    contactoViewModel.deOrganizacion(contactos, o.id.toString()).forEach { c ->
                        Text(
                            "${c.nombre} · ${c.cargo} · ${c.area}",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            "${c.correo}\n${c.telefono}\nMedio: ${c.medio} · ${c.estado}\n${c.observaciones}"
                        )
                        TextButton(
                            onClick = {
                                contactoViewModel.cargar(c)
                                mainViewModel.navigateTo(Screen.Contacto, singleTop = true)
                            }
                        ) {
                            Text("Editar contacto")
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
