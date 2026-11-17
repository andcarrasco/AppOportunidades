package com.example.oportunidades.navigation

// Representa los distintos tipos de eventos de navegación
sealed class NavigationEvent {

    // Navegar a una pantalla, opcionalmente quitando pantallas de la pila
    data class NavigateTo(
        val route: Screen,
        val popUpToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false
    ) : NavigationEvent()

    // Volver a la pantalla anterior
    object PopBackStack : NavigationEvent()

    // Navegar hacia arriba en la jerarquía de la app
    object NavigateUp : NavigationEvent()
}
