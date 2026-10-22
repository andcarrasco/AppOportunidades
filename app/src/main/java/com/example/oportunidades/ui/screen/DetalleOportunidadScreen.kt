package com.example.oportunidades.ui.screen
import androidx.compose.runtime.*
import androidx.compose.material3.*
import com.example.oportunidades.model.data.local.*
import com.example.oportunidades.ui.components.Pagina
@Composable
fun DetalleOportunidadScreen(o: OportunidadEntity?, organizaciones: List<OrganizacionEntity>, contactos: List<ContactoEntity>, resultados: List<ResultadoEntity>, onEditar: () -> Unit, onSeguimiento: () -> Unit, onHistorial: () -> Unit, onResultado: () -> Unit, onVolver: () -> Unit) {
    Pagina("Detalle de oportunidad", onVolver) {
        if (o == null) { Text("Cargando la ficha guardada…"); return@Pagina }
        Text(o.nombre, style = MaterialTheme.typography.headlineMedium)
        Button(onClick = onEditar) { Text("Editar datos, prioridad o estado") }
        OutlinedButton(onClick = onSeguimiento) { Text("Registrar gestión o propuesta") }
        OutlinedButton(onClick = onHistorial) { Text("Consultar historial") }
        OutlinedButton(onClick = onResultado) { Text("Resultado de activación") }
        Text("Organización: ${organizaciones.find { it.id.toString() == o.organizacionId }?.nombre ?: "Sin organización"}")
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
        Text("Público objetivo: ${o.publico}")
        Text("Asistentes estimados: ${o.asistentes}")
        Text("Condiciones de participación: ${o.condiciones}")
        Text("Requisitos: ${o.requisitos}")
        Text("Costos referenciales ficticios: ${o.costos}")
        Text("Espacios disponibles: ${o.espacios}")
        Text("Observaciones: ${o.observaciones}")
        Text("Última actualización: ${o.actualizacion}")
        HorizontalDivider()
        Text("Contactos corporativos", style = MaterialTheme.typography.titleLarge)
        contactos.filter { it.organizacionId == o.organizacionId }.forEach { c -> Text("${c.nombre} · ${c.cargo} · ${c.area}\n${c.correo} · ${c.telefono}") }
        resultados.filter { it.oportunidadId == o.id.toString() }.forEach { r ->
            Text("Resultado: ${r.estado} · ${r.fecha}", style = MaterialTheme.typography.titleLarge)
            Text("Asistentes: ${r.asistentes} · Contactos: ${r.contactos} · Interesados: ${r.interesados} · Inscripciones: ${r.inscripciones}")
            Text(r.observaciones)
        }
    }
}
