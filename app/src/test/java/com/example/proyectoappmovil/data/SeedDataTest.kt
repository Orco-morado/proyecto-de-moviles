package com.example.proyectoappmovil.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.proyectoappmovil.data.local.AppDatabase
import com.example.proyectoappmovil.data.local.SeedData
import com.example.proyectoappmovil.data.local.entity.Contacto
import com.example.proyectoappmovil.data.local.entity.Evento
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.local.entity.Organizacion
import com.example.proyectoappmovil.data.local.entity.ResultadoActivacion
import com.example.proyectoappmovil.data.local.entity.Seguimiento
import com.example.proyectoappmovil.data.local.entity.Usuario
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SeedDataTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Test
    fun seedData_cargarSiEstaVacio_insertaTodasLasEntidadesConFKsValidas() = runBlocking {
        // Ejecutar seed
        SeedData.cargarSiEstaVacio(db)

        // Verificar conteos esperados
        val usuariosCount = db.usuarioDao().contar()
        val orgsCount = db.organizacionDao().observarTodas().first().size
        val contactosCount = db.contactoDao().observarTodos().first().size
        val eventosCount = db.eventoDao().observarTodos().first().size
        val oportunidadesCount = db.oportunidadDao().observarTodas().first().size
        val seguimientosCount = db.seguimientoDao().contar()
        val resultadosCount = db.resultadoActivacionDao().observarTodos().first().size

        assertEquals(4, usuariosCount)
        assertEquals(3, orgsCount)
        assertEquals(3, contactosCount)
        assertEquals(4, eventosCount)
        assertEquals(4, oportunidadesCount)
        assertEquals(4, seguimientosCount)
        assertEquals(1, resultadosCount)

        // Verificar que FKs son válidas (cada referencia existe)
        val usuarios = db.usuarioDao().observarTodos().first()
        val organizaciones = db.organizacionDao().observarTodas().first()
        val contactos = db.contactoDao().observarTodos().first()
        val eventos = db.eventoDao().observarTodos().first()
        val oportunidades = db.oportunidadDao().observarTodas().first()
        val seguimientos = db.seguimientoDao().observarTodos().first()
        val resultados = db.resultadoActivacionDao().observarTodos().first()

        usuarios.forEach { assertNotNull(it.id) }
        organizaciones.forEach { assertNotNull(it.id) }
        for (contacto in contactos) {
            assertTrue(db.organizacionDao().obtenerPorId(contacto.organizacionId) != null)
        }
        for (evento in eventos) {
            evento.organizacionId?.let { orgId ->
                assertNotNull(db.organizacionDao().obtenerPorId(orgId))
            }
        }
        for (oportunidad in oportunidades) {
            assertNotNull(db.eventoDao().obtenerPorId(oportunidad.eventoId!!))
            assertNotNull(db.usuarioDao().obtenerPorId(oportunidad.responsableId!!))
        }
        for (seguimiento in seguimientos) {
            assertNotNull(db.oportunidadDao().obtenerPorId(seguimiento.oportunidadId))
            seguimiento.usuarioResponsableId?.let { userId ->
                assertNotNull(db.usuarioDao().obtenerPorId(userId))
            }
        }
        for (resultado in resultados) {
            assertNotNull(db.oportunidadDao().obtenerPorId(resultado.oportunidadId))
        }

        // Verificar total de filas insertadas = 23
        val totalFilas = db.usuarioDao().contar() +
            db.organizacionDao().observarTodas().first().size +
            db.contactoDao().observarTodos().first().size +
            db.eventoDao().observarTodos().first().size +
            db.oportunidadDao().observarTodas().first().size +
            db.seguimientoDao().contar() +
            db.resultadoActivacionDao().observarTodos().first().size
        assertEquals(23, totalFilas)
    }
}