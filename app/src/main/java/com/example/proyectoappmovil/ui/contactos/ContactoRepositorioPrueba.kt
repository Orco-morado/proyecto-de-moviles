package com.example.proyectoappmovil.ui.contactos

import com.example.proyectoappmovil.data.local.entity.Contacto
import com.example.proyectoappmovil.data.repository.ContactoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ContactoRepositorioPrueba : ContactoRepository {

    override fun observarTodos(): Flow<List<Contacto>> = flowOf(emptyList())

    override fun observarPorOrganizacion(organizacionId: Long): Flow<List<Contacto>> =
        flowOf(emptyList())

    override suspend fun obtenerPorId(id: Long): Contacto? = null

    override suspend fun insertar(contacto: Contacto): Long = 0L

    override suspend fun actualizar(contacto: Contacto) = Unit

    override suspend fun eliminar(contacto: Contacto) = Unit
}
