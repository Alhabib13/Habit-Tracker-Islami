package com.islami.Aha.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenLight,
    onPrimaryContainer = Gray900,

    secondary = Gray600,
    onSecondary = Color.White,
    secondaryContainer = Gray100,
    onSecondaryContainer = Gray900,

    tertiary = IslamicGold,
    onTertiary = Gray900,
    tertiaryContainer = IslamicGold.copy(alpha = 0.2f),
    onTertiaryContainer = Gray900,

    background = BackgroundLight,
    onBackground = Gray900,

    surface = SurfaceWhite,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray600,

    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed,

    outline = Gray300,
    outlineVariant = Gray200,

    scrim = Gray900.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    onPrimary = Gray900,
    primaryContainer = GreenDark,
    onPrimaryContainer = GreenLight,

    secondary = Gray400,
    onSecondary = Gray900,
    secondaryContainer = Gray700,
    onSecondaryContainer = Gray100,

    tertiary = IslamicGold,
    onTertiary = Gray900,
    tertiaryContainer = IslamicGold.copy(alpha = 0.3f),
    onTertiaryContainer = IslamicGold,

    background = Gray900,
    onBackground = Gray100,

    surface = Gray900,
    onSurface = Gray100,
    surfaceVariant = Gray700,
    onSurfaceVariant = Gray300,

    error = ErrorRed,
    onError = Gray900,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,

    outline = Gray600,
    outlineVariant = Gray700,

    scrim = Color.Black.copy(alpha = 0.7f)
)

@Composable
fun HabitIslamiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = GreenPrimary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}