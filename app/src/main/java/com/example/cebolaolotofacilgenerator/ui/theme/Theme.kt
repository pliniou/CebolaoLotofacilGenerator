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

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
private val DarkColorScheme =
        darkColorScheme(primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80)

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

@Composable
fun CebolaoLotofacilGeneratorTheme(
        tema: TemaAplicativo = TemaAplicativo.SISTEMA,
        content: @Composable () -> Unit
) {
    val useDarkTheme: Boolean
    val useDynamicColor: Boolean

    when (tema) {
        TemaAplicativo.CLARO -> {
            useDarkTheme = false
            useDynamicColor = false
        }
        TemaAplicativo.ESCURO -> {
            useDarkTheme = true
            useDynamicColor = false
        }
        TemaAplicativo.SISTEMA -> {
            useDarkTheme = isSystemInDarkTheme()
            useDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        }
    }

    val colorScheme =
            when {
                useDynamicColor -> {
                    val context = LocalContext.current
                    if (useDarkTheme) dynamicDarkColorScheme(context)
                    else dynamicLightColorScheme(context)
                }
                useDarkTheme -> DarkColorScheme
                else -> LightColorScheme
            }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    !useDarkTheme
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
