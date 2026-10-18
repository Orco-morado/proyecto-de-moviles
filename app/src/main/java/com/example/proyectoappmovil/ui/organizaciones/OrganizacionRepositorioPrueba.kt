package com.example.proyectoappmovil.ui.organizaciones

import com.example.proyectoappmovil.data.local.entity.Organizacion
import com.example.proyectoappmovil.data.repository.OrganizacionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class OrganizacionRepositorioPrueba : OrganizacionRepository {

    override fun observarTodas(): Flow<List<Organizacion>> = flowOf(emptyList())

    override suspend fun obtenerPorId(id: Long): Organizacion? = null

    override suspend fun insertar(organizacion: Organizacion): Long = 0L

    override suspend fun actualizar(organizacion: Organizacion) = Unit

    override suspend fun eliminar(organizacion: Organizacion) = Unit
}
