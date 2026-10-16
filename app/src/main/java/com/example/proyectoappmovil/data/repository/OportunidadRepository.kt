package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.local.dao.OportunidadDao
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import kotlinx.coroutines.flow.Flow

interface OportunidadRepository {
    fun observarTodas(): Flow<List<Oportunidad>>
    fun observarFiltradas(
        estado: EstadoOportunidad?,
        prioridad: Prioridad?,
        tipo: TipoEvento?,
        texto: String
    ): Flow<List<Oportunidad>>
    fun observarTotal(): Flow<Int>
    suspend fun obtenerPorId(id: Long): Oportunidad?
    suspend fun insertar(oportunidad: Oportunidad): Long
    suspend fun actualizar(oportunidad: Oportunidad)
    suspend fun eliminar(oportunidad: Oportunidad)
}

class OportunidadRepositoryImpl(
    private val dao: OportunidadDao
) : OportunidadRepository {

    override fun observarTodas(): Flow<List<Oportunidad>> = dao.observarTodas()

    override fun observarFiltradas(
        estado: EstadoOportunidad?,
        prioridad: Prioridad?,
        tipo: TipoEvento?,
        texto: String
    ): Flow<List<Oportunidad>> = dao.observarFiltradas(estado, prioridad, tipo, texto)

    override fun observarTotal(): Flow<Int> = dao.observarTotal()

    override suspend fun obtenerPorId(id: Long): Oportunidad? = dao.obtenerPorId(id)

    override suspend fun insertar(oportunidad: Oportunidad): Long = dao.insertar(oportunidad)

    override suspend fun actualizar(oportunidad: Oportunidad) = dao.actualizar(oportunidad)

    override suspend fun eliminar(oportunidad: Oportunidad) = dao.eliminar(oportunidad)
}
