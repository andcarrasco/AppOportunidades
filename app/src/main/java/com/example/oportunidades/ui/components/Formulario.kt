package com.example.oportunidades.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedVisibility

@Composable
fun CampoTexto(etiqueta: String, valor: String, error: String?, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = valor, onValueChange = onChange, label = { Text(etiqueta) },
        isError = error != null, modifier = Modifier.fillMaxWidth(),
        supportingText = { if (error != null) Text(error) },
        trailingIcon = { if (error != null) Icon(Icons.Default.Error, "Error") }
    )
}

@Composable
fun Selector(etiqueta: String, valor: String, opciones: List<Pair<String, String>>, error: String? = null, onChange: (String) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { abierto = true }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.ArrowDropDown, null)
            Text("$etiqueta: ${opciones.find { it.first == valor }?.second ?: "Seleccionar"}")
        }
        if (error != null) Text(error, color = MaterialTheme.colorScheme.error)
    }
    if (abierto) AlertDialog(
        onDismissRequest = { abierto = false }, title = { Text(etiqueta) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                if (opciones.isEmpty()) Text("Primero registra una organización.")
                opciones.forEach { (id, nombre) ->
                    TextButton(onClick = { onChange(id); abierto = false }) { Text(nombre) }
                }
            }
        }, confirmButton = { TextButton(onClick = { abierto = false }) { Text("Cerrar") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pagina(titulo: String, onVolver: (() -> Unit)? = null, contenido: @Composable ColumnScope.() -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(titulo) }, navigationIcon = {
            if (onVolver != null) IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, "Volver") }
        })
    }) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp), content = contenido)
    }
}

@Composable
fun Formulario(titulo: String, guardando: Boolean, mensaje: String, onVolver: () -> Unit, onGuardar: () -> Unit, contenido: @Composable ColumnScope.() -> Unit) {
    Pagina(titulo, onVolver) {
        Text("Utiliza únicamente información ficticia.", style = MaterialTheme.typography.bodySmall)
        contenido()
        AnimatedVisibility(mensaje.isNotBlank()) { Text(mensaje, color = MaterialTheme.colorScheme.primary) }
        Button(onClick = onGuardar, enabled = !guardando, modifier = Modifier.fillMaxWidth()) {
            if (guardando) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            else { Icon(Icons.Default.Save, null); Spacer(Modifier.width(8.dp)); Text("Guardar") }
        }
    }
}
