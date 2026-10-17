package com.example.proyectoappmovil.ui.seguimiento

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectoappmovil.data.local.entity.Seguimiento
import com.example.proyectoappmovil.data.model.TipoAccionSeguimiento
import com.example.proyectoappmovil.notificaciones.NotificacionHelper
import com.example.proyectoappmovil.ui.oportunidades.OportunidadRepositorioPrueba
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeguimientoScreen(
    oportunidadId: Long,
    navController: NavController,
    vm: SeguimientoViewModel = viewModel(factory = SeguimientoViewModel.Factory(oportunidadId))
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by vm.uiState.collectAsStateWithLifecycle()
    val seguimientos by vm.seguimientos.collectAsStateWithLifecycle()
    val oportunidad by vm.oportunidad.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    var mostrarFormulario by remember { mutableStateOf(false) }

    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    LaunchedEffect(key1 = state.mensaje) {
        val mensajeActual = state.mensaje
        if (mensajeActual != null) {
            snackbarHostState.showSnackbar(mensajeActual)
        }
    }

    LaunchedEffect(key1 = state.recordatorioProgramadoEn) {
        val momento = state.recordatorioProgramadoEn
        if (momento != null) {
            if (NotificacionHelper.tienePermiso(contexto)) {
                NotificacionHelper.programar(
                    context = contexto,
                    titulo = "Seguimiento pendiente",
                    texto = state.recordatorioTexto.orEmpty(),
                    disparoEn = momento,
                    id = 2000 + oportunidadId.toInt()
                )
            }
            vm.consumirRecordatorio()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Historial de seguimiento") },
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
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = oportunidad?.nombre ?: "Cargando oportunidad...",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    oportunidad?.let { actual ->
                        Text(
                            text = "${actual.tipo.etiqueta}  ·  ${actual.estado.etiqueta}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = { mostrarFormulario = !mostrarFormulario },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (mostrarFormulario) "Ocultar formulario" else "Registrar seguimiento")
                }
            }

            item {
                AnimatedVisibility(
                    visible = mostrarFormulario,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    FormularioSeguimiento(state = state, vm = vm)
                }
            }

            item {
                Text(
                    text = "Historial de gestiones",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (seguimientos.isEmpty()) {
                item {
                    Text(
                        text = "Aún no hay gestiones registradas para esta oportunidad",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(seguimientos, key = { it.id }) { seguimiento ->
                FilaSeguimiento(seguimiento = seguimiento, formatoFecha = formatoFecha)
            }
        }
    }
}

@Composable
private fun FormularioSeguimiento(
    state: SeguimientoFormUiState,
    vm: SeguimientoViewModel
) {
    var tipoExpanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier
        .fillMaxWidth()
        .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Nuevo seguimiento",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            TextButton(onClick = { tipoExpanded = !tipoExpanded }) {
                Text("Tipo de acción: ${state.tipoAccion?.etiqueta ?: "Seleccionar"}")
            }
            DropdownMenu(
                expanded = tipoExpanded,
                onDismissRequest = { tipoExpanded = false }
            ) {
                TipoAccionSeguimiento.values().forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo.etiqueta) },
                        onClick = {
                            vm.onTipoAccionSeleccionado(tipo)
                            tipoExpanded = false
                        }
                    )
                }
            }
            TextoError(errores = state.errores, clave = "tipoAccion")

            OutlinedTextField(
                value = state.resultado,
                onValueChange = vm::onResultadoChange,
                label = { Text("Resultado de la gestión") },
                modifier = Modifier.fillMaxWidth()
            )
            TextoError(errores = state.errores, clave = "resultado")

            OutlinedTextField(
                value = state.proximaAccion,
                onValueChange = vm::onProximaAccionChange,
                label = { Text("Próxima acción") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.fechaProximo,
                onValueChange = vm::onFechaProximoChange,
                label = { Text("Fecha próximo seguimiento (yyyy-MM-dd)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            TextoError(errores = state.errores, clave = "fechaProximo")

            OutlinedTextField(
                value = state.observaciones,
                onValueChange = vm::onObservacionesChange,
                label = { Text("Observaciones") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { vm.guardar() },
                enabled = !state.esGuardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.esGuardando) "Guardando..." else "Guardar seguimiento")
            }
        }
    }
}

@Composable
private fun TextoError(
    errores: Map<String, String>,
    clave: String
) {
    AnimatedVisibility(
        visible = errores[clave] != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Text(
            text = errores[clave].orEmpty(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun FilaSeguimiento(
    seguimiento: Seguimiento,
    formatoFecha: SimpleDateFormat
) {
    val atrasado = seguimiento.fechaProximoSeguimiento?.let { fecha ->
        fecha + 24L * 60 * 60 * 1000 < System.currentTimeMillis()
    } ?: false

    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(10.dp)
                .background(
                    color = if (atrasado) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(10.dp))

        Card(
            modifier = Modifier
                .weight(1f)
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = seguimiento.tipoAccion.etiqueta,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formatoFecha.format(seguimiento.fechaHora),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = seguimiento.resultado,
                    style = MaterialTheme.typography.bodyMedium
                )

                if (seguimiento.proximaAccion.isNotBlank()) {
                    Text(
                        text = "Próxima acción: ${seguimiento.proximaAccion}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                seguimiento.fechaProximoSeguimiento?.let { fecha ->
                    Text(
                        text = "Próximo seguimiento: ${formatoFecha.format(fecha)}" +
                            if (atrasado) "  (atrasado)" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (atrasado) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SeguimientoScreenPreview() {
    SeguimientoScreen(
        oportunidadId = 1L,
        navController = rememberNavController(),
        vm = SeguimientoViewModel(
            SeguimientoRepositorioPrueba(),
            OportunidadRepositorioPrueba(),
            1L
        )
    )
}
