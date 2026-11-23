package com.example.proyectoappmovil.utils

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FechaUtils {

    const val PATRON_FECHA = "dd/MM/yyyy"
    const val PATRON_FECHA_HORA = "dd/MM/yyyy HH:mm"

    fun formatearFecha(millis: Long): String =
        SimpleDateFormat(PATRON_FECHA, Locale.getDefault()).format(Date(millis))

    fun formatearFechaHora(millis: Long): String =
        SimpleDateFormat(PATRON_FECHA_HORA, Locale.getDefault()).format(Date(millis))

    fun parsearFecha(texto: String): Long? {
        val limpio = texto.trim()
        if (limpio.isEmpty()) return null
        val formato = SimpleDateFormat(PATRON_FECHA, Locale.getDefault()).apply {
            isLenient = false
        }
        val posicion = ParsePosition(0)
        val fecha = formato.parse(limpio, posicion) ?: return null
        return if (posicion.index == limpio.length) fecha.time else null
    }

    fun esFechaValida(texto: String): Boolean = parsearFecha(texto) != null
}

fun Long.toFechaCorta(): String = FechaUtils.formatearFecha(this)

fun Long.toFechaHora(): String = FechaUtils.formatearFechaHora(this)
