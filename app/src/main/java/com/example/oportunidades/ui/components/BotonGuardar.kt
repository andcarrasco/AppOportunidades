package com.example.oportunidades.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BotonGuardar(guardando: Boolean, mensaje: String, onGuardar: () -> Unit) {
    AnimatedVisibility(visible = mensaje.isNotBlank()) {
        Text(mensaje, color = MaterialTheme.colorScheme.error)
    }
    Button(onClick = onGuardar, enabled = !guardando, modifier = Modifier.fillMaxWidth()) {
        if (guardando) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Spacer(Modifier.width(8.dp))
            Text("Guardando...")
        } else {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Guardar")
        }
    }
}
