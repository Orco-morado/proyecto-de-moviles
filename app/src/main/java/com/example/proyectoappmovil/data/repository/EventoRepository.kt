package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.local.dao.EventoDao
import com.example.proyectoappmovil.data.local.entity.Evento
import kotlinx.coroutines.flow.Flow

interface EventoRepository {
    fun observarTodos(): Flow<List<Evento>>
    fun observarProximos(desde: Long): Flow<List<Evento>>
    suspend fun obtenerPorId(id: Long): Evento?
    suspend fun insertar(evento: Evento): Long
    suspend fun actualizar(evento: Evento)
    suspend fun eliminar(evento: Evento)
}

class EventoRepositoryImpl(
    private val dao: EventoDao
) : EventoRepository {

    override fun observarTodos(): Flow<List<Evento>> = dao.observarTodos()

    override fun observarProximos(desde: Long): Flow<List<Evento>> = dao.observarProximos(desde)

    override suspend fun obtenerPorId(id: Long): Evento? = dao.obtenerPorId(id)

    override suspend fun insertar(evento: Evento): Long = dao.insertar(evento)

    override suspend fun actualizar(evento: Evento) = dao.actualizar(evento)

    override suspend fun eliminar(evento: Evento) = dao.eliminar(evento)
}
