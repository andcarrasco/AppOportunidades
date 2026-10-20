package com.example.oportunidades.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.navigation.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
class MainViewModel : ViewModel() {
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()
    fun navigateTo(screen: Screen) { viewModelScope.launch { _navigationEvents.emit(NavigationEvent.NavigateTo(screen)) } }
}
