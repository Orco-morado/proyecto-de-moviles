package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.local.dao.UsuarioDao
import com.example.proyectoappmovil.data.local.entity.Usuario
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {
    fun observarTodos(): Flow<List<Usuario>>
    suspend fun obtenerPorId(id: Long): Usuario?
    suspend fun insertar(usuario: Usuario): Long
    suspend fun actualizar(usuario: Usuario)
    suspend fun eliminar(usuario: Usuario)
}

class UsuarioRepositoryImpl(
    private val dao: UsuarioDao
) : UsuarioRepository {

    override fun observarTodos(): Flow<List<Usuario>> = dao.observarTodos()

    override suspend fun obtenerPorId(id: Long): Usuario? = dao.obtenerPorId(id)

    override suspend fun insertar(usuario: Usuario): Long = dao.insertar(usuario)

    override suspend fun actualizar(usuario: Usuario) = dao.actualizar(usuario)

    override suspend fun eliminar(usuario: Usuario) = dao.eliminar(usuario)
}
