package com.example.oportunidades.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.oportunidades.navigation.Screen
import com.example.oportunidades.ui.components.BarraTitulo
import com.example.oportunidades.ui.utils.CameraPermissionHelper
import com.example.oportunidades.ui.utils.QrScanner
import com.example.oportunidades.ui.utils.obtenerAnchoContenido
import com.example.oportunidades.viewmodel.*

// Lectura de un código QR con los datos de una oportunidad (camara2026).
@Composable
fun QrScannerScreen(
    viewModel: QrViewModel,
    oportunidadViewModel: OportunidadViewModel,
    organizacionViewModel: OrganizacionViewModel,
    mainViewModel: MainViewModel,
) {
    val qrResult by viewModel.qrResult.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val organizaciones by organizacionViewModel.registros.collectAsState()
    val context = LocalContext.current
    var isScanning by remember { mutableStateOf(true) }
    var hasCameraPermission by remember {
        mutableStateOf(CameraPermissionHelper.hasCameraPermission(context))
    }
    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasCameraPermission = isGranted
            viewModel.mensaje(
                if (isGranted) "" else "Se necesita permiso de cámara para escanear QR"
            )
        }
    val anchoContenido = obtenerAnchoContenido()

    Scaffold(
        topBar = {
            BarraTitulo("Escanear oportunidad") {
                viewModel.clearResult()
                mainViewModel.navigateBack()
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier =
                    Modifier.fillMaxWidth(anchoContenido)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // El mensaje viene del estado del ViewModel, igual que los errores del formulario.
                AnimatedVisibility(mensaje.isNotBlank()) {
                    Text(
                        mensaje,
                        style = MaterialTheme.typography.titleMedium,
                        color =
                            if (hasCameraPermission) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error,
                    )
                }
                if (!hasCameraPermission) {
                    Text(
                        "Permiso de cámara requerido",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        "Para escanear códigos QR, necesitamos acceso a la cámara",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                    Button(onClick = { requestPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Conceder permiso de cámara")
                    }
                } else if (qrResult == null && isScanning) {
                    Text("Escanea un código QR", style = MaterialTheme.typography.titleLarge)
                    // Usar el scanner con CameraX
                    Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                        QrScanner(
                            onQrCodeScanned = { qrContent ->
                                // Procesar el QR detectado
                                viewModel.onQrDetected(qrContent)
                                isScanning = false
                            },
                            modifier = Modifier.fillMaxSize(),
                        )
                        // Overlay para ayudar al escaneo
                        Surface(
                            modifier = Modifier.size(250.dp).align(Alignment.Center),
                            color = Color.Transparent,
                            shape = MaterialTheme.shapes.medium,
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        ) {}
                    }
                    Text(
                        "Enfoca el código QR en el marco central",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        "Usa una línea por dato, por ejemplo: Nombre: Feria Demo, Comuna: Santiago, Fecha inicio: 2026-12-01.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else if (qrResult != null) {
                    Text("QR detectado:", style = MaterialTheme.typography.titleMedium)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                    ) {
                        Text(
                            qrResult!!.content,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                    Text(
                        "Revisa y completa los datos en el formulario y presiona Guardar para registrar la oportunidad.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Button(
                        onClick = {
                            oportunidadViewModel.cargarDesdeQr(viewModel.campos(), organizaciones)
                            viewModel.clearResult()
                            mainViewModel.navigateTo(
                                Screen.Oportunidad,
                                popUpToRoute = Screen.Qr,
                                inclusive = true,
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Crear oportunidad con estos datos")
                    }
                    OutlinedButton(
                        onClick = {
                            viewModel.clearResult()
                            isScanning = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Escanear otro código QR")
                    }
                }
            }
        }
    }
}
