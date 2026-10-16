package com.example.proyectoappmovil.ui.oportunidades

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadFormScreen(
    id: Long? = null,
    navController: NavController,
    vm: OportunidadFormViewModel = viewModel(factory = OportunidadFormViewModel.Factory(id))
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by vm.uiState.collectAsStateWithLifecycle(OportunidadFormUiState())

    var tipoExpanded by remember { mutableStateOf(false) }
    var prioridadExpanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Nueva oportunidad") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.nombre,
                onValueChange = vm::onNombreChange,
                label = { Text("Nombre de la oportunidad") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            var tipoSeleccionado by remember { mutableStateOf(state.tipo ?: TipoEvento.EVENTO_CORPORATIVO) }
            TextButton(onClick = { tipoExpanded = !tipoExpanded }) {
                Text(if (tipoExpanded) "Ocultar tipo" else "Tipo de evento")
            }
            DropdownMenu(
                expanded = tipoExpanded,
                onDismissRequest = { tipoExpanded = false }
            ) {
                TipoEvento.values().forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo.etiqueta) },
                        onClick = {
                            tipoSeleccionado = tipo
                            vm.onTipoSeleccionado(tipo)
                            tipoExpanded = false
                        }
                    )
                }
            }
            Text(
                text = tipoSeleccionado.etiqueta,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            state.errores["tipo"]?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            OutlinedTextField(
                value = state.fuenteInformacion,
                onValueChange = vm::onFuenteInformacionChange,
                label = { Text("Fuente de información") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.fechaDeteccion,
                onValueChange = vm::onFechaDeteccionChange,
                label = { Text("Fecha de detección (yyyy-MM-dd)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.descripcion,
                onValueChange = vm::onDescripcionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            var prioridadSeleccionada by remember { mutableStateOf(state.prioridad ?: Prioridad.MEDIA) }
            TextButton(onClick = { prioridadExpanded = !prioridadExpanded }) {
                Text(if (prioridadExpanded) "Ocultar prioridad" else "Prioridad")
            }
            DropdownMenu(
                expanded = prioridadExpanded,
                onDismissRequest = { prioridadExpanded = false }
            ) {
                Prioridad.values().forEach { prioridad ->
                    DropdownMenuItem(
                        text = { Text(prioridad.etiqueta) },
                        onClick = {
                            prioridadSeleccionada = prioridad
                            vm.onPrioridadSeleccionada(prioridad)
                            prioridadExpanded = false
                        }
                    )
                }
            }
            Text(
                text = prioridadSeleccionada.etiqueta,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = state.potencialComercial,
                onValueChange = vm::onPotencialComercialChange,
                label = { Text("Potencial comercial estimado") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.observaciones,
                onValueChange = vm::onObservacionesChange,
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { vm.guardar() },
                    enabled = !state.esGuardando,
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text(if (state.esGuardando) "Guardando..." else "Guardar")
                }
            }

            LaunchedEffect(key1 = state.mensaje) {
                val mensajeActual = state.mensaje
                if (mensajeActual != null) {
                    snackbarHostState.showSnackbar(mensajeActual)
                    if (mensajeActual == "Oportunidad registrada") {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OportunidadFormScreenPreview() {
    OportunidadFormScreen(
        id = null,
        navController = rememberNavController(),
        vm = OportunidadFormViewModel(OportunidadRepositorioPrueba(), null)
    )
}
