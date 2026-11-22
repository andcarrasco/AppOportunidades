package com.example.oportunidades.ui.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OportunidadViewModel
import com.example.oportunidades.viewmodel.OrganizacionViewModel
import com.example.oportunidades.viewmodel.QrViewModel

// Lectura de un código QR con los datos de una oportunidad
@Composable
fun QrScannerScreen(
    viewModel: QrViewModel,
    oportunidadViewModel: OportunidadViewModel,
    organizacionViewModel: OrganizacionViewModel,
    mainViewModel: MainViewModel
) {
    val qrResult by viewModel.qrResult.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val mensajeGuardado by oportunidadViewModel.mensaje.collectAsState()
    val context = LocalContext.current
    val anchoContenido = obtenerAnchoContenido()

    var isScanning by remember { mutableStateOf(true) }
    var registrando by remember { mutableStateOf(false) }

    // Si el guardado falla se vuelve a habilitar el botón Registrar
    LaunchedEffect(mensajeGuardado) {
        if (mensajeGuardado.isNotBlank()) {
            registrando = false
        }
    }
    var hasCameraPermission by remember {
        mutableStateOf(CameraPermissionHelper.hasCameraPermission(context))
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        viewModel.mensaje(if (isGranted) "" else "Se necesita permiso de cámara para escanear QR")
    }

    Scaffold(
        topBar = {
            BarraTitulo("Escanear oportunidad") {
                viewModel.clearResult()
                mainViewModel.navigateBack()
            }
        }
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // El mensaje viene del estado del ViewModel
                AnimatedVisibility(mensaje.isNotBlank()) {
                    Text(
                        mensaje,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (hasCameraPermission) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }

                if (!hasCameraPermission) {
                    Text(
                        "Permiso de cámara requerido",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Para escanear códigos QR, necesitamos acceso a la cámara",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { requestPermissionLauncher.launch(Manifest.permission.CAMERA) }
                    ) {
                        Text("Conceder permiso de cámara")
                    }
                } else if (qrResult == null && isScanning) {
                    Text(
                        "Escanea un código QR",
                        style = MaterialTheme.typography.titleLarge
                    )

                    // Scanner con CameraX
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    ) {
                        QrScanner(
                            onQrCodeScanned = { qrContent ->
                                viewModel.onQrDetected(qrContent)
                                isScanning = false
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Marco para ayudar al escaneo
                        Surface(
                            modifier = Modifier
                                .size(250.dp)
                                .align(Alignment.Center),
                            color = Color.Transparent,
                            shape = MaterialTheme.shapes.medium,
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        ) {}
                    }

                    Text(
                        "Enfoca el código QR en el marco central",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Usa una línea por dato, por ejemplo: Nombre: Feria Demo, Comuna: Santiago, Fecha inicio: 2026-12-01.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else if (qrResult != null) {
                    Text(
                        "QR detectado:",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            qrResult?.content ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    if (mensajeGuardado.isNotBlank()) {
                        Text(
                            mensajeGuardado,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // Registra la oportunidad: busca o crea la organización y guarda en el microservicio.
                    // Si faltan datos obligatorios se abre el formulario para completarlos.
                    Button(
                        enabled = !registrando,
                        onClick = {
                            registrando = true
                            val campos = viewModel.campos()
                            organizacionViewModel.buscarOCrear(
                                campos["organizacion"] ?: "Organización sin nombre",
                                campos["comuna"] ?: "",
                                campos["region"] ?: "",
                                onError = {
                                    registrando = false
                                    viewModel.mensaje("No se pudo registrar la organización del código")
                                }
                            ) { organizacionId ->
                                oportunidadViewModel.cargarDesdeQr(campos, organizacionId)
                                if (oportunidadViewModel.validarFormulario()) {
                                    oportunidadViewModel.guardar {
                                        registrando = false
                                        viewModel.clearResult()
                                        mainViewModel.navigateTo(
                                            Screen.Resumen,
                                            popUpToRoute = Screen.Qr,
                                            inclusive = true
                                        )
                                    }
                                } else {
                                    registrando = false
                                    viewModel.clearResult()
                                    mainViewModel.navigateTo(
                                        Screen.Oportunidad,
                                        popUpToRoute = Screen.Qr,
                                        inclusive = true
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrar oportunidad")
                    }

                    // Pasa los datos del QR al formulario para revisarlos antes de guardar
                    OutlinedButton(
                        onClick = {
                            val campos = viewModel.campos()
                            organizacionViewModel.buscarOCrear(
                                campos["organizacion"] ?: "Organización sin nombre",
                                campos["comuna"] ?: "",
                                campos["region"] ?: "",
                                onError = {
                                    viewModel.mensaje("No se pudo registrar la organización del código")
                                }
                            ) { organizacionId ->
                                oportunidadViewModel.cargarDesdeQr(campos, organizacionId)
                                viewModel.clearResult()
                                mainViewModel.navigateTo(
                                    Screen.Oportunidad,
                                    popUpToRoute = Screen.Qr,
                                    inclusive = true
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Revisar en el formulario")
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.clearResult()
                            isScanning = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Escanear otro código QR")
                    }
                }
            }
        }
    }
}
