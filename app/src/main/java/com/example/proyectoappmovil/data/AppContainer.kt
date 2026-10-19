package com.example.proyectoappmovil.data

import android.content.Context
import com.example.proyectoappmovil.data.local.AppDatabase
import com.example.proyectoappmovil.data.repository.AuthRepository
import com.example.proyectoappmovil.data.repository.ContactoRepository
import com.example.proyectoappmovil.data.repository.ContactoRepositoryImpl
import com.example.proyectoappmovil.data.repository.EventoRepository
import com.example.proyectoappmovil.data.repository.EventoRepositoryImpl
import com.example.proyectoappmovil.data.repository.OportunidadRepository
import com.example.proyectoappmovil.data.repository.OportunidadRepositoryImpl
import com.example.proyectoappmovil.data.repository.OrganizacionRepository
import com.example.proyectoappmovil.data.repository.OrganizacionRepositoryImpl
import com.example.proyectoappmovil.data.repository.ResultadoActivacionRepository
import com.example.proyectoappmovil.data.repository.ResultadoActivacionRepositoryImpl
import com.example.proyectoappmovil.data.repository.SeguimientoRepository
import com.example.proyectoappmovil.data.repository.SeguimientoRepositoryImpl
import com.example.proyectoappmovil.data.repository.SessionManager
import com.example.proyectoappmovil.data.repository.UsuarioRepository
import com.example.proyectoappmovil.data.repository.UsuarioRepositoryImpl

class AppContainer(baseDeDatos: AppDatabase, context: Context) {

    val usuarioRepository: UsuarioRepository =
        UsuarioRepositoryImpl(baseDeDatos.usuarioDao())

    val organizacionRepository: OrganizacionRepository =
        OrganizacionRepositoryImpl(baseDeDatos.organizacionDao())

    val contactoRepository: ContactoRepository =
        ContactoRepositoryImpl(baseDeDatos.contactoDao())

    val eventoRepository: EventoRepository =
        EventoRepositoryImpl(baseDeDatos.eventoDao())

    val oportunidadRepository: OportunidadRepository =
        OportunidadRepositoryImpl(baseDeDatos.oportunidadDao())

    val seguimientoRepository: SeguimientoRepository =
        SeguimientoRepositoryImpl(baseDeDatos.seguimientoDao())

    val resultadoActivacionRepository: ResultadoActivacionRepository =
        ResultadoActivacionRepositoryImpl(baseDeDatos.resultadoActivacionDao())

    val authRepository: AuthRepository =
        AuthRepository(usuarioRepository)

    val sessionManager: SessionManager =
        SessionManager.create(context)
}
