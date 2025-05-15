package com.example.cebolaolotofacilgenerator.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.cebolaolotofacilgenerator.viewmodel.MainViewModel.TemaAplicativo

// Cores Padrão
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Tema Azul
val BluePrimary = Color(0xFF1976D2) // Azul 700
val BlueSecondary = Color(0xFF64B5F6) // Azul 300
val BlueTertiary = Color(0xFFBBDEFB) // Azul 100
val OnBluePrimary = Color.White
val OnBlueSecondary = Color.Black
val OnBlueTertiary = Color.Black

// Tema Verde
val GreenPrimary = Color(0xFF388E3C) // Verde 700
val GreenSecondary = Color(0xFF81C784) // Verde 300
val GreenTertiary = Color(0xFFA5D6A7) // Verde 200
val OnGreenPrimary = Color.White
val OnGreenSecondary = Color.Black
val OnGreenTertiary = Color.Black

// Tema Laranja
val OrangePrimary = Color(0xFFF57C00) // Laranja 700
val OrangeSecondary = Color(0xFFFFB74D) // Laranja 300
val OrangeTertiary = Color(0xFFFFE0B2) // Laranja 100
val OnOrangePrimary = Color.Black
val OnOrangeSecondary = Color.Black
val OnOrangeTertiary = Color.Black

// Tema Ciano
val CyanPrimary = Color(0xFF0097A7) // Ciano 700
val CyanSecondary = Color(0xFF4DD0E1) // Ciano 300
val CyanTertiary = Color(0xFFB2EBF2) // Ciano 100
val OnCyanPrimary = Color.White
val OnCyanSecondary = Color.Black
val OnCyanTertiary = Color.Black


private val DarkColorScheme =
        darkColorScheme(
            primary = Purple80,
            secondary = PurpleGrey80,
            tertiary = Pink80
        )

private val LightColorScheme =
        lightColorScheme(
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

private val AppBlueColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    tertiary = BlueTertiary,
    onPrimary = OnBluePrimary,
    onSecondary = OnBlueSecondary,
    onTertiary = OnBlueTertiary,
    background = Color(0xFFF0F4FF), // Fundo levemente azulado
    surface = Color(0xFFF8FAFF),    // Superfície levemente azulada
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val AppGreenColorScheme = lightColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    tertiary = GreenTertiary,
    onPrimary = OnGreenPrimary,
    onSecondary = OnGreenSecondary,
    onTertiary = OnGreenTertiary,
    background = Color(0xFFF0FFF0), // Fundo levemente esverdeado
    surface = Color(0xFFF5FFF5),    // Superfície levemente esverdeada
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val AppOrangeColorScheme = lightColorScheme(
    primary = OrangePrimary,
    secondary = OrangeSecondary,
    tertiary = OrangeTertiary,
    onPrimary = OnOrangePrimary,
    onSecondary = OnOrangeSecondary,
    onTertiary = OnOrangeTertiary,
    background = Color(0xFFFFF8E1), // Fundo levemente alaranjado
    surface = Color(0xFFFFFDF5),    // Superfície levemente alaranjada
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val AppCyanColorScheme = lightColorScheme(
    primary = CyanPrimary,
    secondary = CyanSecondary,
    tertiary = CyanTertiary,
    onPrimary = OnCyanPrimary,
    onSecondary = OnCyanSecondary,
    onTertiary = OnCyanTertiary,
    background = Color(0xFFE0F7FA), // Fundo levemente ciano
    surface = Color(0xFFF0FDFF),    // Superfície levemente ciano
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun CebolaoLotofacilGeneratorTheme(
        tema: TemaAplicativo = TemaAplicativo.SISTEMA,
        content: @Composable () -> Unit
) {
    var useDarkTheme: Boolean = isSystemInDarkTheme()
    var colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme // Padrão inicial

    when (tema) {
        TemaAplicativo.CLARO -> {
            useDarkTheme = false
            colorScheme = LightColorScheme
        }
        TemaAplicativo.ESCURO -> {
            useDarkTheme = true
            colorScheme = DarkColorScheme
        }
        TemaAplicativo.SISTEMA -> {
            // useDarkTheme já está definido pelo isSystemInDarkTheme()
            val context = LocalContext.current
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                colorScheme = if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                // Mantém Light/DarkColorScheme padrão para versões < S
                colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme
            }
        }
        TemaAplicativo.AZUL -> {
            useDarkTheme = false // Tema Azul será sempre claro por esta definição
            colorScheme = AppBlueColorScheme
        }
        TemaAplicativo.VERDE -> {
            useDarkTheme = false // Tema Verde será sempre claro por esta definição
            colorScheme = AppGreenColorScheme
        }
        TemaAplicativo.LARANJA -> {
            useDarkTheme = false // Tema Laranja será sempre claro
            colorScheme = AppOrangeColorScheme
        }
        TemaAplicativo.CIANO -> {
            useDarkTheme = false // Tema Ciano será sempre claro
            colorScheme = AppCyanColorScheme
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
