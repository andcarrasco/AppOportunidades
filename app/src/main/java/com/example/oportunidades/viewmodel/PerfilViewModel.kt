package com.example.oportunidades.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.model.data.local.EstadoDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class PerfilViewModel(app: Application) : AndroidViewModel(app) {

    private val store = EstadoDataStore(app)

    // URI de la imagen seleccionada o capturada
    private val _imagen = MutableStateFlow<Uri?>(null)
    val imagen: StateFlow<Uri?> = _imagen

    // Preferencia de avisos guardada con DataStore
    private val _avisos = MutableStateFlow(true)
    val avisos: StateFlow<Boolean> = _avisos

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    private var capturaPendiente: Uri? = null

    init {
        viewModelScope.launch {
            store.obtenerEstado().collect { valor ->
                _avisos.value = valor
            }
        }
    }

    // Actualiza la imagen elegida desde la galería
    fun actualizarImagen(uri: Uri?) {
        if (uri != null) {
            _imagen.value = uri
        }
    }

    // Prepara el archivo temporal con FileProvider para la cámara
    fun prepararCaptura(): Uri? {
        return try {
            val context = getApplication<Application>()
            val carpeta = File(context.cacheDir, "capturas")
            carpeta.mkdirs()
            val uri = FileProvider.getUriForFile(
                context,
                context.packageName + ".fileprovider",
                File.createTempFile("perfil_", ".jpg", carpeta)
            )
            capturaPendiente = uri
            uri
        } catch (e: Exception) {
            _mensaje.value = "No se pudo abrir la cámara en este dispositivo"
            null
        }
    }

    // Actualiza la imagen con la fotografía capturada si el usuario la aceptó
    fun finalizarCaptura(aceptada: Boolean) {
        if (aceptada) {
            actualizarImagen(capturaPendiente)
        }
        capturaPendiente = null
    }

    fun mensaje(valor: String) {
        _mensaje.value = valor
    }

    // Guarda la preferencia de avisos en DataStore
    fun cambiarAvisos(valor: Boolean) {
        viewModelScope.launch {
            store.guardarEstado(valor)
            _mensaje.value = "Preferencia guardada"
        }
    }

    fun limpiarImagen() {
        _imagen.value = null
        capturaPendiente = null
    }
}
