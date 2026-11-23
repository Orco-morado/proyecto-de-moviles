package com.example.proyectoappmovil.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.proyectoappmovil.data.local.AppDatabase
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.local.entity.ResultadoActivacion
import com.example.proyectoappmovil.data.local.entity.Seguimiento
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoAccionSeguimiento
import com.example.proyectoappmovil.data.model.TipoEvento
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OportunidadRepositoryImplTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var baseDeDatos: AppDatabase
    private lateinit var fuenteRemota: OportunidadRemoteDataSource
    private lateinit var repositorio: OportunidadRepositoryImpl

    @Before
    fun setUp() {
        baseDeDatos = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
        fuenteRemota = mockk()
        repositorio = OportunidadRepositoryImpl(baseDeDatos.oportunidadDao(), fuenteRemota)
    }

    @After
    fun tearDown() {
        baseDeDatos.close()
    }

    @Test
    fun refrescar_sincronizaSinBorrarSeguimientosDeOportunidadesExistentes() = runTest {
        val oportunidadExistente = crearOportunidad(1L, "Original")
        val oportunidadEliminada = crearOportunidad(3L, "Eliminada")
        baseDeDatos.oportunidadDao().insertar(oportunidadExistente)
        baseDeDatos.oportunidadDao().insertar(oportunidadEliminada)

        baseDeDatos.seguimientoDao().insertar(
            Seguimiento(
                oportunidadId = 1L,
                usuarioResponsableId = null,
                tipoAccion = TipoAccionSeguimiento.PRIMER_CONTACTO,
                fechaHora = 1000L,
                resultado = "Contacto realizado",
                proximaAccion = "Enviar propuesta",
                fechaProximoSeguimiento = null,
                observaciones = ""
            )
        )
        baseDeDatos.resultadoActivacionDao().guardar(
            ResultadoActivacion(
                oportunidadId = 1L,
                fechaRealizacion = 1500L,
                estadoFinal = EstadoOportunidad.ACTIVACION_REALIZADA,
                cantidadAsistentes = 10,
                contactosGenerados = 2,
                personasInteresadas = 3,
                inscripciones = 1,
                observaciones = "",
                evaluacionGeneral = "",
                aprendizajes = ""
            )
        )

        coEvery { fuenteRemota.obtenerTodas() } returns listOf(
            oportunidadExistente.copy(nombre = "Actualizada"),
            crearOportunidad(2L, "Nueva")
        )

        repositorio.refrescar()

        val oportunidades = baseDeDatos.oportunidadDao().observarTodas().first().associateBy { it.id }
        assertEquals(2, oportunidades.size)
        assertEquals("Actualizada", oportunidades[1L]?.nombre)
        assertNotNull(oportunidades[2L])
        assertNull(oportunidades[3L])
        assertEquals(1, baseDeDatos.seguimientoDao().observarPorOportunidad(1L).first().size)
        assertEquals(1, baseDeDatos.seguimientoDao().contar())
        assertNotNull(baseDeDatos.resultadoActivacionDao().obtenerPorOportunidad(1L))
    }

    private fun crearOportunidad(id: Long, nombre: String) = Oportunidad(
        id = id,
        nombre = nombre,
        tipo = TipoEvento.FERIA_EMPRESARIAL,
        descripcion = "Descripcion",
        fuenteInformacion = "Web",
        fechaDeteccion = 1000L,
        responsableId = null,
        prioridad = Prioridad.MEDIA,
        potencialComercial = "Medio",
        estado = EstadoOportunidad.EVENTO_IDENTIFICADO,
        eventoId = null,
        fechaActualizacion = 2000L,
        observaciones = ""
    )
}
