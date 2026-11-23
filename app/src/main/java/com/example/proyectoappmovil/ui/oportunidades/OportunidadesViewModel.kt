package com.example.proyectoappmovil.ui.oportunidades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoappmovil.App
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.local.entity.Usuario
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.PerfilUsuario
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import com.example.proyectoappmovil.data.repository.OportunidadRepository
import com.example.proyectoappmovil.data.repository.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class OportunidadesViewModel(
    private val repositorio: OportunidadRepository,
    private val sessionManager: SessionManager = SessionManager()
) : ViewModel() {

    val oportunidades: StateFlow<List<Oportunidad>> = repositorio.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val usuarioActual: StateFlow<Usuario?> = sessionManager.currentUser

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun puedeEliminar(oportunidad: Oportunidad, usuario: Usuario?): Boolean {
        if (usuario == null) return false
        val esAdmin = usuario.perfil == PerfilUsuario.SUPERVISOR ||
                usuario.username.equals("admin", ignoreCase = true)
        val esCreador = oportunidad.responsableId == usuario.id
        return esAdmin || esCreador
    }

    fun refrescar() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                repositorio.refrescar()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al sincronizar oportunidades"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun eliminarOportunidad(oportunidad: Oportunidad, onExito: () -> Unit = {}) {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            try {
                repositorio.eliminar(oportunidad)
                onExito()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al eliminar oportunidad"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun reintentar() {
        refrescar()
    }

    fun limpiarError() {
        _error.value = null
    }

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
                OportunidadesViewModel(
                    repositorio = app.container.oportunidadRepository,
                    sessionManager = app.container.sessionManager
                )
            }
        }
    }
}