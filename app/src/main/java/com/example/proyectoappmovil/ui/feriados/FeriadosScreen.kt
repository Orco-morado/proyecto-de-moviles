package com.example.proyectoappmovil.ui.feriados

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeriadosScreen(
    navController: NavController,
    vm: FeriadosViewModel = viewModel(factory = FeriadosViewModel.Factory)
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feriados (planificación de activaciones)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
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
            when (val estado = uiState) {
                FeriadosUiState.Cargando -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    Text("Cargando feriados...", modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                FeriadosUiState.Vacio -> {
                    Text("No hay feriados próximos.", style = MaterialTheme.typography.bodyLarge)
                }

                is FeriadosUiState.Error -> {
                    Text(
                        text = estado.mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(onClick = { vm.reintentar() }) {
                        Text("Reintentar")
                    }
                }

                is FeriadosUiState.Exito -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(estado.feriados, key = { it.fechaMillis to it.nombre }) { feriado ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(feriado.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text(feriado.fechaTexto)
                                    Text(
                                        if (feriado.diasFaltan == 1) "Falta 1 día"
                                        else "Faltan ${feriado.diasFaltan} días"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
