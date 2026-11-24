package com.example.proyectoappmovil.ui.feriados

import com.example.proyectoappmovil.data.MainDispatcherRule
import com.example.proyectoappmovil.data.remote.externo.Feriado
import com.example.proyectoappmovil.data.remote.externo.FeriadosRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class FeriadosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repositorio: FeriadosRepository = mockk()

    private fun feriado() = Feriado(
        fechaMillis = 1_700_000_000_000L,
        nombre = "Navidad",
        nombreLocal = "Navidad",
        global = true,
        tipos = listOf("Public")
    )

    @Test
    fun cargar_exito_muestraFeriados() = runTest {
        coEvery { repositorio.obtenerProximos() } returns listOf(feriado())
        val vm = FeriadosViewModel(repositorio)

        advanceUntilIdle()

        val estado = vm.uiState.value
        assertEquals(1, (estado as FeriadosUiState.Exito).feriados.size)
        assertEquals("Navidad", estado.feriados[0].nombre)
    }

    @Test
    fun cargar_vacio_muestraEstadoVacio() = runTest {
        coEvery { repositorio.obtenerProximos() } returns emptyList()
        val vm = FeriadosViewModel(repositorio)

        advanceUntilIdle()

        assertEquals(FeriadosUiState.Vacio, vm.uiState.value)
    }

    @Test
    fun cargar_error_muestraMensaje() = runTest {
        coEvery { repositorio.obtenerProximos() } throws IOException("Sin conexión")
        val vm = FeriadosViewModel(repositorio)

        advanceUntilIdle()

        assertEquals("Sin conexión", (vm.uiState.value as FeriadosUiState.Error).mensaje)
    }

    @Test
    fun reintentar_recuperaErrorYVuelveACargar() = runTest {
        var llamadas = 0
        coEvery { repositorio.obtenerProximos() } answers {
            llamadas++
            if (llamadas == 1) throw IOException("Fallo temporal") else listOf(feriado())
        }
        val vm = FeriadosViewModel(repositorio)
        advanceUntilIdle()
        assertEquals(FeriadosUiState.Error::class, vm.uiState.value::class)

        vm.reintentar()
        advanceUntilIdle()

        assertEquals(FeriadosUiState.Exito::class, vm.uiState.value::class)
        assertEquals(2, llamadas)
    }
}
