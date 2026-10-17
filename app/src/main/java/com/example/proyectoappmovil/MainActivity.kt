package com.example.proyectoappmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.proyectoappmovil.navigation.AppNav
import com.example.proyectoappmovil.ui.theme.ProyectoAppMovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoAppMovilTheme {
                AppNav()
            }
        }
    }
}
