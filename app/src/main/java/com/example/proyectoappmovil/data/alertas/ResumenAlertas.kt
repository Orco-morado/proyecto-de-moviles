package com.example.proyectoappmovil.data.alertas

object ResumenAlertas {

    const val TITULO = "Resumen de pendientes"

    fun construirTexto(pendientes: Int, vencidos: Int, enEspera: Int): String {
        if (pendientes == 0 && enEspera == 0) {
            return "Sin pendientes por ahora: no hay seguimientos ni propuestas en espera"
        }

        val partes = mutableListOf<String>()

        if (pendientes > 0) {
            val base = if (pendientes == 1) {
                "1 seguimiento pendiente"
            } else {
                "$pendientes seguimientos pendientes"
            }
            partes += if (vencidos > 0) {
                "$base ($vencidos ${if (vencidos == 1) "vencido" else "vencidos"})"
            } else {
                base
            }
        }

        if (enEspera > 0) {
            partes += if (enEspera == 1) {
                "1 propuesta en espera de respuesta"
            } else {
                "$enEspera propuestas en espera de respuesta"
            }
        }

        return partes.joinToString(" y ")
    }
}
