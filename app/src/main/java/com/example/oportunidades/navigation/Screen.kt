package com.example.oportunidades.navigation
sealed class Screen(val route: String) {
    data object Login : Screen("login")

    data object Home : Screen("home")

    data object Oportunidades : Screen("oportunidades")

    data object Oportunidad : Screen("oportunidad")

    data object Resumen : Screen("resumen")

    data object Detalle : Screen("detalle")

    data object Organizaciones : Screen("organizaciones")

    data object Organizacion : Screen("organizacion")

    data object Contacto : Screen("contacto")

    data object Seguimiento : Screen("seguimiento")

    data object Historial : Screen("historial")

    data object Resultado : Screen("resultado")

    data object Pendientes : Screen("pendientes")

    data object Perfil : Screen("perfil")
}
