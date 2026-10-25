package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.model.data.local.*
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.ui.utils.obtenerAnchoContenido

@Composable
fun OrganizacionesScreen(
    organizaciones: List<OrganizacionEntity>,
    contactos: List<ContactoEntity>,
    onNueva: () -> Unit,
    onEditar: (OrganizacionEntity) -> Unit,
    onContacto: (OrganizacionEntity) -> Unit,
    onEditarContacto: (ContactoEntity) -> Unit,
    onVolver: () -> Unit,
) {
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(topBar = { BarraTitulo("Organizaciones y contactos", onVolver) }) { padding ->
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
                Button(onClick = onNueva) { Text("Registrar organización") }
                if (organizaciones.isEmpty())
                    Text("Registra una organización ficticia para asociarla a las oportunidades.")
                organizaciones.forEach { o ->
                    Text(o.nombre, style = MaterialTheme.typography.titleLarge)
                    Text("${o.tipo} · ${o.comuna} · ${o.region}")
                    Text("${o.rubro}\n${o.sitioWeb}\n${o.publico}\n${o.observaciones}")
                    TextButton(onClick = { onEditar(o) }) { Text("Editar organización") }
                    OutlinedButton(onClick = { onContacto(o) }) {
                        Text("Agregar contacto ficticio")
                    }
                    contactos
                        .filter { it.organizacionId == o.id.toString() }
                        .forEach { c ->
                            Text(
                                "${c.nombre} · ${c.cargo} · ${c.area}",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                "${c.correo}\n${c.telefono}\nMedio: ${c.medio} · ${c.estado}\n${c.observaciones}"
                            )
                            TextButton(onClick = { onEditarContacto(c) }) {
                                Text("Editar contacto")
                            }
                        }
                    HorizontalDivider()
                }
            }
        }
    }
}
