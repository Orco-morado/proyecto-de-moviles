package com.example.proyectoappmovil.ui.oportunidades

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.proyectoappmovil.data.model.Prioridad
import com.example.proyectoappmovil.data.model.TipoEvento
import com.example.proyectoappmovil.data.repository.OportunidadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadesScreen(
    username: String,
    navController: NavController,
    vm: OportunidadesViewModel = viewModel(factory = OportunidadesViewModel.Factory)
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var textoBusqueda by remember { mutableStateOf("") }
    var estadoFiltro by remember { mutableStateOf<EstadoOportunidad?>(null) }
    var mostrarBusqueda by remember { mutableStateOf(false) }
    var mostrarFiltro by remember { mutableStateOf(false) }

    val oportunidades by vm.observarFiltradas(estadoFiltro, null, null, textoBusqueda)
        .collectAsStateWithLifecycle(emptyList())

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Oportunidades") })
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

            TextButton(onClick = { mostrarBusqueda = !mostrarBusqueda }) {
                Text(if (mostrarBusqueda) "Ocultar búsqueda" else "Buscar")
            }
            if (mostrarBusqueda) {
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

            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

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
                                        text = formatoFecha.format(oportunidad.fechaActualizacion),
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
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { navController.navigate("oportunidad/form") },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text("Nueva oportunidad")
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
