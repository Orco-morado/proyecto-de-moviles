package com.example.proyectoappmovil.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import kotlinx.coroutines.flow.Flow

@Dao
interface OportunidadDao {

    @Query("SELECT * FROM oportunidades ORDER BY fechaActualizacion DESC")
    fun observarTodas(): Flow<List<Oportunidad>>

    @Query(
        """
        SELECT * FROM oportunidades
        WHERE (:estado IS NULL OR estado = :estado)
          AND (:prioridad IS NULL OR prioridad = :prioridad)
          AND (:tipo IS NULL OR tipo = :tipo)
          AND (:texto = '' OR nombre LIKE '%' || :texto || '%')
        ORDER BY fechaActualizacion DESC
        """
    )
    fun observarFiltradas(
        estado: EstadoOportunidad?,
        prioridad: Prioridad?,
        tipo: TipoEvento?,
        texto: String
    ): Flow<List<Oportunidad>>

    @Query("SELECT COUNT(*) FROM oportunidades")
    fun observarTotal(): Flow<Int>

    @Query("SELECT * FROM oportunidades WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Oportunidad?

    @Insert
    suspend fun insertar(oportunidad: Oportunidad): Long

    @Update
    suspend fun actualizar(oportunidad: Oportunidad)

    @Delete
    suspend fun eliminar(oportunidad: Oportunidad)
}
