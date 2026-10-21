package com.example.oportunidades.ui.screen
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import com.example.oportunidades.model.data.local.SeguimientoEntity
import com.example.oportunidades.ui.components.Pagina
@Composable
fun HistorialScreen(id: Long, seguimientos: List<SeguimientoEntity>, onVolver: () -> Unit) {
    Pagina("Historial de la oportunidad", onVolver) {
        val gestiones = seguimientos.filter { it.oportunidadId == id.toString() }
        if (gestiones.isEmpty()) Text("Sin gestiones registradas.")
        gestiones.forEach { s ->
            Text(s.tipo, style = MaterialTheme.typography.titleLarge)
            Text("${s.fecha} ${s.hora} · ${s.responsable}")
            Text(s.resultado)
            if (s.proximaAccion.isNotBlank()) Text("Próxima acción: ${s.proximaFecha} · ${s.proximaAccion}")
            Text(s.observaciones)
            HorizontalDivider()
        }
    }
}
