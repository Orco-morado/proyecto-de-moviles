package com.example.proyectoappmovil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.proyectoappmovil.data.model.EstadoUsuario
import com.example.proyectoappmovil.data.model.PerfilUsuario

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val perfil: PerfilUsuario,
    val area: String,
    val zona: String,
    val estadoCuenta: EstadoUsuario,
    val fechaCreacion: Long,
    val fechaActualizacion: Long
)
