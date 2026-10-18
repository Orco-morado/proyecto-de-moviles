package com.example.proyectoappmovil.ubicacion

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

object UbicacionHelper {

    private const val TIMEOUT_UBICACION_MS = 10_000L

    fun tienePermiso(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

    suspend fun obtenerUbicacion(context: Context): Pair<Double, Double>? {
        if (!tienePermiso(context)) return null
        val gestor = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            for (proveedor in listOf(
                LocationManager.NETWORK_PROVIDER,
                LocationManager.GPS_PROVIDER
            )) {
                if (!gestor.isProviderEnabled(proveedor)) continue
                val ubicacion = withTimeoutOrNull(TIMEOUT_UBICACION_MS) {
                    ubicacionActual(context, gestor, proveedor)
                }
                if (ubicacion != null) {
                    return ubicacion.latitude to ubicacion.longitude
                }
            }
        }

        for (proveedor in listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )) {
            val ubicacion = ubicacionConocida(gestor, proveedor)
            if (ubicacion != null) {
                return ubicacion.latitude to ubicacion.longitude
            }
        }

        return null
    }

    private fun ubicacionConocida(gestor: LocationManager, proveedor: String): Location? = try {
        gestor.getLastKnownLocation(proveedor)
    } catch (_: SecurityException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun ubicacionActual(
        context: Context,
        gestor: LocationManager,
        proveedor: String
    ): Location? = suspendCancellableCoroutine { continuacion ->
        try {
            gestor.getCurrentLocation(proveedor, null, context.mainExecutor) { ubicacion ->
                if (continuacion.isActive) {
                    continuacion.resume(ubicacion)
                }
            }
        } catch (_: SecurityException) {
            if (continuacion.isActive) {
                continuacion.resume(null)
            }
        } catch (_: IllegalArgumentException) {
            if (continuacion.isActive) {
                continuacion.resume(null)
            }
        }
    }
}
