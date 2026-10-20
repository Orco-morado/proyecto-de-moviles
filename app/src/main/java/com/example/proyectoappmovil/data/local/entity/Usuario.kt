package com.example.proyectoappmovil.data.local.entity

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.proyectoappmovil.data.model.EstadoUsuario
import com.example.proyectoappmovil.data.model.PerfilUsuario
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Entity(tableName = "usuarios")
@Serializable
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String = "",
    val nombre: String,
    val perfil: PerfilUsuario,
    val area: String,
    val zona: String,
    val estadoCuenta: EstadoUsuario,
    val passwordHash: String = "",
    val salt: String = "",
    val fechaCreacion: Long,
    val fechaActualizacion: Long
)
