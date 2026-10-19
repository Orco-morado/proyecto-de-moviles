package com.example.proyectoappmovil.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.proyectoappmovil.data.model.TipoEvento

@Entity(
    tableName = "eventos",
    foreignKeys = [
        ForeignKey(
            entity = Organizacion::class,
            parentColumns = ["id"],
            childColumns = ["organizacionId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("organizacionId"), Index("fechaInicio")]
)
data class Evento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val tipo: TipoEvento,
    val descripcion: String,
    val fechaInicio: Long,
    val fechaTermino: Long?,
    val horario: String,
    val direccion: String,
    val comuna: String,
    val region: String,
    val organizacionId: Long?,
    val publicoEsperado: String,
    val publicoObjetivo: String,
    val cantidadAsistentesEstimada: Int?,
    val condicionesParticipacion: String,
    val requisitos: String,
    val costosReferenciales: String,
    val espaciosDisponibles: String,
    val observaciones: String
)
