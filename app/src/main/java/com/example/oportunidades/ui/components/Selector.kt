package com.example.oportunidades.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun Selector(
    etiqueta: String,
    valor: String,
    opciones: List<Pair<String, String>>,
    error: String? = null,
    onChange: (String) -> Unit,
) {
    var abierto by remember { mutableStateOf(false) }
    val seleccion = opciones.find { it.first == valor }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { abierto = true }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir opciones")
            Text("$etiqueta: ${seleccion?.second ?: "Seleccionar"}")
        }
        if (error != null) {
            Row {
                Icon(
                    Icons.Default.Error,
                    contentDescription = "Error en la selección",
                    tint = MaterialTheme.colorScheme.error,
                )
                Text(error, color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (abierto) {
        AlertDialog(
            onDismissRequest = { abierto = false },
            title = { Text(etiqueta) },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    if (opciones.isEmpty()) {
                        Text("No hay opciones disponibles. Registra los datos necesarios primero.")
                    }
                    opciones.forEach { opcion ->
                        TextButton(
                            onClick = {
                                onChange(opcion.first)
                                abierto = false
                            }
                        ) {
                            Text(opcion.second)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { abierto = false }) { Text("Cerrar") } },
        )
    }
}
