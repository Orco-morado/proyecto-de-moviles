package com.example.proyectoappmovil.data.remote.externo

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class Feriado(
    val fechaMillis: Long,
    val nombre: String,
    val nombreLocal: String,
    val global: Boolean,
    val tipos: List<String>
)

interface FeriadosRepository {
    suspend fun obtenerProximos(): List<Feriado>
}

class FeriadosRepositoryImpl(
    private val api: FeriadosApi = FeriadoApiClient.crearFeriadosApi(),
    private val pais: String = FeriadoApiClient.PAIS,
    private val reloj: () -> Long = System::currentTimeMillis,
    private val consultarAnioSiguiente: Boolean = true
) : FeriadosRepository {

    override suspend fun obtenerProximos(): List<Feriado> {
        val hoy = reloj()
        val calendario = Calendar.getInstance().apply { timeInMillis = hoy }
        val anioActual = calendario.get(Calendar.YEAR)
        val anioSiguiente = anioActual + 1

        val feriadosAnioActual = api.obtenerFeriados(anioActual, pais)
        val feriados = feriadosAnioActual.toMutableList()

        val proximosDelAnio = feriadosAnioActual.count { esFuturo(it.date, hoy) }
        if (consultarAnioSiguiente && proximosDelAnio < MINIMO_PROXIMOS) {
            feriados.addAll(api.obtenerFeriados(anioSiguiente, pais))
        }

        return feriados
            .mapNotNull { dto -> dto.aFeriado() }
            .filter { it.fechaMillis >= inicioDelDia(hoy) }
            .distinctBy { it.fechaMillis to it.nombreLocal }
            .sortedBy { it.fechaMillis }
    }

    private fun esFuturo(fechaIso: String, hoy: Long): Boolean =
        parsearFechaIso(fechaIso)?.let { it >= inicioDelDia(hoy) } ?: false

    private fun FeriadoDto.aFeriado(): Feriado? {
        val fecha = parsearFechaIso(date) ?: return null
        return Feriado(
            fechaMillis = fecha,
            nombre = name.ifBlank { localName },
            nombreLocal = localName.ifBlank { name },
            global = global,
            tipos = types
        )
    }

    private fun parsearFechaIso(texto: String): Long? {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }
        val posicion = ParsePosition(0)
        val fecha = formato.parse(texto, posicion) ?: return null
        return if (posicion.index == texto.length) fecha.time else null
    }

    private fun inicioDelDia(millis: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private companion object {
        const val MINIMO_PROXIMOS = 3
    }
}
