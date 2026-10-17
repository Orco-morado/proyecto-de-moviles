package com.example.proyectoappmovil.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoappmovil.data.local.entity.Contacto
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactoDao {

    @Query("SELECT * FROM contactos ORDER BY nombre")
    fun observarTodos(): Flow<List<Contacto>>

    @Query("SELECT * FROM contactos WHERE organizacionId = :organizacionId ORDER BY nombre")
    fun observarPorOrganizacion(organizacionId: Long): Flow<List<Contacto>>

    @Query("SELECT * FROM contactos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Contacto?

    @Insert
    suspend fun insertar(contacto: Contacto): Long

    @Update
    suspend fun actualizar(contacto: Contacto)

    @Delete
    suspend fun eliminar(contacto: Contacto)
}
