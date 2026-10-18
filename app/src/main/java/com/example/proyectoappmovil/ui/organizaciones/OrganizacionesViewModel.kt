package com.example.proyectoappmovil.ui.organizaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoappmovil.App
import com.example.proyectoappmovil.data.local.entity.Organizacion
import com.example.proyectoappmovil.data.repository.OrganizacionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class OrganizacionesViewModel(
    repositorio: OrganizacionRepository
) : ViewModel() {

    val organizaciones: StateFlow<List<Organizacion>> =
        repositorio.observarTodas()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                OrganizacionesViewModel(app.container.organizacionRepository)
            }
        }
    }
}
