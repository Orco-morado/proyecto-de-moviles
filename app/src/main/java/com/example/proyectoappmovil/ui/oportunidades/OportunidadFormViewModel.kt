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
import com.example.proyectoappmovil.data.validation.OportunidadValidaciones
import com.example.proyectoappmovil.data.repository.OportunidadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OportunidadFormUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val fuenteInformacion: String = "",
    val fechaDeteccion: String = "",
    val potencialComercial: String = "",
    val observaciones: String = "",
    val tipo: TipoEvento? = null,
    val prioridad: Prioridad? = null,
    val errores: Map<String, String> = emptyMap(),
    val esGuardando: Boolean = false,
    val mensaje: String? = null
)

class OportunidadFormViewModel(
    private val repositorio: OportunidadRepository,
    idOportunidad: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(OportunidadFormUiState())
    val uiState: StateFlow<OportunidadFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (idOportunidad != null) {
                repositorio.obtenerPorId(idOportunidad)?.let { oportunidad ->
                    _uiState.update {
                        it.copy(
                            nombre = oportunidad.nombre,
                            descripcion = oportunidad.descripcion,
                            fuenteInformacion = oportunidad.fuenteInformacion,
                            fechaDeteccion = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(oportunidad.fechaDeteccion),
                            potencialComercial = oportunidad.potencialComercial,
                            observaciones = oportunidad.observaciones,
                            tipo = oportunidad.tipo,
                            prioridad = oportunidad.prioridad
                        )
                    }
                }
            }
        }
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value, errores = it.errores - "nombre") }
    }

    fun onDescripcionChange(value: String) {
        _uiState.update { it.copy(descripcion = value) }
    }

    fun onFuenteInformacionChange(value: String) {
        _uiState.update { it.copy(fuenteInformacion = value, errores = it.errores - "fuenteInformacion") }
    }

    fun onFechaDeteccionChange(value: String) {
        _uiState.update { it.copy(fechaDeteccion = value, errores = it.errores - "fechaDeteccion") }
    }

    fun onPotencialComercialChange(value: String) {
        _uiState.update { it.copy(potencialComercial = value) }
    }

    fun onObservacionesChange(value: String) {
        _uiState.update { it.copy(observaciones = value) }
    }

    fun onTipoSeleccionado(tipo: TipoEvento) {
        _uiState.update { it.copy(tipo = tipo, errores = it.errores - "tipo") }
    }

    fun onPrioridadSeleccionada(prioridad: Prioridad) {
        _uiState.update { it.copy(prioridad = prioridad, errores = it.errores - "prioridad") }
    }

    fun guardar() {
        val estado = _uiState.value
        val errores = OportunidadValidaciones.validar(
            estado.nombre,
            estado.tipo,
            estado.fuenteInformacion,
            estado.fechaDeteccion
        )

        if (errores.isNotEmpty()) {
            _uiState.update { it.copy(errores = errores) }
            return
        }

        _uiState.update { it.copy(esGuardando = true, mensaje = null) }

        viewModelScope.launch {
            val formato = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val fechaMillis = formato.parse(estado.fechaDeteccion)?.time ?: System.currentTimeMillis()
            val oportunidad = Oportunidad(
                id = 0,
                nombre = estado.nombre,
                tipo = estado.tipo ?: TipoEvento.OTRA,
                descripcion = estado.descripcion,
                fuenteInformacion = estado.fuenteInformacion,
                fechaDeteccion = fechaMillis,
                responsableId = null,
                prioridad = estado.prioridad ?: Prioridad.MEDIA,
                potencialComercial = estado.potencialComercial,
                estado = EstadoOportunidad.EVENTO_IDENTIFICADO,
                eventoId = null,
                fechaActualizacion = System.currentTimeMillis(),
                observaciones = estado.observaciones
            )

            val idGenerado = repositorio.insertar(oportunidad)

            _uiState.update {
                it.copy(esGuardando = false, mensaje = if (idGenerado > 0) "Oportunidad registrada" else "No se pudo registrar")
            }
        }
    }

    companion object {
        fun Factory(idOportunidad: Long?) = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                OportunidadFormViewModel(app.container.oportunidadRepository, idOportunidad)
            }
        }
    }
}
