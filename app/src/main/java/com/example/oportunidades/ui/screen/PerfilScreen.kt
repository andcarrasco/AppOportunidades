package com.example.oportunidades.ui.screen
import android.Manifest
import android.content.pm.PackageManager
import java.io.File
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.oportunidades.ui.components.*
import com.example.oportunidades.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen(viewModel: PerfilViewModel, onSalir: () -> Unit) {
    val context = LocalContext.current
    val imagen by viewModel.imagen.collectAsState()
    val avisos by viewModel.avisos.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val color by animateColorAsState(if (avisos) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, label = "Avisos")
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { aceptada ->
        if (aceptada) viewModel.actualizarImagen(viewModel.capturaPendiente)
        viewModel.capturaPendiente = null
    }
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { viewModel.actualizarImagen(it) }
    fun capturar() {
        try {
            val carpeta = File(context.cacheDir, "capturas").apply { mkdirs() }
            val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", File.createTempFile("perfil_", ".jpg", carpeta))
            viewModel.capturaPendiente = uri
            camara.launch(uri)
        } catch (e: Exception) { viewModel.mensaje("No se pudo abrir la cámara en este dispositivo") }
    }
    val permiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permitido ->
        if (permitido) capturar() else viewModel.mensaje("Cámara sin permiso. Puedes usar la galería o continuar sin imagen.")
    }
    Pagina("Perfil") {
        Text("Ejecutivo Demo", style = MaterialTheme.typography.headlineMedium)
        Text("Equipo comercial · Cuenta ficticia")
        ImagenInteligente(imagen) { viewModel.limpiarImagen(); viewModel.mensaje("Selecciona otra imagen") }
        Text("Fotografía un objeto o selecciona un emblema sin personas identificables.")
        Button(onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) capturar()
            else permiso.launch(Manifest.permission.CAMERA)
        }) { Text("Tomar imagen con cámara") }
        OutlinedButton(onClick = { try { galeria.launch("image/*") } catch (e: Exception) { viewModel.mensaje("No hay selector de imágenes disponible") } }) { Text("Elegir desde galería") }
        Row {
            Switch(checked = avisos, onCheckedChange = viewModel::cambiarAvisos)
            Text("Mostrar avisos internos", color = color)
        }
        AnimatedVisibility(mensaje.isNotBlank()) { Text(mensaje) }
        OutlinedButton(onClick = onSalir, modifier = Modifier.fillMaxWidth()) { Text("Cerrar sesión") }
    }
}
