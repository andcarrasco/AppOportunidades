package com.example.oportunidades.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.example.oportunidades.model.EvidenciaUiState
import com.example.oportunidades.model.data.repository.EvidenciaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class EvidenciaViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = EvidenciaRepository(app)
    private val _estado = MutableStateFlow(EvidenciaUiState())
    val estado: StateFlow<EvidenciaUiState> = _estado
    private var capturaPendiente: Uri? = null
    private var oportunidadCaptura: Long = 0

    fun cargar(oportunidadId: Long) {
        _estado.update { it.copy(oportunidadId = oportunidadId, imagenes = emptyList(), mensaje = "") }
        try {
            val imagenes = repository.obtenerImagenes(oportunidadId)
            _estado.update { it.copy(imagenes = imagenes) }
        } catch (e: Exception) {
            mensaje("No se pudieron leer las evidencias de esta oportunidad")
        }
    }

    fun prepararCaptura(): Uri? {
        if (_estado.value.capturando) return null
        try {
            oportunidadCaptura = _estado.value.oportunidadId
            val captura = repository.prepararCaptura(oportunidadCaptura)
            capturaPendiente = captura
            _estado.update { it.copy(capturando = true, mensaje = "") }
            return captura
        } catch (e: Exception) {
            mensaje("No se pudo preparar el archivo de la fotografía")
            return null
        }
    }

    fun finalizarCaptura(aceptada: Boolean) {
        val captura = capturaPendiente
        if (captura == null) {
            _estado.update { it.copy(capturando = false) }
            mensaje("La captura se interrumpió. Puedes tomar otra fotografía.")
            return
        }
        try {
            repository.finalizarCaptura(oportunidadCaptura, captura, aceptada)
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
