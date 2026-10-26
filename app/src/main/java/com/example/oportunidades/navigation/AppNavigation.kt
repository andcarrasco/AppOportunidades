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

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Una sola instancia compartida por formulario y resumen: guía 11.
    val usuario: UsuarioViewModel = viewModel()
    val oportunidad: OportunidadViewModel = viewModel()
    val organizacion: OrganizacionViewModel = viewModel()
    val contacto: ContactoViewModel = viewModel()
    val seguimiento: SeguimientoViewModel = viewModel()
    val resultado: ResultadoViewModel = viewModel()
    val perfil: PerfilViewModel = viewModel()
    val evidencia: EvidenciaViewModel = viewModel()
    val gestion: GestionViewModel = viewModel()
    val main: MainViewModel = viewModel()
    val sesion by usuario.estado.collectAsState()
    val organizaciones by organizacion.registros.collectAsState()
    val contactos by contacto.registros.collectAsState()
    val oportunidades by oportunidad.registros.collectAsState()
    val seguimientos by seguimiento.registros.collectAsState()
    val resultados by resultado.registros.collectAsState()
    val seleccionado by oportunidad.estado.collectAsState()
    val avisos by perfil.avisos.collectAsState()
    val entrada by navController.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route
    // Una ruta restaurada por Android no reemplaza una sesión de demostración.
    LaunchedEffect(sesion.activo, ruta) {
        if (!sesion.activo && ruta != null && ruta != Screen.Login.route) {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    fun ir(screen: Screen) {
        main.navigateTo(screen)
    }
    fun abrir(o: com.example.oportunidades.model.data.local.OportunidadEntity) {
        oportunidad.cargar(o)
        ir(Screen.Detalle)
    }
    fun nueva() {
        oportunidad.nuevo()
        ir(Screen.Oportunidad)
    }
    LaunchedEffect(Unit) {
        main.navigationEvents.collect { evento ->
            when (evento) {
                is NavigationEvent.NavigateTo ->
                    navController.navigate(evento.screen.route) { launchSingleTop = true }
            }
        }
    }
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
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Home.route)
                                    launchSingleTop = true
                                }
                            },
                        )
                    }
                }
        },
    ) { padding ->
        NavHost(
            navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Screen.Login.route) {
                LoginScreen(usuario) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    gestion,
                    ::nueva,
                    { ir(Screen.Oportunidades) },
                    { ir(Screen.Pendientes) },
                )
            }
            composable(Screen.Oportunidades.route) {
                OportunidadesScreen(
                    gestion,
                    organizaciones,
                    ::nueva,
                    { ir(Screen.Organizaciones) },
                    ::abrir,
                )
            }
            composable(Screen.Oportunidad.route) {
                OportunidadScreen(
                    oportunidad,
                    organizaciones,
                    onGuardado = {
                        navController.navigate(Screen.Resumen.route) {
                            popUpTo(Screen.Oportunidad.route) { inclusive = true }
                        }
                    },
                    onVolver = { navController.popBackStack() },
                )
            }
            composable(Screen.Resumen.route) {
                ResumenOportunidadScreen(seleccionado) {
                    navController.navigate(Screen.Detalle.route) {
                        popUpTo(Screen.Resumen.route) { inclusive = true }
                    }
                }
            }
            composable(Screen.Detalle.route) {
                val actual = oportunidades.find { it.id == seleccionado.id }
                DetalleOportunidadScreen(
                    actual,
                    organizaciones,
                    contactos,
                    resultados,
                    evidencia,
                    onEditar = {
                        if (actual != null) {
                            oportunidad.cargar(actual)
                            ir(Screen.Oportunidad)
                        }
                    },
                    onSeguimiento = {
                        seguimiento.nuevo(seleccionado.id.toString())
                        ir(Screen.Seguimiento)
                    },
                    onHistorial = { ir(Screen.Historial) },
                    onResultado = {
                        val previo =
                            resultados.firstOrNull {
                                it.oportunidadId == seleccionado.id.toString()
                            }
                        if (previo != null) resultado.cargar(previo)
                        else resultado.nuevo(seleccionado.id.toString())
                        ir(Screen.Resultado)
                    },
                    onVolver = { navController.popBackStack() },
                )
            }
            composable(Screen.Organizaciones.route) {
                OrganizacionesScreen(
                    organizaciones,
                    contactos,
                    onNueva = {
                        organizacion.nuevo()
                        ir(Screen.Organizacion)
                    },
                    onEditar = {
                        organizacion.cargar(it)
                        ir(Screen.Organizacion)
                    },
                    onContacto = {
                        contacto.nuevo()
                        contacto.onOrganizacionIdChange(it.id.toString())
                        ir(Screen.Contacto)
                    },
                    onEditarContacto = {
                        contacto.cargar(it)
                        ir(Screen.Contacto)
                    },
                    onVolver = { navController.popBackStack() },
                )
            }
            composable(Screen.Organizacion.route) {
                OrganizacionScreen(
                    organizacion,
                    { navController.popBackStack() },
                    { navController.popBackStack() },
                )
            }
            composable(Screen.Contacto.route) {
                ContactoScreen(
                    contacto,
                    organizaciones,
                    { navController.popBackStack() },
                    { navController.popBackStack() },
                )
            }
            composable(Screen.Seguimiento.route) {
                SeguimientoScreen(
                    seguimiento,
                    { navController.popBackStack() },
                    { navController.popBackStack() },
                )
            }
            composable(Screen.Historial.route) {
                HistorialScreen(seleccionado.id, seguimientos, { navController.popBackStack() })
            }
            composable(Screen.Resultado.route) {
                ResultadoScreen(
                    resultado,
                    { navController.popBackStack() },
                    { navController.popBackStack() },
                )
            }
            composable(Screen.Pendientes.route) { PendientesScreen(gestion, avisos, ::abrir) }
            composable(Screen.Perfil.route) {
                PerfilScreen(perfil) {
                    usuario.salir()
                    perfil.limpiarImagen()
                    gestion.limpiarFiltros()
                    oportunidad.nuevo()
                    organizacion.nuevo()
                    contacto.nuevo()
                    seguimiento.nuevo()
                    resultado.nuevo()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}
