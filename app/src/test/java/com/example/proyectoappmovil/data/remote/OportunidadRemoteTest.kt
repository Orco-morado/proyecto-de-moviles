package com.example.proyectoappmovil.data.remote

import com.example.proyectoappmovil.data.local.dao.EventoDao
import com.example.proyectoappmovil.data.local.dao.OportunidadDao
import com.example.proyectoappmovil.data.local.dao.UsuarioDao
import com.example.proyectoappmovil.data.local.entity.Evento
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.local.entity.Usuario
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class OportunidadRemoteTest {

    private lateinit var server: MockWebServer
    private lateinit var api: OportunidadApi
    private val oportunidadDao: OportunidadDao = mockk(relaxed = true)
    private val eventoDao: EventoDao = mockk()
    private val usuarioDao: UsuarioDao = mockk()

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = ApiClient.crearOportunidadApi(server.url("/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun toEntity_convierteEnumsInvalidos_aValoresPorDefecto() {
        val dto = OportunidadDto(
            id = 10L,
            nombre = "Feria Tech",
            tipo = "TIPO_INEXISTENTE",
            prioridad = "PRIORIDAD_INVALIDA",
            estado = "ESTADO_INVALIDO"
        )

        val entity = dto.toEntity(responsableIdValido = null, eventoIdValido = null)

        assertEquals(10L, entity.id)
        assertEquals("Feria Tech", entity.nombre)
        assertEquals(TipoEvento.OTRA, entity.tipo)
        assertEquals(Prioridad.MEDIA, entity.prioridad)
        assertEquals(EstadoOportunidad.EVENTO_IDENTIFICADO, entity.estado)
    }

    @Test
    fun toDto_conservaCamposDeEntidad() {
        val tipoValido = TipoEvento.entries.first()
        val estadoValido = EstadoOportunidad.entries.last()

        val entity = Oportunidad(
            id = 5L,
            nombre = "Lanzamiento",
            tipo = tipoValido,
            descripcion = "Desc",
            fuenteInformacion = "Web",
            fechaDeteccion = 1000L,
            responsableId = 2L,
            prioridad = Prioridad.ALTA,
            potencialComercial = "Alto",
            estado = estadoValido,
            eventoId = 3L,
            fechaActualizacion = 2000L,
            observaciones = "Obs"
        )

        val dto = entity.toDto()

        assertEquals(5L, dto.id)
        assertEquals("Lanzamiento", dto.nombre)
        assertEquals(tipoValido.name, dto.tipo)
        assertEquals(Prioridad.ALTA.name, dto.prioridad)
        assertEquals(estadoValido.name, dto.estado)
        assertEquals(2L, dto.responsableId)
        assertEquals(3L, dto.eventoId)
    }

    @Test
    fun obtenerTodas_conMockWebServer_deserializaYLimpiaFksInexistentes() = runTest {
        val json = """
            [
              {
                "id": 1,
                "nombre": "Expo Santiago",
                "tipo": "OTRA",
                "descripcion": "Evento masivo",
                "fuenteInformacion": "LinkedIn",
                "fechaDeteccion": 1700000000000,
                "responsableId": 99,
                "prioridad": "ALTA",
                "potencialComercial": "Alto",
                "estado": "EVENTO_IDENTIFICADO",
                "eventoId": 50,
                "fechaActualizacion": 1700000005000,
                "observaciones": "Revisar"
              }
            ]
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(json))
        coEvery { eventoDao.obtenerPorId(50L) } returns null
        coEvery { usuarioDao.obtenerPorId(99L) } returns null

        val dataSource = OportunidadRemoteDataSourceImpl(oportunidadDao, eventoDao, usuarioDao, api)
        val resultado = dataSource.obtenerTodas()

        assertEquals(1, resultado.size)
        assertEquals("Expo Santiago", resultado[0].nombre)
        assertEquals(Prioridad.ALTA, resultado[0].prioridad)
        assertNull(resultado[0].eventoId)
        assertNull(resultado[0].responsableId)
    }

    @Test
    fun crear_enviaPostYMantieneFksExistentes() = runTest {
        val respuestaJson = """
            {
              "id": 7,
              "nombre": "Activación Mall",
              "tipo": "OTRA",
              "descripcion": "",
              "fuenteInformacion": "",
              "fechaDeteccion": 0,
              "responsableId": 1,
              "prioridad": "MEDIA",
              "potencialComercial": "",
              "estado": "EVENTO_IDENTIFICADO",
              "eventoId": 2,
              "fechaActualizacion": 0,
              "observaciones": ""
            }
        """.trimIndent()

        server.enqueue(MockResponse().setResponseCode(200).setBody(respuestaJson))
        coEvery { eventoDao.obtenerPorId(2L) } returns mockk<Evento>()
        coEvery { usuarioDao.obtenerPorId(1L) } returns mockk<Usuario>()

        val dataSource = OportunidadRemoteDataSourceImpl(oportunidadDao, eventoDao, usuarioDao, api)
        val creada = dataSource.crear(
            Oportunidad(
                id = 0L,
                nombre = "Activación Mall",
                tipo = TipoEvento.OTRA,
                descripcion = "",
                fuenteInformacion = "",
                fechaDeteccion = 0L,
                responsableId = 1L,
                prioridad = Prioridad.MEDIA,
                potencialComercial = "",
                estado = EstadoOportunidad.EVENTO_IDENTIFICADO,
                eventoId = 2L,
                fechaActualizacion = 0L,
                observaciones = ""
            )
        )

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/oportunidades", request.path)
        assertEquals(7L, creada.id)
        assertEquals(1L, creada.responsableId)
        assertEquals(2L, creada.eventoId)
    }

    @Test
    fun eliminar_enviaDeleteConIdCorrecto() = runTest {
        server.enqueue(MockResponse().setResponseCode(204))

        val dataSource = OportunidadRemoteDataSourceImpl(oportunidadDao, eventoDao, usuarioDao, api)
        dataSource.eliminar(12L)

        val request = server.takeRequest()
        assertEquals("DELETE", request.method)
        assertEquals("/api/oportunidades/12", request.path)
    }
}