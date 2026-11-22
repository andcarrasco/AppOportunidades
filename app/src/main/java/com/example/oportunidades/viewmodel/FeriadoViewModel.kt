package com.example.oportunidades.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.model.Feriado
import com.example.oportunidades.model.Fechas
import com.example.oportunidades.model.data.local.OportunidadEntity
import com.example.oportunidades.repository.FeriadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

// ViewModel que mantiene los feriados obtenidos desde la API externa
class FeriadoViewModel(
    private val repository: FeriadoRepository = FeriadoRepository()
) : ViewModel() {

    // Flujo mutable que contiene la lista de feriados
    private val _feriados = MutableStateFlow<List<Feriado>>(emptyList())

    // Flujo público de solo lectura
    val feriados: StateFlow<List<Feriado>> = _feriados

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    // Se llama automáticamente al iniciar
    init {
        fetchFeriados()
    }

    // Obtiene los feriados de este año y del siguiente en segundo plano
    fun fetchFeriados() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val anio = LocalDate.now().year
                _feriados.value = repository.getFeriados(anio) + repository.getFeriados(anio + 1)
                _mensaje.value = ""
            } catch (e: Exception) {
                _mensaje.value = "No se pudieron obtener los feriados. Revisa la conexión a internet."
            }
            _cargando.value = false
        }
    }

    // Feriados desde hoy en adelante
    fun proximos(lista: List<Feriado>, hoy: String = Fechas.hoy()): List<Feriado> {
        return lista.filter { it.date >= hoy }
    }

    // Oportunidades cuyo evento comienza en ese feriado
    fun oportunidadesDelDia(feriado: Feriado, oportunidades: List<OportunidadEntity>): List<OportunidadEntity> {
        return oportunidades.filter { it.fechaInicio == feriado.date }
    }
}
