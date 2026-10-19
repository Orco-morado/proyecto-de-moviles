package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.local.entity.Usuario
import com.example.proyectoappmovil.data.model.EstadoUsuario
import org.mindrot.jbcrypt.BCrypt

sealed interface AuthResult {
    data class Success(val usuario: Usuario) : AuthResult
    data class Failure(val reason: String) : AuthResult
}

class AuthRepository(
    private val usuarioRepository: UsuarioRepository
) {

    suspend fun login(username: String, password: String): AuthResult {
        val usuario = usuarioRepository.obtenerPorUsername(username.trim())
        return if (usuario == null) {
            AuthResult.Failure("Usuario no encontrado")
        } else if (usuario.estadoCuenta != EstadoUsuario.ACTIVO) {
            AuthResult.Failure("Cuenta inactiva")
        } else if (!verifyPassword(password, usuario.passwordHash)) {
            AuthResult.Failure("Contraseña incorrecta")
        } else {
            AuthResult.Success(usuario)
        }
    }

    private fun verifyPassword(password: String, hash: String): Boolean {
        return try {
            BCrypt.checkpw(password, hash)
        } catch (e: Exception) {
            false
        }
    }
}