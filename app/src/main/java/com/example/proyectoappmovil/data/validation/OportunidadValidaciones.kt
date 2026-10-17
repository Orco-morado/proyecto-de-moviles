package com.example.proyectoappmovil.data.validation

import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

object OportunidadValidaciones {

    private val FORMATO_FECHA = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun validar(
        nombre: String,
        tipo: TipoEvento?,
        descripcion: String,
        fuenteInformacion: String,
        fechaDeteccion: String,
        prioridad: Prioridad?,
        potencialComercial: String
    ): Map<String, String> {
        val errores = mutableMapOf<String, String>()

        if (nombre.isBlank()) {
            errores["nombre"] = "El nombre es obligatorio"
        }
        if (tipo == null) {
            errores["tipo"] = "Selecciona el tipo de evento"
        }
        if (descripcion.isBlank()){
            errores["descripcion"] = "La descripcion es obligatoria"
        }
        if (fuenteInformacion.isBlank()) {
            errores["fuenteInformacion"] = "La fuente de información es obligatoria"
        }
        if (prioridad == null) {
            errores["prioridad"] = "La prioridad es obligatoria"
        }
        if (potencialComercial.isBlank()) {
            errores["potencialComercial"] = "El potencial comercial es obligatorio"
        }

        val fechaValida = try {
            FORMATO_FECHA.parse(fechaDeteccion) != null
        } catch (_: ParseException) {
            false
        }
        if (fechaDeteccion.isBlank() || !fechaValida) {
            errores["fechaDeteccion"] = "La fecha debe tener formato yyyy-MM-dd"
        }

        return errores
    }
}