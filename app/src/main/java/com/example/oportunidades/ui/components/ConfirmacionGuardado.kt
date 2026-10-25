package com.example.oportunidades.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ConfirmacionGuardado(visible: Boolean, onContinuar: () -> Unit) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onContinuar,
            title = { Text("Registro guardado") },
            text = { Text("Los datos se guardaron en este dispositivo.") },
            confirmButton = { TextButton(onClick = onContinuar) { Text("Continuar") } },
        )
    }
}
