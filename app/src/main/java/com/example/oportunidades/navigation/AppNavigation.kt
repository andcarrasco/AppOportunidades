package com.example.oportunidades.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.oportunidades.ui.screen.*
import com.example.oportunidades.viewmodel.*
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Aquí creamos los ViewModel una sola vez y se comparten entre pantallas (guía 11).
    val mainViewModel: MainViewModel = viewModel()
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val oportunidadViewModel: OportunidadViewModel = viewModel()
    val organizacionViewModel: OrganizacionViewModel = viewModel()
    val contactoViewModel: ContactoViewModel = viewModel()
    val seguimientoViewModel: SeguimientoViewModel = viewModel()
    val resultadoViewModel: ResultadoViewModel = viewModel()
    val perfilViewModel: PerfilViewModel = viewModel()
    val evidenciaViewModel: EvidenciaViewModel = viewModel()
    val gestionViewModel: GestionViewModel = viewModel()
    val qrViewModel: QrViewModel = viewModel()

    val sesion by usuarioViewModel.estado.collectAsState()
    val entrada by navController.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route

    // Escuchar eventos de navegación emitidos por el ViewModel (guía 10).
    LaunchedEffect(key1 = Unit) {
        mainViewModel.navigationEvents.collectLatest { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route.route) {
                        event.popUpToRoute?.let { popUpTo(it.route) { inclusive = event.inclusive } }
                        launchSingleTop = event.singleTop
                        restoreState = true
                    }
                }
                is NavigationEvent.PopBackStack -> navController.popBackStack()
                is NavigationEvent.NavigateUp -> navController.navigateUp()
            }
        }
    }

    // Una ruta restaurada por Android no reemplaza una sesión de demostración.
    LaunchedEffect(sesion.activo, ruta) {
        if (!sesion.activo && ruta != null && ruta != Screen.Login.route) {
            mainViewModel.navigateTo(
                Screen.Login,
                popUpToRoute = Screen.Home,
                inclusive = true,
                singleTop = true,
            )
        }
    }

    // Layout base con NavHost
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (sesion.activo)
                NavigationBar {
                    val destinos =
                        listOf(Screen.Home, Screen.Oportunidades, Screen.Pendientes, Screen.Perfil)
                    val nombres = listOf("Inicio", "Oportunidades", "Pendientes", "Perfil")
                    val iconos =
                        listOf(
                            Icons.Default.Home,
                            Icons.Default.Event,
                            Icons.Default.Notifications,
                            Icons.Default.Person,
                        )
                    destinos.forEachIndexed { i, screen ->
                        NavigationBarItem(
                            selected = ruta == screen.route,
                            icon = { Icon(iconos[i], null) },
                            label = { Text(nombres[i]) },
                            onClick = {
                                mainViewModel.navigateTo(
                                    screen,
                                    popUpToRoute = Screen.Home,
                                    singleTop = true,
                                )
                            },
                        )
                    }
                }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Login.route) { LoginScreen(usuarioViewModel, mainViewModel) }
            composable(Screen.Home.route) {
                HomeScreen(gestionViewModel, oportunidadViewModel, mainViewModel)
            }
            composable(Screen.Oportunidades.route) {
                OportunidadesScreen(
                    gestionViewModel,
                    oportunidadViewModel,
                    organizacionViewModel,
                    qrViewModel,
                    mainViewModel,
                )
            }
            composable(Screen.Oportunidad.route) {
                OportunidadScreen(oportunidadViewModel, organizacionViewModel, mainViewModel)
            }
            composable(Screen.Qr.route) {
                QrScannerScreen(
                    qrViewModel,
                    oportunidadViewModel,
                    organizacionViewModel,
                    mainViewModel,
                )
            }
            composable(Screen.Resumen.route) {
                ResumenOportunidadScreen(oportunidadViewModel, mainViewModel)
            }
            composable(Screen.Detalle.route) {
                DetalleOportunidadScreen(
                    oportunidadViewModel,
                    organizacionViewModel,
                    contactoViewModel,
                    seguimientoViewModel,
                    resultadoViewModel,
                    evidenciaViewModel,
                    mainViewModel,
                )
            }
            composable(Screen.Organizaciones.route) {
                OrganizacionesScreen(organizacionViewModel, contactoViewModel, mainViewModel)
            }
            composable(Screen.Organizacion.route) {
                OrganizacionScreen(organizacionViewModel, mainViewModel)
            }
            composable(Screen.Contacto.route) {
                ContactoScreen(contactoViewModel, organizacionViewModel, mainViewModel)
            }
            composable(Screen.Seguimiento.route) {
                SeguimientoScreen(seguimientoViewModel, mainViewModel)
            }
            composable(Screen.Historial.route) {
                HistorialScreen(oportunidadViewModel, seguimientoViewModel, mainViewModel)
            }
            composable(Screen.Resultado.route) {
                ResultadoScreen(resultadoViewModel, mainViewModel)
            }
            composable(Screen.Pendientes.route) {
                PendientesScreen(
                    gestionViewModel,
                    perfilViewModel,
                    oportunidadViewModel,
                    mainViewModel,
                )
            }
            composable(Screen.Perfil.route) {
                PerfilScreen(
                    perfilViewModel,
                    onSalir = {
                        // Limpieza al salir: se borran los datos de la sesión de demostración.
                        usuarioViewModel.salir()
                        perfilViewModel.limpiarImagen()
                        gestionViewModel.limpiarFiltros()
                        oportunidadViewModel.nuevo()
                        organizacionViewModel.nuevo()
                        contactoViewModel.nuevo()
                        seguimientoViewModel.nuevo()
                        resultadoViewModel.nuevo()
                        qrViewModel.clearResult()
                        mainViewModel.navigateTo(
                            Screen.Login,
                            popUpToRoute = Screen.Home,
                            inclusive = true,
                            singleTop = true,
                        )
                    },
                )
            }
        }
    }
}
