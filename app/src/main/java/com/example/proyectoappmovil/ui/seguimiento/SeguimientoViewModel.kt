package com.example.proyectoappmovil.ui.seguimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoappmovil.App
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.local.entity.Seguimiento
import com.example.proyectoappmovil.data.model.TipoAccionSeguimiento
import com.example.proyectoappmovil.data.repository.OportunidadRepository
import com.example.proyectoappmovil.data.repository.SeguimientoRepository
import com.example.proyectoappmovil.data.validation.SeguimientoValidaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

data class SeguimientoFormUiState(
    val tipoAccion: TipoAccionSeguimiento? = null,
    val resultado: String = "",
    val proximaAccion: String = "",
    val fechaProximo: String = "",
    val observaciones: String = "",
    val fotoEvidencia: String? = null,
    val errores: Map<String, String> = emptyMap(),
    val esGuardando: Boolean = false,
    val mensaje: String? = null,
    val recordatorioProgramadoEn: Long? = null,
    val recordatorioTexto: String? = null
)

class SeguimientoViewModel(
    private val repositorio: SeguimientoRepository,
    private val oportunidadRepositorio: OportunidadRepository,
    private val oportunidadId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeguimientoFormUiState())
    val uiState: StateFlow<SeguimientoFormUiState> = _uiState.asStateFlow()

    private val _oportunidad = MutableStateFlow<Oportunidad?>(null)
    val oportunidad: StateFlow<Oportunidad?> = _oportunidad.asStateFlow()

    val seguimientos: StateFlow<List<Seguimiento>> =
        repositorio.observarPorOportunidad(oportunidadId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            _oportunidad.value = oportunidadRepositorio.obtenerPorId(oportunidadId)
        }
    }

    fun onTipoAccionSeleccionado(tipo: TipoAccionSeguimiento) {
        _uiState.update { it.copy(tipoAccion = tipo, errores = it.errores - "tipoAccion") }
    }

    fun onResultadoChange(value: String) {
        _uiState.update { it.copy(resultado = value, errores = it.errores - "resultado") }
    }

    fun onProximaAccionChange(value: String) {
        _uiState.update { it.copy(proximaAccion = value, errores = it.errores - "fechaProximo") }
    }

    fun onFechaProximoChange(value: String) {
        _uiState.update { it.copy(fechaProximo = value, errores = it.errores - "fechaProximo") }
    }

    fun onObservacionesChange(value: String) {
        _uiState.update { it.copy(observaciones = value) }
    }

    fun onFotoTomada(ruta: String) {
        _uiState.update { it.copy(fotoEvidencia = ruta, mensaje = "Evidencia fotográfica adjunta") }
    }

    fun onQuitarFoto() {
        _uiState.update { it.copy(fotoEvidencia = null) }
    }

    fun onAviso(texto: String) {
        _uiState.update { it.copy(mensaje = texto) }
    }

    fun consumirRecordatorio() {
        _uiState.update { it.copy(recordatorioProgramadoEn = null, recordatorioTexto = null) }
    }

    fun guardar() {
        val estado = _uiState.value
        val errores = SeguimientoValidaciones.validar(
            estado.tipoAccion,
            estado.resultado,
            estado.fechaProximo,
            estado.proximaAccion
        )

        if (errores.isNotEmpty()) {
            _uiState.update { it.copy(errores = errores) }
            return
        }

        _uiState.update { it.copy(esGuardando = true, mensaje = null) }

        viewModelScope.launch {
            val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fechaMillis = if (estado.fechaProximo.isNotBlank()) {
                formato.parse(estado.fechaProximo)?.time
            } else {
                null
            }

            val seguimiento = Seguimiento(
                id = 0,
                oportunidadId = oportunidadId,
                usuarioResponsableId = null,
                tipoAccion = estado.tipoAccion ?: TipoAccionSeguimiento.SEGUIMIENTO,
                fechaHora = System.currentTimeMillis(),
                resultado = estado.resultado,
                proximaAccion = estado.proximaAccion,
                fechaProximoSeguimiento = fechaMillis,
                observaciones = estado.observaciones,
                fotoEvidencia = estado.fotoEvidencia
            )

            val idGenerado = repositorio.insertar(seguimiento)

            _uiState.update {
                it.copy(
                    esGuardando = false,
                    mensaje = if (idGenerado > 0) {
                        if (fechaMillis != null) {
                            "Seguimiento registrado y recordatorio programado"
                        } else {
                            "Seguimiento registrado"
                        }
                    } else {
                        "No se pudo registrar el seguimiento"
                    },
                    recordatorioProgramadoEn = if (idGenerado > 0 && fechaMillis != null) {
                        fechaMillis + 9L * 60 * 60 * 1000
                    } else {
                        null
                    },
                    recordatorioTexto = if (idGenerado > 0 && fechaMillis != null) {
                        estado.proximaAccion.ifBlank { "Revisa el historial de la oportunidad" }
                    } else {
                        null
                    },
                    tipoAccion = null,
                    resultado = "",
                    proximaAccion = "",
                    fechaProximo = "",
                    observaciones = "",
                    fotoEvidencia = null,
                    errores = emptyMap()
                )
            }
        }
    }

    companion object {
        fun Factory(oportunidadId: Long) = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                SeguimientoViewModel(
                    app.container.seguimientoRepository,
                    app.container.oportunidadRepository,
                    oportunidadId
                )
            }
        }
    }
}
