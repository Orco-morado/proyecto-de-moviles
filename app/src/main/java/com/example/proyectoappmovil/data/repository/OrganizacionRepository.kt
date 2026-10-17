package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.local.dao.OrganizacionDao
import com.example.proyectoappmovil.data.local.entity.Organizacion
import kotlinx.coroutines.flow.Flow

interface OrganizacionRepository {
    fun observarTodas(): Flow<List<Organizacion>>
    suspend fun obtenerPorId(id: Long): Organizacion?
    suspend fun insertar(organizacion: Organizacion): Long
    suspend fun actualizar(organizacion: Organizacion)
    suspend fun eliminar(organizacion: Organizacion)
}

class OrganizacionRepositoryImpl(
    private val dao: OrganizacionDao
) : OrganizacionRepository {

    override fun observarTodas(): Flow<List<Organizacion>> = dao.observarTodas()

    override suspend fun obtenerPorId(id: Long): Organizacion? = dao.obtenerPorId(id)

    override suspend fun insertar(organizacion: Organizacion): Long = dao.insertar(organizacion)

    override suspend fun actualizar(organizacion: Organizacion) = dao.actualizar(organizacion)

    override suspend fun eliminar(organizacion: Organizacion) = dao.eliminar(organizacion)
}
