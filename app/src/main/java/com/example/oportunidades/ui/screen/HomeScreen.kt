package com.example.oportunidades.ui.screen
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.ui.utils.obtenerWindowSizeClass
import com.example.oportunidades.viewmodel.GestionViewModel

@Composable
fun HomeScreen(gestion: GestionViewModel, onNueva: () -> Unit, onOportunidades: () -> Unit, onPendientes: () -> Unit) {
    val lista by gestion.oportunidades.collectAsState()
    val gestiones by gestion.seguimientos.collectAsState()
    val resultados by gestion.resultados.collectAsState()
    val error by gestion.error.collectAsState()
    val size = obtenerWindowSizeClass()
    Pagina("Inicio") {
        Text("Hola, Ejecutivo Demo", style = MaterialTheme.typography.headlineMedium)
        Text("Gestiona las oportunidades con anticipación.")
        if (error.isNotBlank()) Text(error, color = MaterialTheme.colorScheme.error)
        when (size.widthSizeClass) {
            WindowWidthSizeClass.Compact -> HomeCompacta(lista.size, gestion.avisos(lista, gestiones, resultados).size, onOportunidades, onPendientes)
            WindowWidthSizeClass.Medium -> HomeMediana(lista.size, gestion.avisos(lista, gestiones, resultados).size, onOportunidades, onPendientes)
            else -> HomeExpandida(lista.size, gestion.avisos(lista, gestiones, resultados).size, onOportunidades, onPendientes)
        }
        Button(onClick = onNueva, modifier = Modifier.fillMaxWidth()) { Text("Registrar oportunidad") }
        Text("Comienza registrando una organización desde Oportunidades. Toda la información se guarda en este dispositivo.")
    }
}
@Composable
private fun Resumen(texto: String, cantidad: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier) { Column(Modifier.padding(16.dp)) {
        Text(cantidad.toString(), style = MaterialTheme.typography.headlineLarge)
        Text(texto)
        TextButton(onClick = onClick) { Text("Consultar") }
    } }
}
@Composable
fun HomeCompacta(total: Int, avisos: Int, onLista: () -> Unit, onAvisos: () -> Unit) {
    Resumen("Oportunidades registradas", total, onLista, Modifier.fillMaxWidth())
    Resumen("Avisos de gestión", avisos, onAvisos, Modifier.fillMaxWidth())
}
@Composable
fun HomeMediana(total: Int, avisos: Int, onLista: () -> Unit, onAvisos: () -> Unit) {
    Row { Resumen("Oportunidades", total, onLista, Modifier.weight(1f)); Resumen("Avisos", avisos, onAvisos, Modifier.weight(1f)) }
}
@Composable
fun HomeExpandida(total: Int, avisos: Int, onLista: () -> Unit, onAvisos: () -> Unit) {
    Row { Column(Modifier.weight(1f)) { Text("Resumen comercial", style = MaterialTheme.typography.titleLarge); Text("Revisa prioridades y próximas gestiones.") }; Resumen("Oportunidades", total, onLista, Modifier.weight(1f)); Resumen("Avisos", avisos, onAvisos, Modifier.weight(1f)) }
}
