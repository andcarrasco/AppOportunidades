package com.example.oportunidades.viewmodel

import android.app.Application
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import com.example.oportunidades.model.EvidenciaUiState
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

// Fotografías de evidencia de cada oportunidad, guardadas con FileProvider (guía 13).
class EvidenciaViewModel(app: Application) : AndroidViewModel(app) {
    private val _estado = MutableStateFlow(EvidenciaUiState())
    val estado: StateFlow<EvidenciaUiState> = _estado
    private var capturaPendiente: Uri? = null
    private var oportunidadCaptura: Long = 0

    private fun carpeta(oportunidadId: Long): File {
        require(oportunidadId > 0)
        return File(getApplication<Application>().filesDir, "evidencias/$oportunidadId")
    }

    private fun uri(archivo: File): Uri {
        val context = getApplication<Application>()
        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", archivo)
    }

    fun cargar(oportunidadId: Long) {
        _estado.update { it.copy(oportunidadId = oportunidadId, imagenes = emptyList(), mensaje = "") }
        try {
            val archivos = carpeta(oportunidadId).listFiles() ?: emptyArray()
            val imagenes = mutableListOf<Uri>()
            for (archivo in archivos) {
                if (archivo.isFile && archivo.name.startsWith("foto_") && archivo.length() > 0) {
                    imagenes.add(uri(archivo))
                }
            }
            _estado.update { it.copy(imagenes = imagenes) }
        } catch (e: Exception) {
            mensaje("No se pudieron leer las evidencias de esta oportunidad")
        }
    }

    // Crea el archivo temporal donde la cámara guardará la fotografía.
    fun prepararCaptura(): Uri? {
        if (_estado.value.capturando) return null
        try {
            oportunidadCaptura = _estado.value.oportunidadId
            val destino = carpeta(oportunidadCaptura)
            check(destino.isDirectory || destino.mkdirs())
            val captura = uri(File.createTempFile("pendiente_", ".jpg", destino))
            capturaPendiente = captura
            _estado.update { it.copy(capturando = true, mensaje = "") }
            return captura
        } catch (e: Exception) {
            mensaje("No se pudo preparar el archivo de la fotografía")
            return null
        }
    }

    // Confirma la fotografía si la cámara la entregó, o elimina el archivo si se canceló.
    fun finalizarCaptura(aceptada: Boolean) {
        val captura = capturaPendiente
        if (captura == null) {
            _estado.update { it.copy(capturando = false) }
            mensaje("La captura se interrumpió. Puedes tomar otra fotografía.")
            return
        }
        try {
            val nombre = captura.lastPathSegment ?: error("Captura sin nombre")
            require(nombre.startsWith("pendiente_") && '/' !in nombre)
            val archivo = File(carpeta(oportunidadCaptura), nombre)
            if (!aceptada) {
                archivo.delete()
            } else {
                if (!archivo.isFile || archivo.length() == 0L) {
                    archivo.delete()
                    error("La cámara no entregó una imagen")
                }
                val confirmada =
                    File(archivo.parentFile, "foto_" + nombre.removePrefix("pendiente_"))
                check(archivo.renameTo(confirmada))
            }
            cargar(_estado.value.oportunidadId)
            mensaje(if (aceptada) "Fotografía guardada en la oportunidad" else "Captura cancelada")
        } catch (e: Exception) {
            mensaje("No se pudo guardar la fotografía. Puedes intentarlo nuevamente.")
        } finally {
            capturaPendiente = null
            oportunidadCaptura = 0
            _estado.update { it.copy(capturando = false) }
        }
    }

    fun mensaje(valor: String) {
        _estado.update { it.copy(mensaje = valor) }
    }
}
