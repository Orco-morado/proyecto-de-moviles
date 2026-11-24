package com.example.proyectoappmovil.ui.feriados

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectoappmovil.data.remote.externo.Feriado
import com.example.proyectoappmovil.data.remote.externo.FeriadoApiClient
import com.example.proyectoappmovil.data.remote.externo.FeriadosRepository
import com.example.proyectoappmovil.data.remote.externo.FeriadosRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class FeriadoUi(
    val fechaMillis: Long,
    val fechaTexto: String,
    val nombre: String,
    val diasFaltan: Int
)

sealed interface FeriadosUiState {
    data object Cargando : FeriadosUiState
    data class Exito(val feriados: List<FeriadoUi>) : FeriadosUiState
    data class Error(val mensaje: String) : FeriadosUiState
    data object Vacio : FeriadosUiState
}

class FeriadosViewModel(
    private val repositorio: FeriadosRepository,
    private val formatearFecha: (Long) -> String = { com.example.proyectoappmovil.utils.FechaUtils.formatearFecha(it) },
    private val reloj: () -> Long = System::currentTimeMillis
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeriadosUiState>(FeriadosUiState.Cargando)
    val uiState: StateFlow<FeriadosUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = FeriadosUiState.Cargando
            try {
                val feriados = repositorio.obtenerProximos()
                _uiState.value = if (feriados.isEmpty()) {
                    FeriadosUiState.Vacio
                } else {
                    FeriadosUiState.Exito(feriados.map { it.aUi() })
                }
            } catch (e: Exception) {
                _uiState.value = FeriadosUiState.Error(
                    e.message ?: "Error al cargar feriados"
                )
            }
        }
    }

    fun reintentar() = cargar()

    private fun Feriado.aUi(): FeriadoUi {
        val hoy = Calendar.getInstance().apply {
            timeInMillis = reloj()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val dias = ((fechaMillis - hoy) / MILISEGUNDOS_POR_DIA).toInt().coerceAtLeast(0)
        return FeriadoUi(
            fechaMillis = fechaMillis,
            fechaTexto = formatearFecha(fechaMillis),
            nombre = nombreLocal.ifBlank { nombre },
            diasFaltan = dias
        )
    }

    companion object {
        private const val MILISEGUNDOS_POR_DIA = 24L * 60 * 60 * 1000

        val Factory = viewModelFactory {
            initializer {
                val repositorio = FeriadosRepositoryImpl(
                    api = FeriadoApiClient.crearFeriadosApi()
                )
                FeriadosViewModel(repositorio = repositorio)
            }
        }
    }
}
