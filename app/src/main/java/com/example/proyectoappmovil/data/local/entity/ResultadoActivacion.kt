package com.example.proyectoappmovil.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.proyectoappmovil.data.model.EstadoOportunidad

@Entity(
    tableName = "resultados_activacion",
    foreignKeys = [
        ForeignKey(
            entity = Oportunidad::class,
            parentColumns = ["id"],
            childColumns = ["oportunidadId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("oportunidadId", unique = true)]
)
data class ResultadoActivacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val oportunidadId: Long,
    val fechaRealizacion: Long,
    val estadoFinal: EstadoOportunidad,
    val cantidadAsistentes: Int?,
    val contactosGenerados: Int?,
    val personasInteresadas: Int?,
    val inscripciones: Int?,
    val observaciones: String,
    val evaluacionGeneral: String,
    val aprendizajes: String
)
