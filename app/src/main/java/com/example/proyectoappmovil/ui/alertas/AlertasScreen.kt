package com.example.proyectoappmovil.ui.alertas

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectoappmovil.data.alertas.ResumenAlertas
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.local.entity.Seguimiento
import com.example.proyectoappmovil.notificaciones.NotificacionHelper
import com.example.proyectoappmovil.ui.oportunidades.OportunidadRepositorioPrueba
import com.example.proyectoappmovil.ui.seguimiento.SeguimientoRepositorioPrueba
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(
    username: String,
    navController: NavController,
    vm: AlertasViewModel = viewModel(factory = AlertasViewModel.Factory)
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    val pendientes by vm.seguimientosPendientes.collectAsStateWithLifecycle()
    val enEspera by vm.oportunidadesEnEspera.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val ahora = System.currentTimeMillis()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Alertas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Button(
                    onClick = {
                        if (NotificacionHelper.tienePermiso(contexto)) {
                            notificarResumen(
                                contexto = contexto,
                                pendientes = pendientes,
                                enEspera = enEspera,
                                ahora = ahora
                            )
                            alcance.launch {
                                snackbarHostState.showSnackbar("Resumen enviado como notificación")
                            }
                        } else {
                            alcance.launch {
                                snackbarHostState.showSnackbar(
                                    "Permiso de notificaciones no concedido"
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Filled.Notifications, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviar resumen")
                }
            }

            item {
                Text(
                    text = "Seguimientos pendientes (${pendientes.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (pendientes.isEmpty()) {
                item {
                    Text(
                        text = "No hay seguimientos dentro de los próximos 7 días",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(pendientes, key = { "seguimiento-${it.id}" }) { seguimiento ->
                val vencido = esVencido(seguimiento, ahora)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(
                                "seguimientos/${seguimiento.oportunidadId}/$username"
                            )
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = seguimiento.resultado,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        seguimiento.fechaProximoSeguimiento?.let { fecha ->
                            Text(
                                text = "Próximo: ${formatoFecha.format(fecha)}" +
                                    if (vencido) "  (vencido)" else "",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (vencido) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Propuestas en espera (${enEspera.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (enEspera.isEmpty()) {
                item {
                    Text(
                        text = "No hay propuestas en espera de respuesta",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(enEspera, key = { "oportunidad-${it.id}" }) { oportunidad ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("oportunidad/form/${oportunidad.id}")
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = oportunidad.nombre,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${oportunidad.estado.etiqueta}  ·  actualizada el " +
                                formatoFecha.format(oportunidad.fechaActualizacion),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun notificarResumen(
    contexto: Context,
    pendientes: List<Seguimiento>,
    enEspera: List<Oportunidad>,
    ahora: Long
) {
    val vencidos = pendientes.count { esVencido(it, ahora) }
    NotificacionHelper.mostrar(
        context = contexto,
        titulo = ResumenAlertas.TITULO,
        texto = ResumenAlertas.construirTexto(pendientes.size, vencidos, enEspera.size),
        id = 3000,
        canal = NotificacionHelper.CANAL_ALERTAS
    )
}

private fun esVencido(seguimiento: Seguimiento, ahora: Long): Boolean =
    (seguimiento.fechaProximoSeguimiento ?: Long.MAX_VALUE) < ahora

@Preview(showBackground = true)
@Composable
fun AlertasScreenPreview() {
    AlertasScreen(
        username = "admin",
        navController = rememberNavController(),
        vm = AlertasViewModel(
            SeguimientoRepositorioPrueba(),
            OportunidadRepositorioPrueba()
        )
    )
}
