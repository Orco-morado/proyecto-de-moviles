package com.example.proyectoappmovil.ui.oportunidades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoappmovil.App
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import com.example.proyectoappmovil.data.repository.OportunidadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class OportunidadesViewModel(
    private val repositorio: OportunidadRepository
) : ViewModel() {

    val oportunidades: StateFlow<List<Oportunidad>> = repositorio.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun observarFiltradas(
        estado: EstadoOportunidad?,
        prioridad: Prioridad?,
        tipo: TipoEvento?,
        texto: String
    ): Flow<List<Oportunidad>> =
        repositorio.observarFiltradas(estado, prioridad, tipo, texto)

    suspend fun guardar(oportunidad: Oportunidad): Long = repositorio.insertar(oportunidad)

    suspend fun actualizar(oportunidad: Oportunidad) = repositorio.actualizar(oportunidad)

    suspend fun eliminar(oportunidad: Oportunidad) = repositorio.eliminar(oportunidad)

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                OportunidadesViewModel(app.container.oportunidadRepository)
            }
        }
    }
}
