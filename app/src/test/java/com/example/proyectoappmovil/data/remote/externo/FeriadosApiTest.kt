package com.example.proyectoappmovil.data.remote.externo

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Locale

class FeriadosApiTest {

    private lateinit var server: MockWebServer
    private lateinit var api: FeriadosApi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = FeriadoApiClient.crearFeriadosApi(server.url("/").toString())
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun obtenerFeriados_respuesta200_deserializaDto() = runTest {
        val json = """
            [
              {
                "date": "2026-09-18",
                "localName": "Fiestas Patrias",
                "name": "Independence Day",
                "countryCode": "CL",
                "fixed": true,
                "global": true,
                "counties": null,
                "launchYear": null,
                "types": ["Public"]
              }
            ]
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val resultado = api.obtenerFeriados(2026, "CL")

        assertEquals(1, resultado.size)
        assertEquals("2026-09-18", resultado[0].date)
        assertEquals("Fiestas Patrias", resultado[0].localName)
        assertEquals("Independence Day", resultado[0].name)
        assertTrue(resultado[0].global)
        assertEquals(listOf("Public"), resultado[0].types)

        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/api/v3/PublicHolidays/2026/CL", request.path)
    }

    @Test(expected = retrofit2.HttpException::class)
    fun obtenerFeriados_respuesta500_lanzaHttpException() = runTest {
        server.enqueue(MockResponse().setResponseCode(500).setBody("error"))
        api.obtenerFeriados(2026, "CL")
    }
}

class FeriadosRepositoryTest {

    private val api: FeriadosApi = mockk()

    private fun fecha(texto: String): Long =
        SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).parse(texto)!!.time

    private fun dto(fechaIso: String, nombre: String) = FeriadoDto(
        date = fechaIso,
        localName = nombre,
        name = nombre,
        global = true,
        types = listOf("Public")
    )

    @Test
    fun obtenerProximos_filtraPasadosYOrdena() = runTest {
        val hoy = fecha("2026-09-01")
        coEvery { api.obtenerFeriados(2026, "CL") } returns listOf(
            dto("2026-01-01", "Año Nuevo"),
            dto("2026-12-25", "Navidad"),
            dto("2026-09-18", "Fiestas Patrias")
        )

        val repositorio = FeriadosRepositoryImpl(api, "CL", { hoy }, consultarAnioSiguiente = false)
        val resultado = repositorio.obtenerProximos()

        assertEquals(listOf("Fiestas Patrias", "Navidad"), resultado.map { it.nombreLocal })
        coVerify(exactly = 1) { api.obtenerFeriados(2026, "CL") }
        coVerify(exactly = 0) { api.obtenerFeriados(2027, "CL") }
    }

    @Test
    fun obtenerProximos_quedanPocos_consultaAnioSiguiente() = runTest {
        val hoy = fecha("2026-12-20")
        coEvery { api.obtenerFeriados(2026, "CL") } returns listOf(dto("2026-12-25", "Navidad"))
        coEvery { api.obtenerFeriados(2027, "CL") } returns listOf(dto("2027-01-01", "Año Nuevo"))

        val repositorio = FeriadosRepositoryImpl(api, "CL", { hoy })
        val resultado = repositorio.obtenerProximos()

        assertEquals(listOf("Navidad", "Año Nuevo"), resultado.map { it.nombreLocal })
        coVerify(exactly = 1) { api.obtenerFeriados(2027, "CL") }
    }
}
