package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.local.dao.SeguimientoDao
import com.example.proyectoappmovil.data.local.entity.Seguimiento
import kotlinx.coroutines.flow.Flow

interface SeguimientoRepository {
    fun observarPorOportunidad(oportunidadId: Long): Flow<List<Seguimiento>>
    fun observarPendientes(hasta: Long): Flow<List<Seguimiento>>
    suspend fun insertar(seguimiento: Seguimiento): Long
    suspend fun actualizar(seguimiento: Seguimiento)
    suspend fun eliminar(seguimiento: Seguimiento)
}

class SeguimientoRepositoryImpl(
    private val dao: SeguimientoDao
) : SeguimientoRepository {

    override fun observarPorOportunidad(oportunidadId: Long): Flow<List<Seguimiento>> =
        dao.observarPorOportunidad(oportunidadId)

    override fun observarPendientes(hasta: Long): Flow<List<Seguimiento>> =
        dao.observarPendientes(hasta)

    override suspend fun insertar(seguimiento: Seguimiento): Long = dao.insertar(seguimiento)

    override suspend fun actualizar(seguimiento: Seguimiento) = dao.actualizar(seguimiento)

    override suspend fun eliminar(seguimiento: Seguimiento) = dao.eliminar(seguimiento)
}
