package com.example.proyecto.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// --- ESQUEMA DE COLOR PARA MODO OSCURO ---
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,

    // --- AQUÍ ESTÁ EL CAMBIO ---
    // Le decimos que todo texto "sobre" el fondo o superficies
    // sea de nuestro color gris claro personalizado
    onBackground = TextoModoOscuro,
    onSurface = TextoModoOscuro,
    onPrimary = TextoModoOscuro,
    onSecondary = TextoModoOscuro,
    onTertiary = TextoModoOscuro
)

// --- ESQUEMA DE COLOR PARA MODO CLARO ---
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    // --- AQUÍ ESTÁ EL CAMBIO ---
    // Le decimos que todo texto "sobre" el fondo o superficies
    // sea de color negro, como pediste.
    onBackground = TextoModoClaro,
    onSurface = TextoModoClaro,

    /* Otros colores (texto sobre botones de color, etc.)
       pueden seguir siendo blancos para mejor contraste */
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White
)

@Composable
fun ProyectoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // El 'dynamicColor' es para Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Aplica nuestros esquemas de color personalizados
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // 'Typography' viene de tu archivo Type.kt
        content = content
    )
}