package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.remote.RetroFitInstance
import com.google.gson.JsonObject

class OportunidadRepositoryPost {
    suspend fun getEventosPosts(): JsonObject {
        return RetroFitInstance.api.obtenerTodas()
    }
}