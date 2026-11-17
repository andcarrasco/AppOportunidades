package com.example.oportunidades.viewmodel

import androidx.lifecycle.ViewModel
import com.example.oportunidades.navigation.NavigationEvent
import com.example.oportunidades.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// Centraliza la navegación de la app mediante eventos
class MainViewModel : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()

    // Expone el flujo de solo lectura para que la UI pueda observarlo
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    // Emite el evento de navegación hacia la ruta deseada
    fun navigateTo(
        screen: Screen,
        popUpToRoute: Screen? = null,
        inclusive: Boolean = false,
        singleTop: Boolean = false
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            _navigationEvents.emit(
                NavigationEvent.NavigateTo(
                    route = screen,
                    popUpToRoute = popUpToRoute,
                    inclusive = inclusive,
                    singleTop = singleTop
                )
            )
        }
    }

    // Vuelve a la pantalla anterior
    fun navigateBack() {
        CoroutineScope(Dispatchers.Main).launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }

    // Navega hacia arriba (pantalla padre)
    fun navigateUp() {
        CoroutineScope(Dispatchers.Main).launch {
            _navigationEvents.emit(NavigationEvent.NavigateUp)
        }
    }
}
