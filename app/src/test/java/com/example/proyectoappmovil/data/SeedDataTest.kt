package com.example.proyectoappmovil.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.proyectoappmovil.data.local.AppDatabase
import com.example.proyectoappmovil.data.local.entity.Contacto
import com.example.proyectoappmovil.data.local.entity.Evento
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.local.entity.Organizacion
import com.example.proyectoappmovil.data.local.entity.ResultadoActivacion
import com.example.proyectoappmovil.data.local.entity.Seguimiento
import com.example.proyectoappmovil.data.local.entity.Usuario
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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

        assertEquals(2, usuariosCount)
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
        contactos.forEach {
            assertTrue(db.organizacionDao().obtenerPorId(it.organizacionId) != null)
        }
        eventos.forEach {
            it.organizacionId?.let { orgId ->
                assertNotNull(db.organizacionDao().obtenerPorId(orgId))
            }
        }
        oportunidades.forEach {
            assertNotNull(db.eventoDao().obtenerPorId(it.eventoId!!))
            assertNotNull(db.usuarioDao().obtenerPorId(it.responsableId!!))
        }
        seguimientos.forEach {
            assertNotNull(db.oportunidadDao().obtenerPorId(it.oportunidadId))
            it.usuarioResponsableId?.let { userId ->
                assertNotNull(db.usuarioDao().obtenerPorId(userId))
            }
        }
        resultados.forEach {
            assertNotNull(db.oportunidadDao().obtenerPorId(it.oportunidadId))
        }

        // Verificar total de filas insertadas = 21
        val totalFilas = db.usuarioDao().contar() +
            db.organizacionDao().observarTodas().first().size +
            db.contactoDao().observarTodos().first().size +
            db.eventoDao().observarTodos().first().size +
            db.oportunidadDao().observarTodas().first().size +
            db.seguimientoDao().contar() +
            db.resultadoActivacionDao().observarTodos().first().size
        assertEquals(21, totalFilas)
    }
}