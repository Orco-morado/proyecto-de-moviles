package com.example.proyectoappmovil.data.migration

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.proyectoappmovil.data.local.AppDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DatabaseMigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        ApplicationProvider.getApplicationContext(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @get:Rule
    val tempFolder: TemporaryFolder = TemporaryFolder()

    @Test
    fun migrar_v3_a_v4() {
        // Crear BD en versión 3
        val db = helper.createDatabase(tempFolder.newFolder().absolutePath, AppDatabase::class.java, 3)
        db.close()

        // Migrar a versión 4
        val dbV4 = helper.runMigrationsAndValidate(
            tempFolder.newFolder().absolutePath,
            AppDatabase::class.java,
            4,
            true
        )
        dbV4.close()
    }
}