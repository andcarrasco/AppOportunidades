package com.example.oportunidades.ui.screen
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.viewmodel.UsuarioViewModel
@Composable
fun LoginScreen(viewModel: UsuarioViewModel, onIngresar: () -> Unit) {
    val estado by viewModel.estado.collectAsState()
    Pagina("Oportunidades corporativas") {
        Text("Organiza eventos y sus gestiones", style = MaterialTheme.typography.headlineMedium)
        Text("Acceso académico con datos ficticios")
        CampoTexto("Correo", estado.correo, estado.errores.correo, viewModel::onCorreoChange)
        OutlinedTextField(value = estado.clave, onValueChange = viewModel::onClaveChange,
            label = { Text("Contraseña de prueba") }, visualTransformation = PasswordVisualTransformation(),
            isError = estado.errores.clave != null, supportingText = { estado.errores.clave?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth())
        Button(onClick = { if (viewModel.ingresar()) onIngresar() }, modifier = Modifier.fillMaxWidth()) { Text("Ingresar") }
        Text("Cuenta: ejecutivo@example.com\nClave: Demo2026", style = MaterialTheme.typography.bodySmall)
    }
}
