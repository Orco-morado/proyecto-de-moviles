package com.example.proyectoappmovil.data.validation

object ContactoValidaciones {

    private val PATRON_CORREO = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private val PATRON_TELEFONO = Regex("^[+0-9][0-9 ]{7,}$")

    fun validar(
        nombre: String,
        cargo: String,
        correo: String,
        telefono: String
    ): Map<String, String> {
        val errores = mutableMapOf<String, String>()

        if (nombre.isBlank()) {
            errores["nombre"] = "El nombre es obligatorio"
        }
        if (cargo.isBlank()) {
            errores["cargo"] = "El cargo es obligatorio"
        }
        if (correo.isBlank()) {
            errores["correo"] = "El correo es obligatorio"
        } else if (!PATRON_CORREO.matches(correo.trim())) {
            errores["correo"] = "El correo no tiene un formato válido"
        }
        if (telefono.isBlank()) {
            errores["telefono"] = "El teléfono es obligatorio"
        } else if (!PATRON_TELEFONO.matches(telefono.trim())) {
            errores["telefono"] = "El teléfono debe tener al menos 8 dígitos"
        }

        return errores
    }
}
