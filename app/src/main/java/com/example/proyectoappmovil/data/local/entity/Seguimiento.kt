package com.example.proyectoappmovil.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.proyectoappmovil.data.model.TipoAccionSeguimiento

@Entity(
    tableName = "seguimientos",
    foreignKeys = [
        ForeignKey(
            entity = Oportunidad::class,
            parentColumns = ["id"],
            childColumns = ["oportunidadId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioResponsableId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("oportunidadId"), Index("usuarioResponsableId")]
)
data class Seguimiento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val oportunidadId: Long,
    val usuarioResponsableId: Long?,
    val tipoAccion: TipoAccionSeguimiento,
    val fechaHora: Long,
    val resultado: String,
    val proximaAccion: String,
    val fechaProximoSeguimiento: Long?,
    val observaciones: String,
    val fotoEvidencia: String? = null
)
