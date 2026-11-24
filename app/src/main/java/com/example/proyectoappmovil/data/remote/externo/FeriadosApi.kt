package com.example.proyectoappmovil.data.remote.externo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

@Serializable
data class FeriadoDto(
    val date: String = "",
    @SerialName("localName") val localName: String = "",
    val name: String = "",
    val global: Boolean = false,
    val types: List<String> = emptyList()
)

interface FeriadosApi {
    @GET("api/v3/PublicHolidays/{year}/{countryCode}")
    suspend fun obtenerFeriados(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): List<FeriadoDto>
}

object FeriadoApiClient {

    const val BASE_URL = "https://date.nager.at/"
    const val PAIS = "CL"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    fun crearFeriadosApi(baseUrl: String = BASE_URL): FeriadosApi {
        require(baseUrl.endsWith("/")) { "baseUrl debe terminar en /" }
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(FeriadosApi::class.java)
    }
}
