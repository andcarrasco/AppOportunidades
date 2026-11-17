package com.example.oportunidades.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.oportunidades.ui.components.BarraTitulo
import com.example.oportunidades.ui.components.ImagenInteligente
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    onSalir: () -> Unit
) {
    val context = LocalContext.current
    val imagen by viewModel.imagen.collectAsState()
    val avisos by viewModel.avisos.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val anchoContenido = obtenerAnchoContenido()

    // Animación del color según el estado de los avisos
    val color by animateColorAsState(
        if (avisos) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        label = "Avisos"
    )

    // Cámara: guarda la foto en el archivo preparado por el ViewModel
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { aceptada ->
        viewModel.finalizarCaptura(aceptada)
    }

    // Galería: entrega la URI de la imagen elegida
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModel.actualizarImagen(uri)
    }

    fun capturar() {
        val uri = viewModel.prepararCaptura() ?: return
        try {
            camara.launch(uri)
        } catch (e: Exception) {
            viewModel.finalizarCaptura(false)
            viewModel.mensaje("No se pudo abrir la cámara en este dispositivo")
        }
    }

    // Permiso de cámara
    val permiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { permitido ->
        if (permitido) {
            capturar()
        } else {
            viewModel.mensaje("Cámara sin permiso. Puedes usar la galería o continuar sin imagen.")
        }
    }

    Scaffold(
        topBar = { BarraTitulo("Perfil") }
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
                    "Ejecutivo Demo",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text("Equipo comercial · Cuenta ficticia")

                ImagenInteligente(imagen) {
                    viewModel.limpiarImagen()
                    viewModel.mensaje("Selecciona otra imagen")
                }

                Text("Fotografía un objeto o selecciona un emblema sin personas identificables.")

                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            capturar()
                        } else {
                            permiso.launch(Manifest.permission.CAMERA)
                        }
                    }
                ) {
                    Text("Tomar imagen con cámara")
                }

                OutlinedButton(
                    onClick = {
                        try {
                            galeria.launch("image/*")
                        } catch (e: Exception) {
                            viewModel.mensaje("No hay selector de imágenes disponible")
                        }
                    }
                ) {
                    Text("Elegir desde galería")
                }

                Row {
                    Switch(
                        checked = avisos,
                        onCheckedChange = viewModel::cambiarAvisos
                    )
                    Text("Mostrar avisos internos", color = color)
                }

                AnimatedVisibility(mensaje.isNotBlank()) {
                    Text(mensaje)
                }

                OutlinedButton(
                    onClick = onSalir,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}
