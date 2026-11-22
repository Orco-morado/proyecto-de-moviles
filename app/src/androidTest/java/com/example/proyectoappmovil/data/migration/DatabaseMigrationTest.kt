package com.example.proyectoappmovil.data.migration

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.proyectoappmovil.data.local.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {

    @Test
    fun migracion_v3_a_v4_definidaCorrectamente() {
        val migration = AppDatabase.MIGRATION_3_4
        assertEquals(3, migration.startVersion)
        assertEquals(4, migration.endVersion)
    }

    @Test
    fun baseDeDatos_v6_abreCorrectamenteEnMemoria() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .addMigrations(AppDatabase.MIGRATION_3_4)
            .allowMainThreadQueries()
            .build()

        assertNotNull(db.oportunidadDao())
        assertNotNull(db.usuarioDao())
        assertNotNull(db.eventoDao())

        db.close()
    }
}