package com.example.proyectoappmovil.ui.seguimiento

import com.example.proyectoappmovil.data.local.entity.Seguimiento
import com.example.proyectoappmovil.data.repository.SeguimientoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SeguimientoRepositorioPrueba : SeguimientoRepository {

    override fun observarPorOportunidad(oportunidadId: Long): Flow<List<Seguimiento>> =
        flowOf(emptyList())

    override fun observarPendientes(hasta: Long): Flow<List<Seguimiento>> =
        flowOf(emptyList())

    override suspend fun insertar(seguimiento: Seguimiento): Long = 0L

    override suspend fun actualizar(seguimiento: Seguimiento) = Unit

    override suspend fun eliminar(seguimiento: Seguimiento) = Unit
}
