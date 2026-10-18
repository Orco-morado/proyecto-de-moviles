package com.example.proyectoappmovil.ui.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoappmovil.App
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.local.entity.Seguimiento
import com.example.proyectoappmovil.data.repository.OportunidadRepository
import com.example.proyectoappmovil.data.repository.SeguimientoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

private const val DIAS_VENTANA_ALERTA = 7L
private const val MILISEGUNDOS_DIA = 24L * 60L * 60L * 1000L

class AlertasViewModel(
    seguimientoRepository: SeguimientoRepository,
    oportunidadRepository: OportunidadRepository
) : ViewModel() {

    val seguimientosPendientes: StateFlow<List<Seguimiento>> =
        seguimientoRepository.observarPendientes(
            hasta = System.currentTimeMillis() + DIAS_VENTANA_ALERTA * MILISEGUNDOS_DIA
        ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val oportunidadesEnEspera: StateFlow<List<Oportunidad>> =
        oportunidadRepository.observarEnEsperaRespuesta()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                AlertasViewModel(
                    app.container.seguimientoRepository,
                    app.container.oportunidadRepository
                )
            }
        }
    }
}
