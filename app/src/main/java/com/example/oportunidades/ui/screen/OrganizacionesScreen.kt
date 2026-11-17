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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.oportunidades.viewmodel.ContactoViewModel
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OrganizacionViewModel

@Composable
fun OrganizacionesScreen(
    viewModel: OrganizacionViewModel,
    contactoViewModel: ContactoViewModel,
    mainViewModel: MainViewModel
) {
    val organizaciones by viewModel.registros.collectAsState()
    val contactos by contactoViewModel.registros.collectAsState()
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(
        topBar = { BarraTitulo("Organizaciones y contactos") { mainViewModel.navigateBack() } }
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
                        viewModel.nuevo()
                        mainViewModel.navigateTo(Screen.Organizacion, singleTop = true)
                    }
                ) {
                    Text("Registrar organización")
                }

                if (organizaciones.isEmpty()) {
                    Text("Registra una organización ficticia para asociarla a las oportunidades.")
                }

                organizaciones.forEach { organizacion ->
                    Text(
                        organizacion.nombre,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text("${organizacion.tipo} · ${organizacion.comuna} · ${organizacion.region}")
                    Text("${organizacion.rubro}\n${organizacion.sitioWeb}\n${organizacion.publico}\n${organizacion.observaciones}")

                    TextButton(
                        onClick = {
                            viewModel.cargar(organizacion)
                            mainViewModel.navigateTo(Screen.Organizacion, singleTop = true)
                        }
                    ) {
                        Text("Editar organización")
                    }

                    OutlinedButton(
                        onClick = {
                            contactoViewModel.nuevo(organizacion.id.toString())
                            mainViewModel.navigateTo(Screen.Contacto, singleTop = true)
                        }
                    ) {
                        Text("Agregar contacto ficticio")
                    }

                    // Contactos de la organización
                    contactoViewModel.deOrganizacion(contactos, organizacion.id.toString()).forEach { contacto ->
                        Text(
                            "${contacto.nombre} · ${contacto.cargo} · ${contacto.area}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("${contacto.correo}\n${contacto.telefono}\nMedio: ${contacto.medio} · ${contacto.estado}\n${contacto.observaciones}")

                        TextButton(
                            onClick = {
                                contactoViewModel.cargar(contacto)
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
