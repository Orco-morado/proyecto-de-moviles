package com.example.proyectoappmovil.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento

@Entity(
    tableName = "oportunidades",
    foreignKeys = [
        ForeignKey(
            entity = Evento::class,
            parentColumns = ["id"],
            childColumns = ["eventoId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["responsableId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("eventoId"), Index("responsableId"), Index("estado", "prioridad", "fechaActualizacion")]
)
data class Oportunidad(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String = "",
    val tipo: TipoEvento = TipoEvento.OTRA,
    val descripcion: String = "",
    val fuenteInformacion: String = "",
    val fechaDeteccion: Long = 0L,
    val responsableId: Long? = 0L,
    val prioridad: Prioridad = Prioridad.MEDIA,
    val potencialComercial: String = "",
    val estado: EstadoOportunidad = EstadoOportunidad.EN_EVALUACION,
    val eventoId: Long? = 0L,
    val fechaActualizacion: Long = 0L,
    val observaciones: String = ""
)
