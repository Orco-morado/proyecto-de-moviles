package com.example.proyectoappmovil.data.remote


import com.google.gson.JsonObject
import retrofit2.http.GET

interface ApiService {
    @GET("api/oportunidades")
suspend fun obtenerTodas(): JsonObject
}