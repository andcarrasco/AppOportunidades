package com.example.oportunidades.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CampoTexto(etiqueta: String, valor: String, error: String?, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = onChange,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = {
            if (error != null) {
                Text(error)
            }
        },
        trailingIcon = {
            if (error != null) {
                Icon(Icons.Default.Error, contentDescription = "Error en el campo")
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}
