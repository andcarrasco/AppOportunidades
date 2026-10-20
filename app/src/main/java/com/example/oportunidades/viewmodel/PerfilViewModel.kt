package com.example.oportunidades.viewmodel
import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.data.EstadoDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class PerfilViewModel(app: Application) : AndroidViewModel(app) {
    private val store = EstadoDataStore(app)
    private val _imagen = MutableStateFlow<Uri?>(null)
    val imagen: StateFlow<Uri?> = _imagen
    private val _avisos = MutableStateFlow(true)
    val avisos: StateFlow<Boolean> = _avisos
    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje
    var capturaPendiente: Uri? = null
    init { viewModelScope.launch {
        try { store.obtenerEstado().collect { _avisos.value = it } }
        catch (e: Exception) { _mensaje.value = "No se pudieron leer las preferencias" }
    } }
    fun actualizarImagen(uri: Uri?) { if (uri != null) _imagen.value = uri }
    fun mensaje(valor: String) { _mensaje.value = valor }
    fun cambiarAvisos(valor: Boolean) { viewModelScope.launch {
        try { store.guardarEstado(valor); _mensaje.value = "Preferencia guardada" }
        catch (e: Exception) { _mensaje.value = "No se pudo guardar la preferencia" }
    } }
    fun limpiarImagen() { _imagen.value = null; capturaPendiente = null }
}
