package com.example.proyectoappmovil.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyectoappmovil.data.local.entity.ResultadoActivacion
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultadoActivacionDao {

    @Query("SELECT * FROM resultados_activacion WHERE oportunidadId = :oportunidadId")
    suspend fun obtenerPorOportunidad(oportunidadId: Long): ResultadoActivacion?

    @Query("SELECT * FROM resultados_activacion ORDER BY fechaRealizacion DESC")
    fun observarTodos(): Flow<List<ResultadoActivacion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(resultado: ResultadoActivacion): Long

    @Delete
    suspend fun eliminar(resultado: ResultadoActivacion)
}
