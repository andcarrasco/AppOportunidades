package com.example.oportunidades.viewmodel

import androidx.lifecycle.ViewModel
import com.example.oportunidades.model.QrResult
import com.example.oportunidades.repository.QrRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Resultado del escaneo de códigos QR
class QrViewModel(
    private val repository: QrRepository = QrRepository()
) : ViewModel() {

    // Último código leído
    private val _qrResult = MutableStateFlow<QrResult?>(null)
    val qrResult: StateFlow<QrResult?> = _qrResult

    // Mensaje que la pantalla muestra al usuario
    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    fun onQrDetected(content: String) {
        _qrResult.value = repository.processQrContent(content)
        _mensaje.value = "QR detectado"
    }

    fun clearResult() {
        _qrResult.value = null
        _mensaje.value = ""
    }

    fun mensaje(valor: String) {
        _mensaje.value = valor
    }

    // Datos de la oportunidad contenidos en el último código leído
    fun campos(): Map<String, String> {
        return repository.leerCampos(_qrResult.value?.content ?: "")
    }
}
