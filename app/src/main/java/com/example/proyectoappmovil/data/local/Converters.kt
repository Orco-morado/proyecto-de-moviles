package com.example.proyectoappmovil.data.local

import androidx.room.TypeConverter
import com.example.proyectoappmovil.data.model.AreaContacto
import com.example.proyectoappmovil.data.model.EstadoContacto
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import com.example.proyectoappmovil.data.model.EstadoUsuario
import com.example.proyectoappmovil.data.model.MedioContacto
import com.example.proyectoappmovil.data.model.PerfilUsuario
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoAccionSeguimiento
import com.example.proyectoappmovil.data.model.TipoEvento
import com.example.proyectoappmovil.data.model.TipoOrganizacion

class Converters {

    @TypeConverter
    fun dePerfilUsuario(valor: PerfilUsuario): String = valor.name

    @TypeConverter
    fun aPerfilUsuario(valor: String): PerfilUsuario = PerfilUsuario.valueOf(valor)

    @TypeConverter
    fun deEstadoUsuario(valor: EstadoUsuario): String = valor.name

    @TypeConverter
    fun aEstadoUsuario(valor: String): EstadoUsuario = EstadoUsuario.valueOf(valor)

    @TypeConverter
    fun deTipoOrganizacion(valor: TipoOrganizacion): String = valor.name

    @TypeConverter
    fun aTipoOrganizacion(valor: String): TipoOrganizacion = TipoOrganizacion.valueOf(valor)

    @TypeConverter
    fun deTipoEvento(valor: TipoEvento): String = valor.name

    @TypeConverter
    fun aTipoEvento(valor: String): TipoEvento = TipoEvento.valueOf(valor)

    @TypeConverter
    fun deEstadoOportunidad(valor: EstadoOportunidad): String = valor.name

    @TypeConverter
    fun aEstadoOportunidad(valor: String): EstadoOportunidad = EstadoOportunidad.valueOf(valor)

    @TypeConverter
    fun dePrioridad(valor: Prioridad): String = valor.name

    @TypeConverter
    fun aPrioridad(valor: String): Prioridad = Prioridad.valueOf(valor)

    @TypeConverter
    fun deAreaContacto(valor: AreaContacto): String = valor.name

    @TypeConverter
    fun aAreaContacto(valor: String): AreaContacto = AreaContacto.valueOf(valor)

    @TypeConverter
    fun deMedioContacto(valor: MedioContacto): String = valor.name

    @TypeConverter
    fun aMedioContacto(valor: String): MedioContacto = MedioContacto.valueOf(valor)

    @TypeConverter
    fun deEstadoContacto(valor: EstadoContacto): String = valor.name

    @TypeConverter
    fun aEstadoContacto(valor: String): EstadoContacto = EstadoContacto.valueOf(valor)

    @TypeConverter
    fun deTipoAccionSeguimiento(valor: TipoAccionSeguimiento): String = valor.name

    @TypeConverter
    fun aTipoAccionSeguimiento(valor: String): TipoAccionSeguimiento = TipoAccionSeguimiento.valueOf(valor)
}
