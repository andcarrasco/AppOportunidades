package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.model.data.local.*
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.EvidenciaViewModel

@Composable
fun DetalleOportunidadScreen(
    o: OportunidadEntity?,
    organizaciones: List<OrganizacionEntity>,
    contactos: List<ContactoEntity>,
    resultados: List<ResultadoEntity>,
    evidenciaViewModel: EvidenciaViewModel,
    onEditar: () -> Unit,
    onSeguimiento: () -> Unit,
    onHistorial: () -> Unit,
    onResultado: () -> Unit,
    onVolver: () -> Unit,
) {
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(topBar = { BarraTitulo("Detalle de oportunidad", onVolver) }) { padding ->
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
                if (o == null) {
                    Text("Cargando la ficha guardada…")
                    return@Column
                }
                Text(o.nombre, style = MaterialTheme.typography.headlineMedium)
                Button(onClick = onEditar) { Text("Editar datos, prioridad o estado") }
                OutlinedButton(onClick = onSeguimiento) { Text("Registrar gestión o propuesta") }
                OutlinedButton(onClick = onHistorial) { Text("Consultar historial") }
                OutlinedButton(onClick = onResultado) { Text("Resultado de activación") }
                Text(
                    "Organización: ${organizaciones.find { it.id.toString() == o.organizacionId }?.nombre ?: "Sin organización"}"
                )
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
                Text("Contactos corporativos", style = MaterialTheme.typography.titleLarge)
                contactos
                    .filter { it.organizacionId == o.organizacionId }
                    .forEach { c ->
                        Text("${c.nombre} · ${c.cargo} · ${c.area}\n${c.correo} · ${c.telefono}")
                    }
                resultados
                    .filter { it.oportunidadId == o.id.toString() }
                    .forEach { r ->
                        Text(
                            "Resultado: ${r.estado} · ${r.fecha}",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            "Asistentes: ${r.asistentes} · Contactos: ${r.contactos} · Interesados: ${r.interesados} · Inscripciones: ${r.inscripciones}"
                        )
                        Text(r.observaciones)
                    }
            }
        }
    }
}
