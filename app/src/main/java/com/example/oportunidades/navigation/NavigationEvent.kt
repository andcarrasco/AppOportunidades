package com.example.oportunidades.navigation

// Representa los distintos tipos de eventos de navegación (guía 10).
sealed class NavigationEvent {

    /**
     * Evento para navegar a un destino específico.
     * @param route Pantalla de destino.
     * @param popUpToRoute Pantalla hasta la que se hace 'pop' en la pila. Si es null, no se hace 'pop'.
     * @param inclusive Si es 'true', [popUpToRoute] también se elimina de la pila.
     * @param singleTop Si es 'true', evita copias del mismo destino en la parte superior de la pila.
     */
    data class NavigateTo(
        val route: Screen,
        val popUpToRoute: Screen? = null,
        val inclusive: Boolean = false,
        val singleTop: Boolean = false,
    ) : NavigationEvent()

    // Evento para volver a la pantalla anterior en la pila de navegación.
    object PopBackStack : NavigationEvent()

    // Evento para navegar "hacia arriba" en la jerarquía de la aplicación.
    object NavigateUp : NavigationEvent()
}
