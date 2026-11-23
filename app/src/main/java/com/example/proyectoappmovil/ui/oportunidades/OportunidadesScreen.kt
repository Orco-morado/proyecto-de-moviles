package com.example.proyectoappmovil.ui.oportunidades

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectoappmovil.data.local.entity.Oportunidad
import com.example.proyectoappmovil.data.model.EstadoOportunidad
import kotlinx.coroutines.launch
import com.example.proyectoappmovil.utils.toFechaCorta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadesScreen(
    username: String,
    navController: NavController,
    vm: OportunidadesViewModel = viewModel(factory = OportunidadesViewModel.Factory)
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var textoBusqueda by remember { mutableStateOf("") }
    var estadoFiltro by remember { mutableStateOf<EstadoOportunidad?>(null) }
    var mostrarBusqueda by remember { mutableStateOf(false) }
    var mostrarFiltro by remember { mutableStateOf(false) }
    var oportunidadAEliminar by remember { mutableStateOf<Oportunidad?>(null) }

    val oportunidades by vm.observarFiltradas(estadoFiltro, null, null, textoBusqueda)
        .collectAsStateWithLifecycle(emptyList())
    val usuarioActual by vm.usuarioActual.collectAsStateWithLifecycle()
    val cargando by vm.cargando.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.refrescar()
    }

    oportunidadAEliminar?.let { oportunidad ->
        AlertDialog(
            onDismissRequest = { oportunidadAEliminar = null },
            title = { Text("Eliminar oportunidad") },
            text = {
                Text("¿Estás seguro de que deseas eliminar \"${oportunidad.nombre}\"? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        oportunidadAEliminar = null
                        vm.eliminarOportunidad(oportunidad) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Oportunidad eliminada")
                            }
                        }
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { oportunidadAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Oportunidades") },
                actions = {
                    IconButton(onClick = { vm.refrescar() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Actualizar"
                        )
                    }
                    IconButton(onClick = { navController.navigate("organizaciones") }) {
                        Icon(
                            imageVector = Icons.Filled.Business,
                            contentDescription = "Organizaciones"
                        )
                    }
                    IconButton(onClick = { navController.navigate("alertas/$username") }) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Alertas"
                        )
                    }
                    IconButton(
                        onClick = {
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("oportunidad/form") },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Nueva") },
                text = { Text("Nueva oportunidad") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Hola, $username",
                style = MaterialTheme.typography.headlineSmall
            )

            if (cargando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            error?.let { mensajeError ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = mensajeError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { vm.reintentar() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }

            TextButton(onClick = { mostrarBusqueda = !mostrarBusqueda }) {
                Text(if (mostrarBusqueda) "Ocultar búsqueda" else "Buscar")
            }

            //Animación de la barra para no perderme
            AnimatedVisibility(visible = mostrarBusqueda) {
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    label = { Text("Buscar por nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            TextButton(onClick = { mostrarFiltro = !mostrarFiltro }) {
                Text(if (mostrarFiltro) "Ocultar filtro" else "Filtrar por estado")
            }
            if (mostrarFiltro) {
                DropdownMenu(
                    expanded = mostrarFiltro,
                    onDismissRequest = { mostrarFiltro = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todos los estados") },
                        onClick = { estadoFiltro = null; mostrarFiltro = false }
                    )
                    listOf(
                        EstadoOportunidad.EVENTO_IDENTIFICADO,
                        EstadoOportunidad.CONTACTO_PENDIENTE,
                        EstadoOportunidad.CONTACTO_REALIZADO,
                        EstadoOportunidad.PROPUESTA_ENVIADA,
                        EstadoOportunidad.EN_ESPERA_RESPUESTA,
                        EstadoOportunidad.PARTICIPACION_CONFIRMADA,
                        EstadoOportunidad.OPORTUNIDAD_DESCARTADA
                    ).forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado.etiqueta) },
                            onClick = { estadoFiltro = estado; mostrarFiltro = false }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(oportunidades, key = { it.id }) { oportunidad ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                navController.navigate("oportunidad/form/${oportunidad.id}")
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = oportunidad.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${oportunidad.tipo.etiqueta}  ·  ${oportunidad.prioridad.etiqueta}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = oportunidad.fechaActualizacion.toFechaCorta(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = oportunidad.estado.etiqueta,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = oportunidad.descripcion.take(80).let {
                                    if (oportunidad.descripcion.length > 80) "$it..." else it
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (vm.puedeEliminar(oportunidad, usuarioActual)) {
                                    TextButton(
                                        onClick = { oportunidadAEliminar = oportunidad }
                                    ) {
                                        Text(
                                            text = "Eliminar",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                                TextButton(
                                    onClick = {
                                        navController.navigate("seguimientos/${oportunidad.id}/$username")
                                    }
                                ) {
                                    Text("Ver historial")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OportunidadesScreenPreview() {
    OportunidadesScreen(
        username = "admin",
        navController = rememberNavController(),
        vm = OportunidadesViewModel(OportunidadRepositorioPrueba())
    )
}