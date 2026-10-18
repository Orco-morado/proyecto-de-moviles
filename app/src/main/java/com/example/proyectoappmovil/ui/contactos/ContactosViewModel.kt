package com.example.proyectoappmovil.ui.contactos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoappmovil.App
import com.example.proyectoappmovil.data.local.entity.Contacto
import com.example.proyectoappmovil.data.repository.ContactoRepository
import com.example.proyectoappmovil.data.repository.OrganizacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactosViewModel(
    contactoRepository: ContactoRepository,
    organizacionRepository: OrganizacionRepository,
    organizacionId: Long
) : ViewModel() {

    val contactos: StateFlow<List<Contacto>> =
        contactoRepository.observarPorOrganizacion(organizacionId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _organizacionNombre = MutableStateFlow("")
    val organizacionNombre: StateFlow<String> = _organizacionNombre.asStateFlow()

    init {
        viewModelScope.launch {
            organizacionRepository.obtenerPorId(organizacionId)?.let { organizacion ->
                _organizacionNombre.value = organizacion.nombre
            }
        }
    }

    companion object {
        fun Factory(organizacionId: Long) = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                ContactosViewModel(
                    app.container.contactoRepository,
                    app.container.organizacionRepository,
                    organizacionId
                )
            }
        }
    }
}
