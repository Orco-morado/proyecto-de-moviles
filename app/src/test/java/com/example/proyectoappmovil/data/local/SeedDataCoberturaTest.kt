package com.example.proyectoappmovil.data.local

import com.example.proyectoappmovil.data.local.dao.ContactoDao
import com.example.proyectoappmovil.data.local.dao.EventoDao
import com.example.proyectoappmovil.data.local.dao.OportunidadDao
import com.example.proyectoappmovil.data.local.dao.OrganizacionDao
import com.example.proyectoappmovil.data.local.dao.ResultadoActivacionDao
import com.example.proyectoappmovil.data.local.dao.SeguimientoDao
import com.example.proyectoappmovil.data.local.dao.UsuarioDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SeedDataCoberturaTest {

    private lateinit var baseDeDatos: AppDatabase
    private lateinit var usuarioDao: UsuarioDao
    private lateinit var organizacionDao: OrganizacionDao
    private lateinit var eventoDao: EventoDao
    private lateinit var contactoDao: ContactoDao
    private lateinit var oportunidadDao: OportunidadDao
    private lateinit var seguimientoDao: SeguimientoDao
    private lateinit var resultadoActivacionDao: ResultadoActivacionDao

    @Before
    fun configurar() {
        baseDeDatos = mockk(relaxed = true)
        usuarioDao = mockk(relaxed = true)
        organizacionDao = mockk(relaxed = true)
        eventoDao = mockk(relaxed = true)
        contactoDao = mockk(relaxed = true)
        oportunidadDao = mockk(relaxed = true)
        seguimientoDao = mockk(relaxed = true)
        resultadoActivacionDao = mockk(relaxed = true)

        every { baseDeDatos.usuarioDao() } returns usuarioDao
        every { baseDeDatos.organizacionDao() } returns organizacionDao
        every { baseDeDatos.eventoDao() } returns eventoDao
        every { baseDeDatos.contactoDao() } returns contactoDao
        every { baseDeDatos.oportunidadDao() } returns oportunidadDao
        every { baseDeDatos.seguimientoDao() } returns seguimientoDao
        every { baseDeDatos.resultadoActivacionDao() } returns resultadoActivacionDao
    }

    @Test
    fun cargarSiEstaVacio_noInsertaCuandoYaHayUsuarios() = runTest {
        coEvery { usuarioDao.contar() } returns 1

        SeedData.cargarSiEstaVacio(baseDeDatos)

        coVerify(exactly = 0) { usuarioDao.insertar(any()) }
        coVerify(exactly = 0) { organizacionDao.insertar(any()) }
        coVerify(exactly = 0) { eventoDao.insertar(any()) }
        coVerify(exactly = 0) { contactoDao.insertar(any()) }
        coVerify(exactly = 0) { oportunidadDao.insertar(any()) }
        coVerify(exactly = 0) { seguimientoDao.insertar(any()) }
        coVerify(exactly = 0) { resultadoActivacionDao.guardar(any()) }
    }

    @Test
    fun cargarSiEstaVacio_insertaLaSemillaCompletaConClavesValidas() = runTest {
        coEvery { usuarioDao.contar() } returns 0
        coEvery { usuarioDao.insertar(any()) } returnsMany listOf(1L, 2L, 3L, 4L)
        coEvery { organizacionDao.insertar(any()) } returnsMany listOf(10L, 11L, 12L)
        coEvery { eventoDao.insertar(any()) } returnsMany listOf(100L, 101L, 102L, 103L)
        coEvery { oportunidadDao.insertar(any()) } returnsMany listOf(200L, 201L, 202L, 203L)

        SeedData.cargarSiEstaVacio(baseDeDatos)

        coVerify(exactly = 4) {
            usuarioDao.insertar(match { it.passwordHash.isNotBlank() && it.salt.isNotBlank() })
        }
        coVerify(exactly = 4) {
            usuarioDao.insertar(match { it.username in listOf("cristopher", "vicente", "lucas", "batistoni") })
        }
        coVerify(exactly = 3) { organizacionDao.insertar(any()) }
        coVerify(exactly = 4) { eventoDao.insertar(any()) }
        coVerify(exactly = 3) { contactoDao.insertar(any()) }
        coVerify(exactly = 4) { oportunidadDao.insertar(any()) }
        coVerify(exactly = 4) { seguimientoDao.insertar(any()) }
        coVerify(exactly = 1) { resultadoActivacionDao.guardar(any()) }
        coVerify { oportunidadDao.insertar(match { it.responsableId == 1L || it.responsableId == 2L }) }
    }
}
