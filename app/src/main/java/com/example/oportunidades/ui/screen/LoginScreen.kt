package com.example.oportunidades.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.oportunidades.navigation.Screen
import com.example.oportunidades.ui.components.BarraTitulo
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.UsuarioViewModel

@Composable
fun LoginScreen(
    viewModel: UsuarioViewModel,
    mainViewModel: MainViewModel
) {
    val estado by viewModel.estado.collectAsState()
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(
        topBar = { BarraTitulo("Oportunidades corporativas") }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                Modifier
                    .fillMaxWidth(anchoContenido)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Organiza eventos y sus gestiones",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text("Acceso académico con datos ficticios")

                // Campo correo
                OutlinedTextField(
                    value = estado.correo,
                    onValueChange = viewModel::onCorreoChange,
                    label = { Text("Correo") },
                    isError = estado.errores.correo != null,
                    supportingText = {
                        estado.errores.correo?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo clave
                OutlinedTextField(
                    value = estado.clave,
                    onValueChange = viewModel::onClaveChange,
                    label = { Text("Contraseña de prueba") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = estado.errores.clave != null,
                    supportingText = {
                        estado.errores.clave?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Botón: ingresar
                Button(
                    onClick = {
                        if (viewModel.validarFormulario()) {
                            mainViewModel.navigateTo(
                                Screen.Home,
                                popUpToRoute = Screen.Login,
                                inclusive = true
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ingresar")
                }

                Text(
                    "Cuenta: ejecutivo@example.com\nClave: Demo2026",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
