package com.example.proyectoappmovil.notificaciones

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.proyectoappmovil.R

object NotificacionHelper {

    const val CANAL_RECORDATORIOS = "recordatorios"
    const val CANAL_ALERTAS = "alertas"

    const val EXTRA_TITULO = "extra_titulo"
    const val EXTRA_TEXTO = "extra_texto"
    const val EXTRA_ID = "extra_id"
    const val EXTRA_CANAL = "extra_canal"

    fun crearCanales(context: Context) {
        val administrador =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val canalRecordatorios = NotificationChannel(
            CANAL_RECORDATORIOS,
            "Recordatorios de seguimiento",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Avisos de próximos seguimientos y actividades"
        }

        val canalAlertas = NotificationChannel(
            CANAL_ALERTAS,
            "Alertas del negocio",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Resumen de pendientes y propuestas en espera"
        }

        administrador.createNotificationChannel(canalRecordatorios)
        administrador.createNotificationChannel(canalAlertas)
    }

    fun tienePermiso(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

    fun mostrar(
        context: Context,
        titulo: String,
        texto: String,
        id: Int = 1,
        canal: String = CANAL_ALERTAS
    ) {
        if (!tienePermiso(context)) return

        val administrador =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificacion = NotificationCompat.Builder(context, canal)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        administrador.notify(id, notificacion)
    }

    fun programar(
        context: Context,
        titulo: String,
        texto: String,
        disparoEn: Long,
        id: Int = 2000,
        canal: String = CANAL_RECORDATORIOS
    ) {
        val intencion = Intent(context, RecordatorioReceiver::class.java).apply {
            putExtra(EXTRA_TITULO, titulo)
            putExtra(EXTRA_TEXTO, texto)
            putExtra(EXTRA_ID, id)
            putExtra(EXTRA_CANAL, canal)
        }

        val pendiente = PendingIntent.getBroadcast(
            context,
            id,
            intencion,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val momento = if (disparoEn <= System.currentTimeMillis()) {
            System.currentTimeMillis() + 5_000
        } else {
            disparoEn
        }

        val administrador = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        administrador.set(AlarmManager.RTC_WAKEUP, momento, pendiente)
    }

    fun cancelar(context: Context, id: Int) {
        val intencion = Intent(context, RecordatorioReceiver::class.java)
        val pendiente = PendingIntent.getBroadcast(
            context,
            id,
            intencion,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val administrador = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        administrador.cancel(pendiente)
    }
}
