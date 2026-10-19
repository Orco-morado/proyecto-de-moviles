package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.local.entity.Usuario
import com.example.proyectoappmovil.data.model.Permission
import com.example.proyectoappmovil.data.model.RolePermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PermissionManager(
    private val sessionManager: SessionManager
) {

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser.asStateFlow()

    init {
        scope.launch {
            sessionManager.currentUser.collect { user ->
                _currentUser.value = user
            }
        }
    }

    fun hasPermission(permission: Permission): Boolean {
        val user = _currentUser.value
        return user?.let { RolePermissions.hasPermission(it.perfil, permission) } ?: false
    }

    fun hasAnyPermission(permissions: Set<Permission>): Boolean {
        return permissions.any { hasPermission(it) }
    }

    fun getCurrentUser(): Usuario? = _currentUser.value

    fun isLoggedIn(): Boolean = _currentUser.value != null

    fun getUserPerfil(): com.example.proyectoappmovil.data.model.PerfilUsuario? =
        _currentUser.value?.perfil
}