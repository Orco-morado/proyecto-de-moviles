package com.example.proyectoappmovil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.proyectoappmovil.data.model.TipoOrganizacion

@Entity(tableName = "organizaciones")
data class Organizacion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val tipo: TipoOrganizacion,
    val rubro: String,
    val comuna: String,
    val region: String,
    val sitioWeb: String,
    val publicoAsociado: String,
    val observaciones: String,
    val latitud: Double? = null,
    val longitud: Double? = null
)
