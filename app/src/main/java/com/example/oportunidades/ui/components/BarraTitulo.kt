package com.example.oportunidades.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

// Barra superior con título y botón para volver (opcional)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraTitulo(
    titulo: String,
    onVolver: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(titulo) },
        navigationIcon = {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        }
    )
}
