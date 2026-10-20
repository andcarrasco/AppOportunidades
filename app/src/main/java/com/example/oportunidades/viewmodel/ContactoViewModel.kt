package com.example.oportunidades.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.model.*
import com.example.oportunidades.model.data.local.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactoViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getDatabase(app)
    private val dao = db.contactoDao()
    private val _estado = MutableStateFlow(ContactoUiState())
    val estado: StateFlow<ContactoUiState> = _estado
    private val _registros = MutableStateFlow<List<ContactoEntity>>(emptyList())
    val registros: StateFlow<List<ContactoEntity>> = _registros
    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando
    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje
    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando
    init {
        viewModelScope.launch {
            try { dao.obtenerTodos().collect { _registros.value = it; _cargando.value = false } }
            catch (e: Exception) { _cargando.value = false; _mensaje.value = "No se pudieron cargar los registros" }
        }
    }
    fun nuevo() { _estado.value = ContactoUiState(); _mensaje.value = "" }
    fun cargar(registro: ContactoEntity) {
        _estado.value = ContactoUiState(id = registro.id, organizacionId = registro.organizacionId, nombre = registro.nombre, cargo = registro.cargo, area = registro.area, correo = registro.correo, telefono = registro.telefono, medio = registro.medio, estado = registro.estado, observaciones = registro.observaciones)
        _mensaje.value = ""
    }
    fun onOrganizacionIdChange(valor: String) {
        _estado.update { it.copy(organizacionId = valor, errores = it.errores.copy(organizacionId = null)) }
    }
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }
    fun onCargoChange(valor: String) {
        _estado.update { it.copy(cargo = valor, errores = it.errores.copy(cargo = null)) }
    }
    fun onAreaChange(valor: String) {
        _estado.update { it.copy(area = valor, errores = it.errores.copy(area = null)) }
    }
    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }
    fun onTelefonoChange(valor: String) {
        _estado.update { it.copy(telefono = valor, errores = it.errores.copy(telefono = null)) }
    }
    fun onMedioChange(valor: String) {
        _estado.update { it.copy(medio = valor, errores = it.errores.copy(medio = null)) }
    }
    fun onEstadoChange(valor: String) {
        _estado.update { it.copy(estado = valor, errores = it.errores.copy(estado = null)) }
    }
    fun onObservacionesChange(valor: String) {
        _estado.update { it.copy(observaciones = valor, errores = it.errores.copy(observaciones = null)) }
    }
    fun validarFormulario(): Boolean {
        val errores = validarDatos(_estado.value)
        _estado.update { it.copy(errores = errores) }
        return listOfNotNull(errores.organizacionId, errores.nombre, errores.cargo, errores.area, errores.correo, errores.telefono, errores.medio, errores.estado, errores.observaciones).isEmpty()
    }
    fun guardar(onListo: () -> Unit) {
        if (_guardando.value || !validarFormulario()) return
        val a = _estado.value
        _guardando.value = true
        _mensaje.value = ""
        viewModelScope.launch {
            try {

                if (db.organizacionDao().obtener(a.organizacionId.toLongOrNull() ?: 0) == null) {
                    _mensaje.value = "Selecciona una organización registrada"; return@launch
                }

                
                val registro = ContactoEntity(id = a.id, organizacionId = a.organizacionId.trim(), nombre = a.nombre.trim(), cargo = a.cargo.trim(), area = a.area.trim(), correo = a.correo.trim(), telefono = a.telefono.trim(), medio = a.medio.trim(), estado = a.estado.trim(), observaciones = a.observaciones.trim())
                val guardadoId = if (a.id == 0L) dao.insertar(registro) else {
                    dao.modificar(a.id, registro.organizacionId, registro.nombre, registro.cargo, registro.area, registro.correo, registro.telefono, registro.medio, registro.estado, registro.observaciones); a.id
                }

                _estado.update { it.copy(id = guardadoId) }
                _mensaje.value = "Guardado correctamente"
                onListo()
            } catch (e: Exception) { _mensaje.value = "No se pudo completar el guardado. Revisa los datos e intenta nuevamente." }
            finally { _guardando.value = false }
        }
    }
    companion object {
        fun validarDatos(a: ContactoUiState): ContactoErrores = ContactoErrores(
            organizacionId = if (a.organizacionId.isBlank()) "Campo obligatorio" else null,
            nombre = if (a.nombre.isBlank()) "Campo obligatorio" else null,
            cargo = if (a.cargo.isBlank()) "Campo obligatorio" else null,
            area = if (a.area.isBlank()) "Campo obligatorio" else null,
            correo = if (!a.correo.contains("@")) "Correo ficticio inválido" else null,
            telefono = if (a.telefono.isBlank()) "Campo obligatorio" else null,
            medio = if (a.medio.isBlank()) "Campo obligatorio" else null,
            estado = if (a.estado.isBlank()) "Campo obligatorio" else null
        )
    }
}
