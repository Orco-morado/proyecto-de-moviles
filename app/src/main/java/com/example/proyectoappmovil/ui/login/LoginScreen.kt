package com.example.proyectoappmovil.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectoappmovil.R
import com.example.proyectoappmovil.ui.theme.AppButton
import com.example.proyectoappmovil.ui.theme.AppCard
import com.example.proyectoappmovil.ui.theme.AppErrorText
import com.example.proyectoappmovil.ui.theme.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    vm: LoginViewModel = viewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()
    var showPass by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoduoc),
                    contentDescription = "Logo Duoc UC",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .padding(24.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bienvenido",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Inicia sesión para continuar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AppTextField(
                        value = state.username,
                        onValueChange = vm::onUsernameChange,
                        label = "Usuario",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = state.password,
                        onValueChange = vm::onPasswordChange,
                        label = "Contraseña",
                        isError = state.error != null,
                        errorText = state.error,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            TextButton(onClick = { showPass = !showPass }) {
                                Text(
                                    text = if (showPass) "Ocultar" else "Ver",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        },
                        visualTransformation = if (showPass) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
                    ) {
                        AppButton(
                            onClick = {
                                vm.submit { user ->
                                    navController.navigate("oportunidades/$user") {
                                        popUpTo("login") { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            text = "Iniciar sesión",
                            loading = state.isLoading,
                            enabled = !state.isLoading
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        navController = rememberNavController(),
        vm = LoginViewModel()
    )
}
