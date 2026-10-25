package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.UsuarioViewModel

@Composable
fun LoginScreen(viewModel: UsuarioViewModel, onIngresar: () -> Unit) {
    val estado by viewModel.estado.collectAsState()
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(topBar = { BarraTitulo("Oportunidades corporativas") }) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier =
                    Modifier.fillMaxWidth(anchoContenido)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    "Organiza eventos y sus gestiones",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text("Acceso académico con datos ficticios")
                CampoTexto(
                    "Correo",
                    estado.correo,
                    estado.errores.correo,
                    viewModel::onCorreoChange,
                )
                OutlinedTextField(
                    value = estado.clave,
                    onValueChange = viewModel::onClaveChange,
                    label = { Text("Contraseña de prueba") },
                    visualTransformation = PasswordVisualTransformation(),
                    trailingIcon = {
                        if (estado.errores.clave != null) {
                            Icon(Icons.Default.Error, contentDescription = "Error en la contraseña")
                        }
                    },
                    isError = estado.errores.clave != null,
                    supportingText = { estado.errores.clave?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = { if (viewModel.ingresar()) onIngresar() },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Ingresar")
                }
                Text(
                    "Cuenta: ejecutivo@example.com\nClave: Demo2026",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
