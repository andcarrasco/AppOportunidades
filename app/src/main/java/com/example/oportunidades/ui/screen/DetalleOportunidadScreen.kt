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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.navigation.Screen
import com.example.oportunidades.ui.components.BarraTitulo
import com.example.oportunidades.ui.components.EvidenciasOportunidad
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.ContactoViewModel
import com.example.oportunidades.viewmodel.EvidenciaViewModel
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OportunidadViewModel
import com.example.oportunidades.viewmodel.OrganizacionViewModel
import com.example.oportunidades.viewmodel.ResultadoViewModel
import com.example.oportunidades.viewmodel.SeguimientoViewModel

@Composable
fun DetalleOportunidadScreen(
    viewModel: OportunidadViewModel,
    organizacionViewModel: OrganizacionViewModel,
    contactoViewModel: ContactoViewModel,
    seguimientoViewModel: SeguimientoViewModel,
    resultadoViewModel: ResultadoViewModel,
    evidenciaViewModel: EvidenciaViewModel,
    mainViewModel: MainViewModel
) {
    val ficha by viewModel.ficha.collectAsState()
    val organizaciones by organizacionViewModel.registros.collectAsState()
    val contactos by contactoViewModel.registros.collectAsState()
    val resultados by resultadoViewModel.registros.collectAsState()
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(
        topBar = { BarraTitulo("Detalle de oportunidad") { mainViewModel.navigateBack() } }
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
                // Se muestra la ficha guardada en Room
                val o = ficha
                if (o == null) {
                    Text("Cargando la ficha guardada…")
                    return@Column
                }

                Text(
                    o.nombre,
                    style = MaterialTheme.typography.headlineMedium
                )

                Button(
                    onClick = {
                        viewModel.editarFicha()
                        mainViewModel.navigateTo(Screen.Oportunidad, singleTop = true)
                    }
                ) {
                    Text("Editar datos, prioridad o estado")
                }

                OutlinedButton(
                    onClick = {
                        seguimientoViewModel.nuevo(o.id.toString())
                        mainViewModel.navigateTo(Screen.Seguimiento, singleTop = true)
                    }
                ) {
                    Text("Registrar gestión o propuesta")
                }

                OutlinedButton(
                    onClick = { mainViewModel.navigateTo(Screen.Historial, singleTop = true) }
                ) {
                    Text("Consultar historial")
                }

                OutlinedButton(
                    onClick = {
                        resultadoViewModel.abrir(o.id)
                        mainViewModel.navigateTo(Screen.Resultado, singleTop = true)
                    }
                ) {
                    Text("Resultado de activación")
                }

                Text("Organización: ${organizacionViewModel.nombre(organizaciones, o.organizacionId)}")
                Text("Nombre de la oportunidad: ${o.nombre}")
                Text("Tipo de evento: ${o.tipo}")
                Text("Descripción: ${o.descripcion}")
                Text("Fuente de información: ${o.fuente}")
                Text("Fecha de detección: ${o.fechaDeteccion}")
                Text("Responsable ficticio: ${o.responsable}")
                Text("Prioridad: ${o.prioridad}")
                Text("Potencial comercial: ${o.potencial}")
                Text("Estado de gestión: ${o.estado}")
                Text("Fecha de inicio: ${o.fechaInicio}")
                Text("Fecha de término: ${o.fechaTermino}")
                Text("Horario de inicio: ${o.horario}")
                Text("Comuna: ${o.comuna}")
                Text("Región: ${o.region}")
                Text("Ubicación general del evento: ${o.ubicacion}")
                Text("Público esperado y objetivo: ${o.publico}")
                Text("Asistentes estimados: ${o.asistentes}")
                Text("Condiciones de participación: ${o.condiciones}")
                Text("Requisitos: ${o.requisitos}")
                Text("Costos referenciales ficticios: ${o.costos}")
                Text("Espacios disponibles: ${o.espacios}")
                Text("Observaciones: ${o.observaciones}")
                Text("Última actualización: ${o.actualizacion}")

                HorizontalDivider()
                EvidenciasOportunidad(o.id, evidenciaViewModel)
                HorizontalDivider()

                Text(
                    "Contactos corporativos",
                    style = MaterialTheme.typography.titleLarge
                )
                contactoViewModel.deOrganizacion(contactos, o.organizacionId).forEach { contacto ->
                    Text("${contacto.nombre} · ${contacto.cargo} · ${contacto.area}\n${contacto.correo} · ${contacto.telefono}")
                }

                resultadoViewModel.deOportunidad(resultados, o.id).forEach { resultado ->
                    Text(
                        "Resultado: ${resultado.estado} · ${resultado.fecha}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text("Asistentes: ${resultado.asistentes} · Contactos: ${resultado.contactos} · Interesados: ${resultado.interesados} · Inscripciones: ${resultado.inscripciones}")
                    Text(resultado.observaciones)
                }
            }
        }
    }
}
