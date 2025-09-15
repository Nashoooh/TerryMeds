package com.example.terrymeds.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Esquemas de colores accesibles con alto contraste
private val AccessibleDarkColorScheme = darkColorScheme(
    primary = AccessibleDarkPrimary,
    secondary = AccessibleDarkSecondary,
    tertiary = AccessibleDarkTertiary,
    error = AccessibleDarkError,
    background = AccessibleDarkBackground,
    surface = AccessibleDarkSurface,
    surfaceVariant = AccessibleDarkSurfaceVariant,
    onPrimary = AccessibleDarkOnPrimary,
    onSecondary = AccessibleDarkOnSecondary,
    onTertiary = AccessibleDarkOnTertiary,
    onError = AccessibleDarkOnError,
    onBackground = AccessibleDarkOnBackground,
    onSurface = AccessibleDarkOnSurface,
    onSurfaceVariant = AccessibleDarkOnSurfaceVariant,
    outline = AccessibleDarkOutline
)

private val AccessibleLightColorScheme = lightColorScheme(
    primary = AccessiblePrimary,
    secondary = AccessibleSecondary,
    tertiary = AccessibleTertiary,
    error = AccessibleError,
    background = AccessibleBackground,
    surface = AccessibleSurface,
    surfaceVariant = AccessibleSurfaceVariant,
    onPrimary = AccessibleOnPrimary,
    onSecondary = AccessibleOnSecondary,
    onTertiary = AccessibleOnTertiary,
    onError = AccessibleOnError,
    onBackground = AccessibleOnBackground,
    onSurface = AccessibleOnSurface,
    onSurfaceVariant = AccessibleOnSurfaceVariant,
    outline = AccessibleOutline
)

// Esquemas de colores originales (mantenidos para referencia)
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun TerryMedsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+ but disabled for accessibility
    dynamicColor: Boolean = false, // Deshabilitado para usar colores accesibles
    // Nuevo parámetro para habilitar/deshabilitar modo accesible
    accessibleMode: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !accessibleMode -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        accessibleMode -> {
            if (darkTheme) AccessibleDarkColorScheme else AccessibleLightColorScheme
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val typography = if (accessibleMode) AccessibleTypography else Typography

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

// Función conveniente para forzar el modo accesible
@Composable
fun TerryMedsAccessibleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    TerryMedsTheme(
        darkTheme = darkTheme,
        dynamicColor = false,
        accessibleMode = true,
        content = content
    )
}