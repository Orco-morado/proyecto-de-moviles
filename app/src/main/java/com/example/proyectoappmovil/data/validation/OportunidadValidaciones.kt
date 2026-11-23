package com.example.proyectoappmovil.data.validation

import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import com.example.proyectoappmovil.utils.FechaUtils

object OportunidadValidaciones {

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

        if (fechaDeteccion.isBlank() || !FechaUtils.esFechaValida(fechaDeteccion)) {
            errores["fechaDeteccion"] = "La fecha debe tener formato dd/MM/yyyy"
        }

        return errores
    }
}