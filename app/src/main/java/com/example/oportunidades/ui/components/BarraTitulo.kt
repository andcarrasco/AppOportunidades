package com.example.oportunidades.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

// Barra superior con título, botón para volver y botón para cerrar sesión (opcionales)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraTitulo(
    titulo: String,
    onSalir: (() -> Unit)? = null,
    onVolver: (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(titulo) },
        navigationIcon = {
            if (onVolver != null) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
            }
        },
        actions = {
            if (onSalir != null) {
                IconButton(onClick = onSalir) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión o cambiar perfil")
                }
            }
        }
    )
}
