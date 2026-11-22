package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.local.dao.OportunidadDao
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import kotlinx.coroutines.flow.Flow

interface OportunidadRemoteDataSource {
    suspend fun obtenerTodas(): List<Oportunidad>
    suspend fun crear(oportunidad: Oportunidad): Oportunidad
    suspend fun actualizar(oportunidad: Oportunidad): Oportunidad
    suspend fun eliminar(id: Long)
}

interface OportunidadRepository {
    fun observarTodas(): Flow<List<Oportunidad>>
    fun observarFiltradas(
        estado: EstadoOportunidad?,
        prioridad: Prioridad?,
        tipo: TipoEvento?,
        texto: String
    ): Flow<List<Oportunidad>>
    fun observarTotal(): Flow<Int>
    fun observarEnEsperaRespuesta(): Flow<List<Oportunidad>>
    suspend fun obtenerPorId(id: Long): Oportunidad?
    suspend fun insertar(oportunidad: Oportunidad): Long
    suspend fun actualizar(oportunidad: Oportunidad)
    suspend fun eliminar(oportunidad: Oportunidad)
    suspend fun refrescar()
}

class OportunidadRepositoryImpl(
    private val dao: OportunidadDao,
    private val remoteDataSource: OportunidadRemoteDataSource? = null
) : OportunidadRepository {

    override fun observarTodas(): Flow<List<Oportunidad>> = dao.observarTodas()

    override fun observarFiltradas(
        estado: EstadoOportunidad?,
        prioridad: Prioridad?,
        tipo: TipoEvento?,
        texto: String
    ): Flow<List<Oportunidad>> = dao.observarFiltradas(estado, prioridad, tipo, texto)

    override fun observarTotal(): Flow<Int> = dao.observarTotal()

    override fun observarEnEsperaRespuesta(): Flow<List<Oportunidad>> =
        dao.observarEnEsperaRespuesta()

    override suspend fun obtenerPorId(id: Long): Oportunidad? = dao.obtenerPorId(id)

    override suspend fun insertar(oportunidad: Oportunidad): Long {
        val remote = remoteDataSource
        if (remote != null) {
            val creada = remote.crear(oportunidad)
            val idLocal = dao.insertar(creada)
            refrescar()
            return idLocal
        }
        return dao.insertar(oportunidad)
    }

    override suspend fun actualizar(oportunidad: Oportunidad) {
        val remote = remoteDataSource
        if (remote != null) {
            remote.actualizar(oportunidad)
            dao.actualizar(oportunidad)
            refrescar()
            return
        }
        dao.actualizar(oportunidad)
    }

    override suspend fun eliminar(oportunidad: Oportunidad) {
        val remote = remoteDataSource
        if (remote != null) {
            remote.eliminar(oportunidad.id)
            dao.eliminar(oportunidad)
            refrescar()
            return
        }
        dao.eliminar(oportunidad)
    }

    override suspend fun refrescar() {
        val remote = remoteDataSource ?: return
        val remotas = remote.obtenerTodas()
        dao.eliminarTodos()
        dao.insertarTodos(remotas)
    }
}