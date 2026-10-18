package com.example.proyectoappmovil.ui.organizaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoappmovil.App
import com.example.proyectoappmovil.data.local.entity.Organizacion
import com.example.proyectoappmovil.data.model.TipoOrganizacion
import com.example.proyectoappmovil.data.repository.OrganizacionRepository
import com.example.proyectoappmovil.data.validation.OrganizacionValidaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrganizacionFormUiState(
    val nombre: String = "",
    val rubro: String = "",
    val comuna: String = "",
    val region: String = "",
    val sitioWeb: String = "",
    val publicoAsociado: String = "",
    val observaciones: String = "",
    val tipo: TipoOrganizacion? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val errores: Map<String, String> = emptyMap(),
    val esGuardando: Boolean = false,
    val mensaje: String? = null
)

class OrganizacionFormViewModel(
    private val repositorio: OrganizacionRepository,
    private val idOrganizacion: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrganizacionFormUiState())
    val uiState: StateFlow<OrganizacionFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (idOrganizacion != null) {
                repositorio.obtenerPorId(idOrganizacion)?.let { organizacion ->
                    _uiState.update {
                        it.copy(
                            nombre = organizacion.nombre,
                            rubro = organizacion.rubro,
                            comuna = organizacion.comuna,
                            region = organizacion.region,
                            sitioWeb = organizacion.sitioWeb,
                            publicoAsociado = organizacion.publicoAsociado,
                            observaciones = organizacion.observaciones,
                            tipo = organizacion.tipo,
                            latitud = organizacion.latitud,
                            longitud = organizacion.longitud
                        )
                    }
                }
            }
        }
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value, errores = it.errores - "nombre") }
    }

    fun onRubroChange(value: String) {
        _uiState.update { it.copy(rubro = value) }
    }

    fun onComunaChange(value: String) {
        _uiState.update { it.copy(comuna = value, errores = it.errores - "comuna") }
    }

    fun onRegionChange(value: String) {
        _uiState.update { it.copy(region = value, errores = it.errores - "region") }
    }

    fun onSitioWebChange(value: String) {
        _uiState.update { it.copy(sitioWeb = value, errores = it.errores - "sitioWeb") }
    }

    fun onPublicoAsociadoChange(value: String) {
        _uiState.update { it.copy(publicoAsociado = value) }
    }

    fun onObservacionesChange(value: String) {
        _uiState.update { it.copy(observaciones = value) }
    }

    fun onTipoSeleccionado(tipo: TipoOrganizacion) {
        _uiState.update { it.copy(tipo = tipo, errores = it.errores - "tipo") }
    }

    fun onUbicacionCapturada(latitud: Double, longitud: Double) {
        _uiState.update { it.copy(latitud = latitud, longitud = longitud) }
    }

    fun onQuitarUbicacion() {
        _uiState.update { it.copy(latitud = null, longitud = null) }
    }

    fun guardar() {
        val estado = _uiState.value
        val errores = OrganizacionValidaciones.validar(
            nombre = estado.nombre,
            tipo = estado.tipo,
            comuna = estado.comuna,
            region = estado.region,
            sitioWeb = estado.sitioWeb
        )

        if (errores.isNotEmpty()) {
            _uiState.update { it.copy(errores = errores) }
            return
        }

        _uiState.update { it.copy(esGuardando = true, mensaje = null) }

        viewModelScope.launch {
            val organizacion = Organizacion(
                id = idOrganizacion ?: 0,
                nombre = estado.nombre.trim(),
                tipo = estado.tipo ?: TipoOrganizacion.OTRA,
                rubro = estado.rubro.trim(),
                comuna = estado.comuna.trim(),
                region = estado.region.trim(),
                sitioWeb = estado.sitioWeb.trim(),
                publicoAsociado = estado.publicoAsociado.trim(),
                observaciones = estado.observaciones,
                latitud = estado.latitud,
                longitud = estado.longitud
            )

            if (idOrganizacion != null) {
                repositorio.actualizar(organizacion)
                _uiState.update {
                    it.copy(esGuardando = false, mensaje = "Organización actualizada")
                }
            } else {
                val idGenerado = repositorio.insertar(organizacion)
                _uiState.update {
                    it.copy(
                        esGuardando = false,
                        mensaje = if (idGenerado > 0) "Organización registrada" else "No se pudo registrar"
                    )
                }
            }
        }
    }

    companion object {
        fun Factory(idOrganizacion: Long?) = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                OrganizacionFormViewModel(app.container.organizacionRepository, idOrganizacion)
            }
        }
    }
}
