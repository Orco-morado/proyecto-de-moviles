// CAMBIO REALIZADO: Se agregó toFirestoreJson() para envolver el POST y PATCH en {"fields": ...}
// con stringValue/integerValue, y se usa documentId para que actualizar y eliminar funcionen por ID.

package com.example.proyectoappmovil.data.remote

import com.example.proyectoappmovil.BuildConfig
import com.example.proyectoappmovil.data.local.dao.EventoDao
import com.example.proyectoappmovil.data.local.dao.OportunidadDao
import com.example.proyectoappmovil.data.local.dao.UsuarioDao
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import com.example.proyectoappmovil.data.repository.OportunidadRemoteDataSource
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class OportunidadDto(
    val id: Long = 0,
    val nombre: String = "",
    val tipo: String = TipoEvento.OTRA.name,
    val descripcion: String = "",
    val fuenteInformacion: String = "",
    val fechaDeteccion: Long = 0L,
    val responsableId: Long? = null,
    val prioridad: String = Prioridad.MEDIA.name,
    val potencialComercial: String = "",
    val estado: String = EstadoOportunidad.EVENTO_IDENTIFICADO.name,
    val eventoId: Long? = null,
    val fechaActualizacion: Long = 0L,
    val observaciones: String = ""
)

interface OportunidadApi {

    @GET("purpletoken")
    suspend fun obtenerTodas(): JsonObject

    @POST("purpletoken")
    suspend fun crear(
        @Query("documentId") documentId: String,
        @Body body: JsonObject
    ): JsonObject

    @PATCH("purpletoken/{id}")
    suspend fun actualizar(
        @Path("id") id: String,
        @Body body: JsonObject
    ): JsonObject

    @DELETE("purpletoken/{id}")
    suspend fun eliminar(@Path("id") id: String)
}

object ApiClient {

    fun crearOportunidadApi(baseUrl: String = BuildConfig.API_BASE_URL): OportunidadApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OportunidadApi::class.java)
    }

    val oportunidadApi: OportunidadApi by lazy {
        crearOportunidadApi()
    }
}

fun OportunidadDto.toEntity(
    responsableIdValido: Long?,
    eventoIdValido: Long?
): Oportunidad {
    val tipoEnum = runCatching { TipoEvento.valueOf(tipo) }
        .getOrDefault(TipoEvento.OTRA)
    val prioridadEnum = runCatching { Prioridad.valueOf(prioridad) }
        .getOrDefault(Prioridad.MEDIA)
    val estadoEnum = runCatching { EstadoOportunidad.valueOf(estado) }
        .getOrDefault(EstadoOportunidad.EVENTO_IDENTIFICADO)

    return Oportunidad(
        id = id,
        nombre = nombre,
        tipo = tipoEnum,
        descripcion = descripcion,
        fuenteInformacion = fuenteInformacion,
        fechaDeteccion = fechaDeteccion,
        responsableId = responsableIdValido,
        prioridad = prioridadEnum,
        potencialComercial = potencialComercial,
        estado = estadoEnum,
        eventoId = eventoIdValido,
        fechaActualizacion = fechaActualizacion,
        observaciones = observaciones
    )
}

fun Oportunidad.toDto(): OportunidadDto {
    return OportunidadDto(
        id = id,
        nombre = nombre,
        tipo = tipo.name,
        descripcion = descripcion,
        fuenteInformacion = fuenteInformacion,
        fechaDeteccion = fechaDeteccion,
        responsableId = responsableId,
        prioridad = prioridad.name,
        potencialComercial = potencialComercial,
        estado = estado.name,
        eventoId = eventoId,
        fechaActualizacion = fechaActualizacion,
        observaciones = observaciones
    )
}

private fun OportunidadDto.toFirestoreJson(): JsonObject {
    fun strField(value: String) = JsonObject().apply { addProperty("stringValue", value) }
    fun intField(value: Long) = JsonObject().apply { addProperty("integerValue", value.toString()) }

    val fields = JsonObject().apply {
        add("id", intField(id))
        add("nombre", strField(nombre))
        add("tipo", strField(tipo))
        add("descripcion", strField(descripcion))
        add("fuenteInformacion", strField(fuenteInformacion))
        add("fechaDeteccion", intField(fechaDeteccion))
        if (responsableId != null) add("responsableId", intField(responsableId))
        add("prioridad", strField(prioridad))
        add("potencialComercial", strField(potencialComercial))
        add("estado", strField(estado))
        if (eventoId != null) add("eventoId", intField(eventoId))
        add("fechaActualizacion", intField(fechaActualizacion))
        add("observaciones", strField(observaciones))
    }

    return JsonObject().apply { add("fields", fields) }
}

private fun JsonObject.toOportunidadDto(): OportunidadDto {
    val fields = getAsJsonObject("fields")
    return OportunidadDto(
        id = fields?.getAsJsonObject("id")?.get("integerValue")?.asLong ?: 0L,
        nombre = fields?.getAsJsonObject("nombre")?.get("stringValue")?.asString ?: "",
        tipo = fields?.getAsJsonObject("tipo")?.get("stringValue")?.asString ?: TipoEvento.OTRA.name,
        descripcion = fields?.getAsJsonObject("descripcion")?.get("stringValue")?.asString ?: "",
        fuenteInformacion = fields?.getAsJsonObject("fuenteInformacion")?.get("stringValue")?.asString ?: "",
        fechaDeteccion = fields?.getAsJsonObject("fechaDeteccion")?.get("integerValue")?.asLong ?: 0L,
        responsableId = fields?.getAsJsonObject("responsableId")?.get("integerValue")?.asLong,
        prioridad = fields?.getAsJsonObject("prioridad")?.get("stringValue")?.asString ?: Prioridad.MEDIA.name,
        potencialComercial = fields?.getAsJsonObject("potencialComercial")?.get("stringValue")?.asString ?: "",
        estado = fields?.getAsJsonObject("estado")?.get("stringValue")?.asString ?: EstadoOportunidad.EVENTO_IDENTIFICADO.name,
        eventoId = fields?.getAsJsonObject("eventoId")?.get("integerValue")?.asLong,
        fechaActualizacion = fields?.getAsJsonObject("fechaActualizacion")?.get("integerValue")?.asLong ?: 0L,
        observaciones = fields?.getAsJsonObject("observaciones")?.get("stringValue")?.asString ?: ""
    )
}

class OportunidadRemoteDataSourceImpl(
    private val oportunidadDao: OportunidadDao,
    private val eventoDao: EventoDao,
    private val usuarioDao: UsuarioDao,
    private val api: OportunidadApi = ApiClient.oportunidadApi
) : OportunidadRemoteDataSource {

    private suspend fun mapearConFkSegura(dto: OportunidadDto): Oportunidad {
        val eventoValido = dto.eventoId?.let { id ->
            if (eventoDao.obtenerPorId(id) != null) id else null
        }
        val responsableValido = dto.responsableId?.let { id ->
            if (usuarioDao.obtenerPorId(id) != null) id else null
        }
        return dto.toEntity(
            responsableIdValido = responsableValido,
            eventoIdValido = eventoValido
        )
    }

    override suspend fun obtenerTodas(): List<Oportunidad> {
        val json = api.obtenerTodas()
        val documents = json.getAsJsonArray("documents") ?: return emptyList()
        return documents.map { element ->
            mapearConFkSegura(element.asJsonObject.toOportunidadDto())
        }
    }

    override suspend fun crear(oportunidad: Oportunidad): Oportunidad {
        val idFinal = if (oportunidad.id == 0L) System.currentTimeMillis() else oportunidad.id
        val dto = oportunidad.copy(id = idFinal).toDto()
        val respuesta = api.crear(idFinal.toString(), dto.toFirestoreJson())
        return mapearConFkSegura(respuesta.toOportunidadDto())
    }

    override suspend fun actualizar(oportunidad: Oportunidad): Oportunidad {
        val dto = oportunidad.toDto()
        val respuesta = api.actualizar(oportunidad.id.toString(), dto.toFirestoreJson())
        return mapearConFkSegura(respuesta.toOportunidadDto())
    }

    override suspend fun eliminar(id: Long) {
        api.eliminar(id.toString())
    }
}