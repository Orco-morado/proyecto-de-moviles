package com.example.proyectoappmovil.ui.contactos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoappmovil.App
import com.example.proyectoappmovil.data.local.entity.Contacto
import com.example.proyectoappmovil.data.model.AreaContacto
import com.example.proyectoappmovil.data.model.EstadoContacto
import com.example.proyectoappmovil.data.model.MedioContacto
import com.example.proyectoappmovil.data.repository.ContactoRepository
import com.example.proyectoappmovil.data.validation.ContactoValidaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContactoFormUiState(
    val nombre: String = "",
    val cargo: String = "",
    val correo: String = "",
    val telefono: String = "",
    val observaciones: String = "",
    val area: AreaContacto = AreaContacto.EVENTOS,
    val medioPreferente: MedioContacto = MedioContacto.CORREO,
    val estado: EstadoContacto = EstadoContacto.ACTIVO,
    val errores: Map<String, String> = emptyMap(),
    val esGuardando: Boolean = false,
    val mensaje: String? = null
)

class ContactoFormViewModel(
    private val repositorio: ContactoRepository,
    private val organizacionId: Long,
    private val idContacto: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactoFormUiState())
    val uiState: StateFlow<ContactoFormUiState> = _uiState.asStateFlow()

    private var fechaUltimoContacto: Long? = null

    init {
        viewModelScope.launch {
            if (idContacto != null) {
                repositorio.obtenerPorId(idContacto)?.let { contacto ->
                    fechaUltimoContacto = contacto.fechaUltimoContacto
                    _uiState.update {
                        it.copy(
                            nombre = contacto.nombre,
                            cargo = contacto.cargo,
                            correo = contacto.correo,
                            telefono = contacto.telefono,
                            observaciones = contacto.observaciones,
                            area = contacto.area,
                            medioPreferente = contacto.medioPreferente,
                            estado = contacto.estado
                        )
                    }
                }
            }
        }
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value, errores = it.errores - "nombre") }
    }

    fun onCargoChange(value: String) {
        _uiState.update { it.copy(cargo = value, errores = it.errores - "cargo") }
    }

    fun onCorreoChange(value: String) {
        _uiState.update { it.copy(correo = value, errores = it.errores - "correo") }
    }

    fun onTelefonoChange(value: String) {
        _uiState.update { it.copy(telefono = value, errores = it.errores - "telefono") }
    }

    fun onObservacionesChange(value: String) {
        _uiState.update { it.copy(observaciones = value) }
    }

    fun onAreaSeleccionada(area: AreaContacto) {
        _uiState.update { it.copy(area = area) }
    }

    fun onMedioSeleccionado(medio: MedioContacto) {
        _uiState.update { it.copy(medioPreferente = medio) }
    }

    fun onEstadoSeleccionado(estado: EstadoContacto) {
        _uiState.update { it.copy(estado = estado) }
    }

    fun guardar() {
        val estado = _uiState.value
        val errores = ContactoValidaciones.validar(
            nombre = estado.nombre,
            cargo = estado.cargo,
            correo = estado.correo,
            telefono = estado.telefono
        )

        if (errores.isNotEmpty()) {
            _uiState.update { it.copy(errores = errores) }
            return
        }

        _uiState.update { it.copy(esGuardando = true, mensaje = null) }

        viewModelScope.launch {
            val contacto = Contacto(
                id = idContacto ?: 0,
                nombre = estado.nombre.trim(),
                organizacionId = organizacionId,
                cargo = estado.cargo.trim(),
                area = estado.area,
                correo = estado.correo.trim(),
                telefono = estado.telefono.trim(),
                medioPreferente = estado.medioPreferente,
                estado = estado.estado,
                fechaUltimoContacto = fechaUltimoContacto,
                observaciones = estado.observaciones
            )

            if (idContacto != null) {
                repositorio.actualizar(contacto)
                _uiState.update {
                    it.copy(esGuardando = false, mensaje = "Contacto actualizado")
                }
            } else {
                val idGenerado = repositorio.insertar(contacto)
                _uiState.update {
                    it.copy(
                        esGuardando = false,
                        mensaje = if (idGenerado > 0) "Contacto registrado" else "No se pudo registrar"
                    )
                }
            }
        }
    }

    companion object {
        fun Factory(organizacionId: Long, idContacto: Long?) = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                ContactoFormViewModel(app.container.contactoRepository, organizacionId, idContacto)
            }
        }
    }
}
