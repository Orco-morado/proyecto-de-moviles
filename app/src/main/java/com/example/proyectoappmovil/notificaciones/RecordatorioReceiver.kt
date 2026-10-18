package com.example.proyectoappmovil.notificaciones

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RecordatorioReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val titulo = intent.getStringExtra(NotificacionHelper.EXTRA_TITULO) ?: "Recordatorio de seguimiento"
        val texto = intent.getStringExtra(NotificacionHelper.EXTRA_TEXTO).orEmpty()
        val id = intent.getIntExtra(NotificacionHelper.EXTRA_ID, 2000)
        val canal = intent.getStringExtra(NotificacionHelper.EXTRA_CANAL)
            ?: NotificacionHelper.CANAL_RECORDATORIOS

        NotificacionHelper.mostrar(context, titulo, texto, id, canal)
    }
}
