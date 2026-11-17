package com.example.oportunidades.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

// Diálogo que confirma que el registro se guardó
@Composable
fun ConfirmacionGuardado(
    visible: Boolean,
    onContinuar: () -> Unit
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onContinuar,
            title = { Text("Registro guardado") },
            text = { Text("Los datos se guardaron en este dispositivo.") },
            confirmButton = {
                TextButton(onClick = onContinuar) {
                    Text("Continuar")
                }
            }
        )
    }
}
