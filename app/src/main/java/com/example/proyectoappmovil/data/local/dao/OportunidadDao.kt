package com.example.proyectoappmovil.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
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

    @Query(
        """
        SELECT * FROM oportunidades
        WHERE estado IN ('PROPUESTA_ENVIADA', 'EN_ESPERA_RESPUESTA')
        ORDER BY fechaActualizacion DESC
        """
    )
    fun observarEnEsperaRespuesta(): Flow<List<Oportunidad>>

    @Query("SELECT * FROM oportunidades WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Oportunidad?

    @Insert
    suspend fun insertar(oportunidad: Oportunidad): Long

    @Upsert
    suspend fun upsertTodos(oportunidades: List<Oportunidad>)

    @Query("DELETE FROM oportunidades WHERE id NOT IN (:ids)")
    suspend fun eliminarNoPresentes(ids: List<Long>)

    @Transaction
    suspend fun sincronizar(oportunidades: List<Oportunidad>) {
        if (oportunidades.isEmpty()) {
            eliminarTodos()
            return
        }
        upsertTodos(oportunidades)
        eliminarNoPresentes(oportunidades.map { it.id })
    }

    @Query("DELETE FROM oportunidades WHERE id = :id")
    suspend fun eliminarPorId(id: Long)

    @Query("DELETE FROM oportunidades")
    suspend fun eliminarTodos()

    @Update
    suspend fun actualizar(oportunidad: Oportunidad)

    @Delete
    suspend fun eliminar(oportunidad: Oportunidad)
}