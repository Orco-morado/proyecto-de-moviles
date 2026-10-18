package com.example.proyectoappmovil.camara

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object CamaraHelper {

    private const val CARPETA_EVIDENCIAS = "evidencias"

    fun crearArchivo(context: Context): File {
        val carpeta = File(context.filesDir, CARPETA_EVIDENCIAS)
        if (!carpeta.exists()) {
            carpeta.mkdirs()
        }
        return File(carpeta, "evidencia_${System.currentTimeMillis()}.jpg")
    }

    fun uriParaArchivo(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )

    fun cargarMiniatura(ruta: String, tamanoMaximo: Int = 512): Bitmap? {
        val archivo = File(ruta)
        if (!archivo.exists()) return null

        val medicion = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(ruta, medicion)

        var escala = 1
        while (medicion.outWidth / escala > tamanoMaximo ||
            medicion.outHeight / escala > tamanoMaximo
        ) {
            escala *= 2
        }

        val opciones = BitmapFactory.Options().apply { inSampleSize = escala }
        return BitmapFactory.decodeFile(ruta, opciones)
    }
}
