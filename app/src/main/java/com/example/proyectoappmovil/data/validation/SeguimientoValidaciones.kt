package com.example.proyectoappmovil.data.validation

import com.example.proyectoappmovil.data.model.TipoAccionSeguimiento
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

object SeguimientoValidaciones {

    private val FORMATO_FECHA = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun validar(
        tipoAccion: TipoAccionSeguimiento?,
        resultado: String,
        fechaProximo: String,
        proximaAccion: String
    ): Map<String, String> {
        val errores = mutableMapOf<String, String>()

        if (tipoAccion == null) {
            errores["tipoAccion"] = "Selecciona el tipo de acción"
        }
        if (resultado.isBlank()) {
            errores["resultado"] = "El resultado de la gestión es obligatorio"
        }
        if (proximaAccion.isNotBlank() && fechaProximo.isBlank()) {
            errores["fechaProximo"] = "Indica la fecha del próximo seguimiento"
        }
        if (fechaProximo.isNotBlank()) {
            val fechaValida = try {
                FORMATO_FECHA.parse(fechaProximo) != null
            } catch (_: ParseException) {
                false
            }
            if (!fechaValida) {
                errores["fechaProximo"] = "La fecha debe tener formato yyyy-MM-dd"
            }
        }

        return errores
    }
}
