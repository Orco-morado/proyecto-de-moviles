package com.example.proyectoappmovil

import android.app.Application
import com.example.proyectoappmovil.data.AppContainer
import com.example.proyectoappmovil.data.local.AppDatabase
import com.example.proyectoappmovil.data.local.SeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    lateinit var container: AppContainer
        private set

    private val alcance = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this
        val baseDeDatos = AppDatabase.obtener(this)
        container = AppContainer(baseDeDatos, this)
        alcance.launch {
            SeedData.cargarSiEstaVacio(baseDeDatos)
        }
    }
}
