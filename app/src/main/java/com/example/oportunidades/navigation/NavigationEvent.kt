package com.example.oportunidades.navigation

sealed class NavigationEvent {
    data class NavigateTo(val screen: Screen) : NavigationEvent()
}
