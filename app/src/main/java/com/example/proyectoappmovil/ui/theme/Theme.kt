package com.example.proyectoappmovil.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AtenasRedLight,
    onPrimary = AtenasRedDark,
    secondary = WarmGrayLight,
    tertiary = GoldLight
)

private val LightColorScheme = lightColorScheme(
    primary = AtenasRed,
    onPrimary = Color.White,
    secondary = WarmGray,
    tertiary = Gold
)

@Composable
fun ProyectoAppMovilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
