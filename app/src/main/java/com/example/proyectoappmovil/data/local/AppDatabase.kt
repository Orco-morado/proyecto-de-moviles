package com.example.proyectoappmovil.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.proyectoappmovil.data.local.dao.ContactoDao
import com.example.proyectoappmovil.data.local.dao.EventoDao
import com.example.proyectoappmovil.data.local.dao.OportunidadDao
import com.example.proyectoappmovil.data.local.dao.OrganizacionDao
import com.example.proyectoappmovil.data.local.dao.ResultadoActivacionDao
import com.example.proyectoappmovil.data.local.dao.SeguimientoDao
import com.example.proyectoappmovil.data.local.dao.UsuarioDao
import com.example.proyectoappmovil.data.local.entity.Contacto
import com.example.proyectoappmovil.data.local.entity.Evento
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.local.entity.Organizacion
import com.example.proyectoappmovil.data.local.entity.ResultadoActivacion
import com.example.proyectoappmovil.data.local.entity.Seguimiento
import com.example.proyectoappmovil.data.local.entity.Usuario

@Database(
    entities = [
        Usuario::class,
        Organizacion::class,
        Contacto::class,
        Evento::class,
        Oportunidad::class,
        Seguimiento::class,
        ResultadoActivacion::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun organizacionDao(): OrganizacionDao
    abstract fun contactoDao(): ContactoDao
    abstract fun eventoDao(): EventoDao
    abstract fun oportunidadDao(): OportunidadDao
    abstract fun seguimientoDao(): SeguimientoDao
    abstract fun resultadoActivacionDao(): ResultadoActivacionDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agencia.db"
                ).build().also { instancia = it }
            }
    }
}
