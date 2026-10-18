package com.example.proyectoappmovil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DuocAzulClaro,
    onPrimary = DuocAzulOscuro,
    secondary = AtenasRedLight,
    onSecondary = AtenasRedDark,
    tertiary = GoldLight,
    surface = DuocAzulOscuro,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = DuocAzul,
    onPrimary = Color.White,
    secondary = AtenasRed,
    onSecondary = Color.White,
    tertiary = Gold,
    background = Color.White,
    surface = Color.White
)

@Composable
fun ProyectoAppMovilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
