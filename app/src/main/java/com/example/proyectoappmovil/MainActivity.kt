package com.example.proyectoappmovil

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.proyectoappmovil.navigation.AppNav
import com.example.proyectoappmovil.notificaciones.NotificacionHelper
import com.example.proyectoappmovil.ui.theme.ProyectoAppMovilTheme

class MainActivity : ComponentActivity() {

    private val solicitudPermisoNotificaciones =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificacionHelper.crearCanales(this)
        solicitarPermisoNotificaciones()
        enableEdgeToEdge()
        setContent {
            ProyectoAppMovilTheme {
                AppNav()
            }
        }
    }

    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !NotificacionHelper.tienePermiso(this)
        ) {
            solicitudPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
