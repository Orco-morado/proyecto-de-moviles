package com.example.proyectoappmovil.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoappmovil.data.local.entity.Seguimiento
import kotlinx.coroutines.flow.Flow

@Dao
interface SeguimientoDao {

    @Query("SELECT * FROM seguimientos WHERE oportunidadId = :oportunidadId ORDER BY fechaHora DESC")
    fun observarPorOportunidad(oportunidadId: Long): Flow<List<Seguimiento>>

    @Query("SELECT * FROM seguimientos ORDER BY fechaHora DESC")
    fun observarTodos(): Flow<List<Seguimiento>>

    @Query(
        """
        SELECT * FROM seguimientos
        WHERE fechaProximoSeguimiento IS NOT NULL
          AND fechaProximoSeguimiento <= :hasta
        ORDER BY fechaProximoSeguimiento
        """
    )
    fun observarPendientes(hasta: Long): Flow<List<Seguimiento>>

    @Query("SELECT COUNT(*) FROM seguimientos")
    suspend fun contar(): Int

    @Insert
    suspend fun insertar(seguimiento: Seguimiento): Long

    @Update
    suspend fun actualizar(seguimiento: Seguimiento)

    @Delete
    suspend fun eliminar(seguimiento: Seguimiento)
}
