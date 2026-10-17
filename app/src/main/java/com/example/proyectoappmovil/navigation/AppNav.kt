package com.example.proyectoappmovil.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectoappmovil.ui.eventos.EventosScreen
import com.example.proyectoappmovil.ui.login.LoginScreen
import com.example.proyectoappmovil.ui.oportunidades.OportunidadesScreen
import com.example.proyectoappmovil.ui.oportunidades.OportunidadFormScreen
import com.example.proyectoappmovil.ui.seguimiento.SeguimientoScreen

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable(
            route = "oportunidades/{username}",
            arguments = listOf(
                navArgument("username") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username").orEmpty()
            OportunidadesScreen(username = username, navController = navController)
        }

        composable("oportunidad/form") {
            OportunidadFormScreen(navController = navController)
        }

        composable(
            route = "oportunidad/form/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            OportunidadFormScreen(navController = navController, id = id)
        }

        composable("eventos") {
            EventosScreen(navController = navController)
        }

        composable(
            route = "seguimientos/{oportunidadId}/{username}",
            arguments = listOf(
                navArgument("oportunidadId") { type = NavType.LongType },
                navArgument("username") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val oportunidadId = backStackEntry.arguments?.getLong("oportunidadId") ?: 0L
            SeguimientoScreen(oportunidadId = oportunidadId, navController = navController)
        }
    }
}
