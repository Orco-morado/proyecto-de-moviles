package com.example.proyectoappmovil.data.validation

import com.example.proyectoappmovil.data.model.TipoAccionSeguimiento
import com.example.proyectoappmovil.utils.FechaUtils

object SeguimientoValidaciones {

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
        if (fechaProximo.isNotBlank() && !FechaUtils.esFechaValida(fechaProximo)) {
            errores["fechaProximo"] = "La fecha debe tener formato dd/MM/yyyy"
        }

        return errores
    }
}
