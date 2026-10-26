package com.example.oportunidades.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.oportunidades.viewmodel.EvidenciaViewModel

@Composable
fun EvidenciasOportunidad(oportunidadId: Long, viewModel: EvidenciaViewModel) {
    val context = LocalContext.current
    val estado by viewModel.estado.collectAsState()
    LaunchedEffect(oportunidadId) { viewModel.cargar(oportunidadId) }

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        viewModel.finalizarCaptura(it)
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

    val permiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) capturar()
        else viewModel.mensaje("Sin permiso de cámara. Puedes continuar y tomar la foto después.")
    }

    Text("Evidencias de la oportunidad", style = MaterialTheme.typography.titleLarge)
    Text("Fotografía un objeto o emblema de prueba. Las imágenes se conservan en este teléfono.")
    OutlinedButton(
        enabled = estado.oportunidadId == oportunidadId && !estado.capturando,
        onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                capturar()
            } else {
                permiso.launch(Manifest.permission.CAMERA)
            }
        },
    ) {
        Text("Fotografiar evidencia")
    }
    if (estado.oportunidadId == oportunidadId) {
        if (estado.mensaje.isNotBlank()) Text(estado.mensaje)
        if (estado.imagenes.isEmpty()) Text("Esta oportunidad todavía no tiene fotografías.")
        estado.imagenes.forEachIndexed { indice, uri ->
            key(uri) {
                AndroidView(
                    factory = { contexto ->
                        val imagen = ImageView(contexto)
                        imagen.scaleType = ImageView.ScaleType.FIT_CENTER
                        imagen.contentDescription = "Evidencia ${indice + 1} de la oportunidad"
                        imagen
                    },
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    update = { imagen ->
                        try {
                            imagen.setImageURI(uri)
                        } catch (e: Exception) {
                            viewModel.mensaje("No se pudo mostrar una de las fotografías")
                        }
                    },
                )
            }
        }
    }
}
