package com.example.proyectoappmovil.data.validation

import com.example.proyectoappmovil.data.model.TipoOrganizacion

object OrganizacionValidaciones {

    fun validar(
        nombre: String,
        tipo: TipoOrganizacion?,
        comuna: String,
        region: String,
        sitioWeb: String
    ): Map<String, String> {
        val errores = mutableMapOf<String, String>()

        if (nombre.isBlank()) {
            errores["nombre"] = "El nombre es obligatorio"
        } else if (nombre.trim().length < 3) {
            errores["nombre"] = "El nombre debe tener al menos 3 caracteres"
        }
        if (tipo == null) {
            errores["tipo"] = "Selecciona el tipo de organización"
        }
        if (comuna.isBlank()) {
            errores["comuna"] = "La comuna es obligatoria"
        }
        if (region.isBlank()) {
            errores["region"] = "La región es obligatoria"
        }
        if (sitioWeb.isNotBlank() &&
            !sitioWeb.startsWith("http://") &&
            !sitioWeb.startsWith("https://")
        ) {
            errores["sitioWeb"] = "El sitio web debe comenzar con http:// o https://"
        }

        return errores
    }
}
