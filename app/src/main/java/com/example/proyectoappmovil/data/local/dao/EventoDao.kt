package com.example.proyectoappmovil.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoappmovil.data.local.entity.Evento
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {

    @Query("SELECT * FROM eventos ORDER BY fechaInicio")
    fun observarTodos(): Flow<List<Evento>>

    @Query("SELECT * FROM eventos WHERE fechaInicio >= :desde ORDER BY fechaInicio")
    fun observarProximos(desde: Long): Flow<List<Evento>>

    @Query("SELECT * FROM eventos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Evento?

    @Insert
    suspend fun insertar(evento: Evento): Long

    @Update
    suspend fun actualizar(evento: Evento)

    @Delete
    suspend fun eliminar(evento: Evento)
}
