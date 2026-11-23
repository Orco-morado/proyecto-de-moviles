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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

@Serializable
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

    @GET("api/oportunidades")
    suspend fun obtenerTodas(): List<OportunidadDto>

    @POST("api/oportunidades")
    suspend fun crear(@Body oportunidad: OportunidadDto): OportunidadDto

    @PUT("api/oportunidades/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body oportunidad: OportunidadDto
    ): OportunidadDto

    @DELETE("api/oportunidades/{id}")
    suspend fun eliminar(@Path("id") id: Long)
}

object ApiClient {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

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
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
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
        return api.obtenerTodas().map { mapearConFkSegura(it) }
    }

    override suspend fun crear(oportunidad: Oportunidad): Oportunidad {
        val respuesta = api.crear(oportunidad.toDto())
        return mapearConFkSegura(respuesta)
    }

    override suspend fun actualizar(oportunidad: Oportunidad): Oportunidad {
        val respuesta = api.actualizar(oportunidad.id, oportunidad.toDto())
        return mapearConFkSegura(respuesta)
    }

    override suspend fun eliminar(id: Long) {
        api.eliminar(id)
    }
}