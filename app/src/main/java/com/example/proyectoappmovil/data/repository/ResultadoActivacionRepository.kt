package com.example.proyectoappmovil.data.repository

import com.example.proyectoappmovil.data.local.dao.ResultadoActivacionDao
import com.example.proyectoappmovil.data.local.entity.ResultadoActivacion

interface ResultadoActivacionRepository {
    suspend fun obtenerPorOportunidad(oportunidadId: Long): ResultadoActivacion?
    suspend fun guardar(resultado: ResultadoActivacion): Long
    suspend fun eliminar(resultado: ResultadoActivacion)
}

class ResultadoActivacionRepositoryImpl(
    private val dao: ResultadoActivacionDao
) : ResultadoActivacionRepository {

    override suspend fun obtenerPorOportunidad(oportunidadId: Long): ResultadoActivacion? =
        dao.obtenerPorOportunidad(oportunidadId)

    override suspend fun guardar(resultado: ResultadoActivacion): Long = dao.guardar(resultado)

    override suspend fun eliminar(resultado: ResultadoActivacion) = dao.eliminar(resultado)
}
