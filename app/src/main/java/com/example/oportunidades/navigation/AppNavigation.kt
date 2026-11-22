package com.example.oportunidades.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.oportunidades.model.Catalogos
import com.example.oportunidades.ui.screen.ActivacionScreen
import com.example.oportunidades.ui.screen.ActivacionesScreen
import com.example.oportunidades.ui.screen.ContactoScreen
import com.example.oportunidades.ui.screen.DetalleOportunidadScreen
import com.example.oportunidades.ui.screen.EstadoGestionScreen
import com.example.oportunidades.ui.screen.FeriadosScreen
import com.example.oportunidades.ui.screen.HistorialScreen
import com.example.oportunidades.ui.screen.HomeScreen
import com.example.oportunidades.ui.screen.LoginScreen
import com.example.oportunidades.ui.screen.OportunidadScreen
import com.example.oportunidades.ui.screen.OportunidadesScreen
import com.example.oportunidades.ui.screen.OrganizacionScreen
import com.example.oportunidades.ui.screen.OrganizacionesScreen
import com.example.oportunidades.ui.screen.PendientesScreen
import com.example.oportunidades.ui.screen.PerfilScreen
import com.example.oportunidades.ui.screen.QrScannerScreen
import com.example.oportunidades.ui.screen.ResultadoScreen
import com.example.oportunidades.ui.screen.ResumenOportunidadScreen
import com.example.oportunidades.ui.screen.SeguimientoScreen
import com.example.oportunidades.viewmodel.ActivacionViewModel
import com.example.oportunidades.viewmodel.ContactoViewModel
import com.example.oportunidades.viewmodel.EvidenciaViewModel
import com.example.oportunidades.viewmodel.FeriadoViewModel
import com.example.oportunidades.viewmodel.GestionViewModel
import com.example.oportunidades.viewmodel.MainViewModel
import com.example.oportunidades.viewmodel.OportunidadViewModel
import com.example.oportunidades.viewmodel.OrganizacionViewModel
import com.example.oportunidades.viewmodel.PerfilViewModel
import com.example.oportunidades.viewmodel.QrViewModel
import com.example.oportunidades.viewmodel.ResultadoViewModel
import com.example.oportunidades.viewmodel.SeguimientoViewModel
import com.example.oportunidades.viewmodel.UsuarioViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Aquí creamos los ViewModel una sola vez y se comparten entre pantallas
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
    val activacionViewModel: ActivacionViewModel = viewModel()
    val feriadoViewModel: FeriadoViewModel = viewModel()

    val sesion by usuarioViewModel.estado.collectAsState()
    val entrada by navController.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route

    // El usuario que inició sesión queda como responsable por defecto
    LaunchedEffect(sesion.nombre) {
        oportunidadViewModel.usuario(sesion.nombre)
    }

    // Cierra la sesión, limpia los formularios y vuelve al Login para elegir otro perfil
    val cerrarSesion = {
        usuarioViewModel.salir()
        perfilViewModel.limpiarImagen()
        gestionViewModel.limpiarFiltros()
        oportunidadViewModel.nuevo()
        organizacionViewModel.nuevo()
        contactoViewModel.nuevo()
        seguimientoViewModel.nuevo()
        resultadoViewModel.nuevo()
        activacionViewModel.nuevo()
        qrViewModel.clearResult()
        mainViewModel.navigateTo(
            Screen.Login,
            popUpToRoute = Screen.Home,
            inclusive = true,
            singleTop = true
        )
    }

    // Escucha los eventos de navegación emitidos por el MainViewModel
    LaunchedEffect(key1 = Unit) {
        mainViewModel.navigationEvents.collectLatest { event ->
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route.route) {
                        event.popUpToRoute?.let {
                            popUpTo(it.route) {
                                inclusive = event.inclusive
                            }
                        }
                        launchSingleTop = event.singleTop
                        restoreState = true
                    }
                }

                is NavigationEvent.PopBackStack -> navController.popBackStack()
                is NavigationEvent.NavigateUp -> navController.navigateUp()
            }
        }
    }

    // Sin sesión iniciada solo se puede estar en el Login
    LaunchedEffect(sesion.activo, ruta) {
        if (!sesion.activo && ruta != null && ruta != Screen.Login.route) {
            mainViewModel.navigateTo(
                Screen.Login,
                popUpToRoute = Screen.Home,
                inclusive = true,
                singleTop = true
            )
        }
    }

    // Layout base con barra inferior y NavHost
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (sesion.activo) {
                NavigationBar {
                    // El perfil de activaciones trabaja con su propia lista
                    val activaciones = sesion.perfil == Catalogos.ACTIVACIONES
                    val destinos = listOf(
                        Screen.Home,
                        if (activaciones) Screen.Activaciones else Screen.Oportunidades,
                        Screen.Pendientes,
                        Screen.Feriados,
                        Screen.Perfil
                    )
                    val nombres = listOf(
                        "Inicio",
                        if (activaciones) "Activaciones" else "Oportunidades",
                        "Pendientes",
                        "Feriados",
                        "Perfil"
                    )
                    val iconos = listOf(
                        Icons.Default.Home,
                        if (activaciones) Icons.Default.Campaign else Icons.Default.Event,
                        Icons.Default.Notifications,
                        Icons.Default.DateRange,
                        Icons.Default.Person
                    )

                    destinos.forEachIndexed { i, screen ->
                        NavigationBarItem(
                            selected = ruta == screen.route,
                            icon = { Icon(iconos[i], contentDescription = null) },
                            label = { Text(nombres[i]) },
                            onClick = {
                                mainViewModel.navigateTo(
                                    screen,
                                    popUpToRoute = Screen.Home,
                                    singleTop = true
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(usuarioViewModel, mainViewModel)
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    sesion.nombre,
                    sesion.perfil,
                    sesion.mensaje,
                    gestionViewModel,
                    oportunidadViewModel,
                    qrViewModel,
                    mainViewModel,
                    onSalir = cerrarSesion
                )
            }

            composable(Screen.Oportunidades.route) {
                OportunidadesScreen(
                    sesion.perfil,
                    gestionViewModel,
                    oportunidadViewModel,
                    organizacionViewModel,
                    qrViewModel,
                    mainViewModel
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
                    mainViewModel
                )
            }

            composable(Screen.Resumen.route) {
                ResumenOportunidadScreen(oportunidadViewModel, mainViewModel)
            }

            composable(Screen.Detalle.route) {
                DetalleOportunidadScreen(
                    sesion.nombre,
                    sesion.perfil,
                    oportunidadViewModel,
                    organizacionViewModel,
                    contactoViewModel,
                    seguimientoViewModel,
                    resultadoViewModel,
                    evidenciaViewModel,
                    activacionViewModel,
                    mainViewModel
                )
            }

            composable(Screen.EstadoGestion.route) {
                EstadoGestionScreen(oportunidadViewModel, mainViewModel)
            }

            composable(Screen.Activaciones.route) {
                ActivacionesScreen(
                    gestionViewModel,
                    oportunidadViewModel,
                    activacionViewModel,
                    mainViewModel
                )
            }

            composable(Screen.Activacion.route) {
                ActivacionScreen(
                    activacionViewModel,
                    oportunidadViewModel,
                    evidenciaViewModel,
                    mainViewModel
                )
            }

            composable(Screen.Feriados.route) {
                FeriadosScreen(feriadoViewModel, gestionViewModel, mainViewModel)
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
                    mainViewModel
                )
            }

            composable(Screen.Perfil.route) {
                PerfilScreen(
                    sesion.nombre,
                    sesion.perfil,
                    perfilViewModel,
                    onSalir = cerrarSesion
                )
            }
        }
    }
}
