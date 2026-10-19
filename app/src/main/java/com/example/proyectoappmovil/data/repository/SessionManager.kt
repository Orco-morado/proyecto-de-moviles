package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.local.entity.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SessionManager {

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser

    suspend fun saveUser(usuario: Usuario) {
        _currentUser.value = usuario
    }

    suspend fun clearUser() {
        _currentUser.value = null
    }

    companion object {
        fun create(context: android.content.Context): SessionManager {
            return SessionManager()
        }
    }
}