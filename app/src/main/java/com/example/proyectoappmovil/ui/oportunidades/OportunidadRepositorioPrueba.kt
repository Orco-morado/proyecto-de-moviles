package com.example.proyectoappmovil.ui.oportunidades

import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import com.example.proyectoappmovil.data.repository.OportunidadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class OportunidadRepositorioPrueba : OportunidadRepository {

    override fun observarTodas(): Flow<List<Oportunidad>> = flowOf(emptyList())

    override fun observarFiltradas(
        estado: EstadoOportunidad?,
        prioridad: Prioridad?,
        tipo: TipoEvento?,
        texto: String
    ): Flow<List<Oportunidad>> = flowOf(emptyList())

    override fun observarTotal(): Flow<Int> = flowOf(0)

    override fun observarEnEsperaRespuesta(): Flow<List<Oportunidad>> = flowOf(emptyList())

    override suspend fun obtenerPorId(id: Long): Oportunidad? = null

    override suspend fun insertar(oportunidad: Oportunidad): Long = 0L

    override suspend fun actualizar(oportunidad: Oportunidad) = Unit

    override suspend fun eliminar(oportunidad: Oportunidad) = Unit
}
