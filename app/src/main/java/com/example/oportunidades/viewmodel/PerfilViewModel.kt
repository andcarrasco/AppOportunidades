package com.example.oportunidades.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.data.EstadoDataStore
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(app: Application) : AndroidViewModel(app) {
    private val store = EstadoDataStore(app)
    // URI de la imagen seleccionada o capturada (guía 13).
    private val _imagen = MutableStateFlow<Uri?>(null)
    val imagen: StateFlow<Uri?> = _imagen
    private val _avisos = MutableStateFlow(true)
    val avisos: StateFlow<Boolean> = _avisos
    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje
    private var capturaPendiente: Uri? = null

    init {
        viewModelScope.launch {
            try {
                store.obtenerEstado().collect { _avisos.value = it }
            } catch (e: Exception) {
                _mensaje.value = "No se pudieron leer las preferencias"
            }
        }
    }

    // Actualiza la imagen elegida desde la galería.
    fun actualizarImagen(uri: Uri?) {
        if (uri != null) _imagen.value = uri
    }

    // Prepara el almacenamiento temporal con FileProvider para la cámara.
    fun prepararCaptura(): Uri? {
        return try {
            val context = getApplication<Application>()
            val carpeta = File(context.cacheDir, "capturas").apply { mkdirs() }
            val uri =
                FileProvider.getUriForFile(
                    context,
                    context.packageName + ".fileprovider",
                    File.createTempFile("perfil_", ".jpg", carpeta),
                )
            capturaPendiente = uri
            uri
        } catch (e: Exception) {
            _mensaje.value = "No se pudo abrir la cámara en este dispositivo"
            null
        }
    }

    // Actualiza la imagen con la fotografía capturada si el usuario la aceptó.
    fun finalizarCaptura(aceptada: Boolean) {
        if (aceptada) actualizarImagen(capturaPendiente)
        capturaPendiente = null
    }

    fun mensaje(valor: String) {
        _mensaje.value = valor
    }

    fun cambiarAvisos(valor: Boolean) {
        viewModelScope.launch {
            try {
                store.guardarEstado(valor)
                _mensaje.value = "Preferencia guardada"
            } catch (e: Exception) {
                _mensaje.value = "No se pudo guardar la preferencia"
            }
        }
    }

    fun limpiarImagen() {
        _imagen.value = null
        capturaPendiente = null
    }
}
