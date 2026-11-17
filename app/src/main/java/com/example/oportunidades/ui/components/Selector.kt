package com.example.oportunidades.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

// Campo de selección: cada opción es un par (valor guardado, texto visible)
@Composable
fun Selector(
    etiqueta: String,
    valor: String,
    opciones: List<Pair<String, String>>,
    error: String? = null,
    onChange: (String) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }
    val seleccion = opciones.find { it.first == valor }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { abierto = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir opciones")
            Text("$etiqueta: ${seleccion?.second ?: "Seleccionar"}")
        }

        // Mensaje de error, igual que en los campos de texto
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
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
            confirmButton = {
                TextButton(onClick = { abierto = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}
