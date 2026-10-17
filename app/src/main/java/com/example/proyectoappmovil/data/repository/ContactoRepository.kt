package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.local.dao.ContactoDao
import com.example.proyectoappmovil.data.local.entity.Contacto
import kotlinx.coroutines.flow.Flow

interface ContactoRepository {
    fun observarTodos(): Flow<List<Contacto>>
    fun observarPorOrganizacion(organizacionId: Long): Flow<List<Contacto>>
    suspend fun obtenerPorId(id: Long): Contacto?
    suspend fun insertar(contacto: Contacto): Long
    suspend fun actualizar(contacto: Contacto)
    suspend fun eliminar(contacto: Contacto)
}

class ContactoRepositoryImpl(
    private val dao: ContactoDao
) : ContactoRepository {

    override fun observarTodos(): Flow<List<Contacto>> = dao.observarTodos()

    override fun observarPorOrganizacion(organizacionId: Long): Flow<List<Contacto>> =
        dao.observarPorOrganizacion(organizacionId)

    override suspend fun obtenerPorId(id: Long): Contacto? = dao.obtenerPorId(id)

    override suspend fun insertar(contacto: Contacto): Long = dao.insertar(contacto)

    override suspend fun actualizar(contacto: Contacto) = dao.actualizar(contacto)

    override suspend fun eliminar(contacto: Contacto) = dao.eliminar(contacto)
}
