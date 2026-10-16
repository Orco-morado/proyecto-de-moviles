package com.example.proyectoappmovil.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoappmovil.data.local.entity.Organizacion
import kotlinx.coroutines.flow.Flow

@Dao
interface OrganizacionDao {

    @Query("SELECT * FROM organizaciones ORDER BY nombre")
    fun observarTodas(): Flow<List<Organizacion>>

    @Query("SELECT * FROM organizaciones WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Organizacion?

    @Insert
    suspend fun insertar(organizacion: Organizacion): Long

    @Update
    suspend fun actualizar(organizacion: Organizacion)

    @Delete
    suspend fun eliminar(organizacion: Organizacion)
}
