package com.example.oportunidades.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.oportunidades.model.ContactoErrores
import com.example.oportunidades.model.ContactoUiState
import com.example.oportunidades.model.data.local.AppDatabase
import com.example.oportunidades.model.data.local.ContactoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactoViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getDatabase(app).contactoDao()

    // Estado interno mutable
    private val _estado = MutableStateFlow(ContactoUiState())

    // Estado expuesto para la UI
    val estado: StateFlow<ContactoUiState> = _estado

    // Contactos guardados en Room
    private val _registros = MutableStateFlow<List<ContactoEntity>>(emptyList())
    val registros: StateFlow<List<ContactoEntity>> = _registros

    init {
        viewModelScope.launch {
            dao.obtenerTodos().collect { lista ->
                _registros.value = lista
            }
        }
    }

    // Limpia el formulario para un contacto nuevo de la organización indicada
    fun nuevo(organizacionId: String = "") {
        _estado.value = ContactoUiState(organizacionId = organizacionId)
    }

    // Carga un registro guardado para editarlo
    fun cargar(registro: ContactoEntity) {
        _estado.value = ContactoUiState(
            id = registro.id,
            organizacionId = registro.organizacionId,
            nombre = registro.nombre,
            cargo = registro.cargo,
            area = registro.area,
            correo = registro.correo,
            telefono = registro.telefono,
            medio = registro.medio,
            estado = registro.estado,
            observaciones = registro.observaciones
        )
    }

    // Contactos asociados a una organización
    fun deOrganizacion(lista: List<ContactoEntity>, organizacionId: String): List<ContactoEntity> {
        return lista.filter { it.organizacionId == organizacionId }
    }

    // Actualiza el campo organización y limpia su error
    fun onOrganizacionIdChange(valor: String) {
        _estado.update {
            it.copy(
                organizacionId = valor,
                errores = it.errores.copy(organizacionId = null)
            )
        }
    }

    fun onNombreChange(valor: String) {
        _estado.update {
            it.copy(
                nombre = valor,
                errores = it.errores.copy(nombre = null)
            )
        }
    }

    fun onCargoChange(valor: String) {
        _estado.update {
            it.copy(
                cargo = valor,
                errores = it.errores.copy(cargo = null)
            )
        }
    }

    fun onAreaChange(valor: String) {
        _estado.update {
            it.copy(
                area = valor,
                errores = it.errores.copy(area = null)
            )
        }
    }

    fun onCorreoChange(valor: String) {
        _estado.update {
            it.copy(
                correo = valor,
                errores = it.errores.copy(correo = null)
            )
        }
    }

    fun onTelefonoChange(valor: String) {
        _estado.update {
            it.copy(
                telefono = valor,
                errores = it.errores.copy(telefono = null)
            )
        }
    }

    fun onMedioChange(valor: String) {
        _estado.update {
            it.copy(
                medio = valor,
                errores = it.errores.copy(medio = null)
            )
        }
    }

    fun onEstadoChange(valor: String) {
        _estado.update {
            it.copy(
                estado = valor,
                errores = it.errores.copy(estado = null)
            )
        }
    }

    fun onObservacionesChange(valor: String) {
        _estado.update {
            it.copy(
                observaciones = valor,
                errores = it.errores.copy(observaciones = null)
            )
        }
    }

    // Validación global del formulario
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value

        val errores = ContactoErrores(
            organizacionId = if (estadoActual.organizacionId.isBlank())
                "Campo obligatorio"
            else null,

            nombre = if (estadoActual.nombre.isBlank())
                "Campo obligatorio"
            else null,

            cargo = if (estadoActual.cargo.isBlank())
                "Campo obligatorio"
            else null,

            area = if (estadoActual.area.isBlank())
                "Campo obligatorio"
            else null,

            correo = if (!estadoActual.correo.contains("@"))
                "Correo inválido"
            else null,

            telefono = if (estadoActual.telefono.isBlank())
                "Campo obligatorio"
            else null,

            medio = if (estadoActual.medio.isBlank())
                "Campo obligatorio"
            else null,

            estado = if (estadoActual.estado.isBlank())
                "Campo obligatorio"
            else null
        )

        val hayErrores = listOfNotNull(
            errores.organizacionId,
            errores.nombre,
            errores.cargo,
            errores.area,
            errores.correo,
            errores.telefono,
            errores.medio,
            errores.estado,
            errores.observaciones
        ).isNotEmpty()

        _estado.update {
            it.copy(errores = errores)
        }

        return !hayErrores
    }

    // Guarda en Room: inserta si es nuevo o modifica si ya existe
    fun guardar(onListo: () -> Unit) {
        val a = _estado.value
        val contacto = ContactoEntity(
            id = a.id,
            organizacionId = a.organizacionId,
            nombre = a.nombre,
            cargo = a.cargo,
            area = a.area,
            correo = a.correo,
            telefono = a.telefono,
            medio = a.medio,
            estado = a.estado,
            observaciones = a.observaciones
        )

        viewModelScope.launch {
            if (a.id == 0L) {
                val id = dao.insertar(contacto)
                _estado.update { it.copy(id = id) }
            } else {
                dao.modificar(contacto)
            }
            onListo()
        }
    }
}
