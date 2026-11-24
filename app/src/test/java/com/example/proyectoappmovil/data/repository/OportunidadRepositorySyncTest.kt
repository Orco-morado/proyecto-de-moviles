package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.local.dao.OportunidadDao
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class OportunidadRepositorySyncTest {

    private lateinit var dao: OportunidadDao
    private lateinit var remote: OportunidadRemoteDataSource

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        remote = mockk(relaxed = true)
    }

    @Test
    fun insertar_conRemoto_creaInsertaLocalYRefresca() = runTest {
        val repositorio = OportunidadRepositoryImpl(dao, remote)
        val nueva = crearOportunidad(0L, "Nueva")
        val creada = crearOportunidad(10L, "Creada remota")
        coEvery { remote.crear(nueva) } returns creada
        coEvery { remote.obtenerTodas() } returns listOf(creada)
        coEvery { dao.insertar(creada) } returns 10L

        val idLocal = repositorio.insertar(nueva)

        assertEquals(10L, idLocal)
        coVerify(exactly = 1) { remote.crear(nueva) }
        coVerify(exactly = 1) { dao.insertar(creada) }
        coVerify(exactly = 1) { remote.obtenerTodas() }
        coVerify(exactly = 1) { dao.sincronizar(listOf(creada)) }
    }

    @Test
    fun actualizar_conRemoto_actualizaRemotoLocalYRefresca() = runTest {
        val repositorio = OportunidadRepositoryImpl(dao, remote)
        val actualizada = crearOportunidad(1L, "Actualizada")
        val desdeRemoto = crearOportunidad(1L, "Desde remoto")
        coEvery { remote.actualizar(actualizada) } returns actualizada
        coEvery { remote.obtenerTodas() } returns listOf(desdeRemoto)

        repositorio.actualizar(actualizada)

        coVerify(exactly = 1) { remote.actualizar(actualizada) }
        coVerify(exactly = 1) { dao.actualizar(actualizada) }
        coVerify(exactly = 1) { remote.obtenerTodas() }
        coVerify(exactly = 1) { dao.sincronizar(listOf(desdeRemoto)) }
    }

    @Test
    fun eliminar_conRemoto_eliminaRemotoLocalYRefresca() = runTest {
        val repositorio = OportunidadRepositoryImpl(dao, remote)
        val oportunidad = crearOportunidad(1L, "A eliminar")
        coEvery { remote.obtenerTodas() } returns emptyList()

        repositorio.eliminar(oportunidad)

        coVerify(exactly = 1) { remote.eliminar(1L) }
        coVerify(exactly = 1) { dao.eliminar(oportunidad) }
        coVerify(exactly = 1) { remote.obtenerTodas() }
        coVerify(exactly = 1) { dao.sincronizar(emptyList()) }
    }

    @Test
    fun operaciones_sinRemoto_deleganSoloADao() = runTest {
        val repositorio = OportunidadRepositoryImpl(dao)
        val oportunidad = crearOportunidad(1L, "Local")
        val actualizada = oportunidad.copy(nombre = "Actualizada")
        coEvery { dao.insertar(oportunidad) } returns 1L

        repositorio.refrescar()
        val id = repositorio.insertar(oportunidad)
        repositorio.actualizar(actualizada)
        repositorio.eliminar(actualizada)

        assertEquals(1L, id)
        coVerify(exactly = 0) { remote.obtenerTodas() }
        coVerify(exactly = 0) { dao.sincronizar(any()) }
        coVerify(exactly = 1) { dao.insertar(oportunidad) }
        coVerify(exactly = 1) { dao.actualizar(actualizada) }
        coVerify(exactly = 1) { dao.eliminar(actualizada) }
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
