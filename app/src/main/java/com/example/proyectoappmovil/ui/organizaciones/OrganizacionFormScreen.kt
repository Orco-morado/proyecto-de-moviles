package com.example.proyectoappmovil.ui.organizaciones

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectoappmovil.data.model.TipoOrganizacion
import com.example.proyectoappmovil.ubicacion.UbicacionHelper
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionFormScreen(
    id: Long? = null,
    navController: NavController,
    vm: OrganizacionFormViewModel = viewModel(factory = OrganizacionFormViewModel.Factory(id))
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by vm.uiState.collectAsStateWithLifecycle(OrganizacionFormUiState())
    val contexto = LocalContext.current
    val alcance = rememberCoroutineScope()

    var tipoExpanded by remember { mutableStateOf(false) }

    fun capturarUbicacion() {
        alcance.launch {
            val ubicacion = UbicacionHelper.obtenerUbicacion(contexto)
            if (ubicacion != null) {
                vm.onUbicacionCapturada(ubicacion.first, ubicacion.second)
                snackbarHostState.showSnackbar("Ubicación capturada")
            } else {
                snackbarHostState.showSnackbar("No se pudo obtener la ubicación")
            }
        }
    }

    val lanzadorPermisoUbicacion = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        if (permisos.values.any { it }) {
            capturarUbicacion()
        } else {
            alcance.launch {
                snackbarHostState.showSnackbar("Permiso de ubicación no concedido")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (id == null) "Nueva organización" else "Editar organización") },
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.nombre,
                onValueChange = vm::onNombreChange,
                label = { Text("Nombre de la organización") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.errores.containsKey("nombre"),
                trailingIcon = {
                    if (state.errores.containsKey("nombre")) {
                        Icon(Icons.Filled.Warning, contentDescription = "Error")
                    }
                },
                supportingText = { state.errores["nombre"]?.let { Text(it) } }
            )

            TextButton(onClick = { tipoExpanded = !tipoExpanded }) {
                Text(if (tipoExpanded) "Ocultar tipo" else "Tipo de organización")
            }
            DropdownMenu(
                expanded = tipoExpanded,
                onDismissRequest = { tipoExpanded = false }
            ) {
                TipoOrganizacion.values().forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo.etiqueta) },
                        onClick = {
                            vm.onTipoSeleccionado(tipo)
                            tipoExpanded = false
                        }
                    )
                }
            }
            Text(
                text = state.tipo?.etiqueta ?: "Tipo sin seleccionar",
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
                value = state.rubro,
                onValueChange = vm::onRubroChange,
                label = { Text("Rubro") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.comuna,
                onValueChange = vm::onComunaChange,
                label = { Text("Comuna") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.errores.containsKey("comuna"),
                trailingIcon = {
                    if (state.errores.containsKey("comuna")) {
                        Icon(Icons.Filled.Warning, contentDescription = "Error")
                    }
                },
                supportingText = { state.errores["comuna"]?.let { Text(it) } }
            )

            OutlinedTextField(
                value = state.region,
                onValueChange = vm::onRegionChange,
                label = { Text("Región") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.errores.containsKey("region"),
                trailingIcon = {
                    if (state.errores.containsKey("region")) {
                        Icon(Icons.Filled.Warning, contentDescription = "Error")
                    }
                },
                supportingText = { state.errores["region"]?.let { Text(it) } }
            )

            OutlinedTextField(
                value = state.sitioWeb,
                onValueChange = vm::onSitioWebChange,
                label = { Text("Sitio web (http:// o https://)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.errores.containsKey("sitioWeb"),
                trailingIcon = {
                    if (state.errores.containsKey("sitioWeb")) {
                        Icon(Icons.Filled.Warning, contentDescription = "Error")
                    }
                },
                supportingText = { state.errores["sitioWeb"]?.let { Text(it) } }
            )

            OutlinedTextField(
                value = state.publicoAsociado,
                onValueChange = vm::onPublicoAsociadoChange,
                label = { Text("Público asociado") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.observaciones,
                onValueChange = vm::onObservacionesChange,
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Ubicación",
                style = MaterialTheme.typography.titleMedium
            )

            val latitud = state.latitud
            val longitud = state.longitud
            if (latitud != null && longitud != null) {
                Text(
                    text = "Latitud: ${formatearCoordenada(latitud)}\n" +
                        "Longitud: ${formatearCoordenada(longitud)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = vm::onQuitarUbicacion) {
                    Text("Quitar ubicación")
                }
            } else {
                Text(
                    text = "Sin ubicación registrada",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    if (UbicacionHelper.tienePermiso(contexto)) {
                        capturarUbicacion()
                    } else {
                        lanzadorPermisoUbicacion.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Filled.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Capturar ubicación")
            }

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
                    if (mensajeActual == "Organización registrada" ||
                        mensajeActual == "Organización actualizada"
                    ) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

private fun formatearCoordenada(valor: Double): String =
    String.format(Locale.getDefault(), "%.5f", valor)

@Preview(showBackground = true)
@Composable
fun OrganizacionFormScreenPreview() {
    OrganizacionFormScreen(
        id = null,
        navController = rememberNavController(),
        vm = OrganizacionFormViewModel(OrganizacionRepositorioPrueba(), null)
    )
}
