package com.example.proyectoappmovil.ui.contactos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectoappmovil.data.model.AreaContacto
import com.example.proyectoappmovil.data.model.EstadoContacto
import com.example.proyectoappmovil.data.model.MedioContacto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactoFormScreen(
    organizacionId: Long,
    contactoId: Long? = null,
    navController: NavController,
    vm: ContactoFormViewModel = viewModel(
        factory = ContactoFormViewModel.Factory(organizacionId, contactoId)
    )
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by vm.uiState.collectAsStateWithLifecycle(ContactoFormUiState())

    var areaExpanded by remember { mutableStateOf(false) }
    var medioExpanded by remember { mutableStateOf(false) }
    var estadoExpanded by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(if (contactoId == null) "Nuevo contacto" else "Editar contacto")
                },
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
                label = { Text("Nombre del contacto") },
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

            OutlinedTextField(
                value = state.cargo,
                onValueChange = vm::onCargoChange,
                label = { Text("Cargo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.errores.containsKey("cargo"),
                trailingIcon = {
                    if (state.errores.containsKey("cargo")) {
                        Icon(Icons.Filled.Warning, contentDescription = "Error")
                    }
                },
                supportingText = { state.errores["cargo"]?.let { Text(it) } }
            )

            OutlinedTextField(
                value = state.correo,
                onValueChange = vm::onCorreoChange,
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.errores.containsKey("correo"),
                trailingIcon = {
                    if (state.errores.containsKey("correo")) {
                        Icon(Icons.Filled.Warning, contentDescription = "Error")
                    }
                },
                supportingText = { state.errores["correo"]?.let { Text(it) } }
            )

            OutlinedTextField(
                value = state.telefono,
                onValueChange = vm::onTelefonoChange,
                label = { Text("Teléfono") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = state.errores.containsKey("telefono"),
                trailingIcon = {
                    if (state.errores.containsKey("telefono")) {
                        Icon(Icons.Filled.Warning, contentDescription = "Error")
                    }
                },
                supportingText = { state.errores["telefono"]?.let { Text(it) } }
            )

            TextButton(onClick = { areaExpanded = !areaExpanded }) {
                Text(if (areaExpanded) "Ocultar área" else "Área")
            }
            DropdownMenu(
                expanded = areaExpanded,
                onDismissRequest = { areaExpanded = false }
            ) {
                AreaContacto.values().forEach { area ->
                    DropdownMenuItem(
                        text = { Text(area.etiqueta) },
                        onClick = {
                            vm.onAreaSeleccionada(area)
                            areaExpanded = false
                        }
                    )
                }
            }
            Text(
                text = state.area.etiqueta,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(onClick = { medioExpanded = !medioExpanded }) {
                Text(if (medioExpanded) "Ocultar medio preferente" else "Medio preferente")
            }
            DropdownMenu(
                expanded = medioExpanded,
                onDismissRequest = { medioExpanded = false }
            ) {
                MedioContacto.values().forEach { medio ->
                    DropdownMenuItem(
                        text = { Text(medio.etiqueta) },
                        onClick = {
                            vm.onMedioSeleccionado(medio)
                            medioExpanded = false
                        }
                    )
                }
            }
            Text(
                text = state.medioPreferente.etiqueta,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(onClick = { estadoExpanded = !estadoExpanded }) {
                Text(if (estadoExpanded) "Ocultar estado" else "Estado")
            }
            DropdownMenu(
                expanded = estadoExpanded,
                onDismissRequest = { estadoExpanded = false }
            ) {
                EstadoContacto.values().forEach { estado ->
                    DropdownMenuItem(
                        text = { Text(estado.etiqueta) },
                        onClick = {
                            vm.onEstadoSeleccionado(estado)
                            estadoExpanded = false
                        }
                    )
                }
            }
            Text(
                text = state.estado.etiqueta,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
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
                    if (mensajeActual == "Contacto registrado" ||
                        mensajeActual == "Contacto actualizado"
                    ) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactoFormScreenPreview() {
    ContactoFormScreen(
        organizacionId = 1L,
        contactoId = null,
        navController = rememberNavController(),
        vm = ContactoFormViewModel(ContactoRepositorioPrueba(), 1L, null)
    )
}
