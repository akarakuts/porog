/** Theme — светлая кремовая и тёмная бирюзовая схемы без dynamic color. */
package ru.akarakuts.porog.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColors = darkColorScheme(
    primary = Amber,
    onPrimary = Color(0xFF2A2100),
    primaryContainer = TealMid,
    onPrimaryContainer = Cream,
    secondary = Amber,
    onSecondary = Color(0xFF2A2100),
    secondaryContainer = Color(0xFF4A3A10),
    onSecondaryContainer = Color(0xFFFFE8B0),
    tertiary = CreamDeep,
    onTertiary = TealDeep,
    background = Color(0xFF081F1F),
    onBackground = Mist,
    surface = Color(0xFF0B2A2A),
    onSurface = Mist,
    surfaceVariant = Color(0xFF164242),
    onSurfaceVariant = Color(0xFFC9D4D3),
    surfaceContainerLowest = Color(0xFF061818),
    surfaceContainerLow = Color(0xFF0E3333),
    surfaceContainer = Color(0xFF134040),
    surfaceContainerHigh = Color(0xFF1A4C4C),
    surfaceContainerHighest = Color(0xFF245858),
    outline = Color(0xFF7A9A9A),
    outlineVariant = Color(0xFF2F5555),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

private val LightColors = lightColorScheme(
    primary = TealMid,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD5EBEA),
    onPrimaryContainer = TealDeep,
    secondary = AmberDeep,
    onSecondary = Color(0xFF2A2100),
    secondaryContainer = Color(0xFFFFE8B0),
    onSecondaryContainer = Color(0xFF3D2E00),
    tertiary = Color(0xFF8B5E3C),
    onTertiary = Color.White,
    background = Cream,
    onBackground = Ink,
    surface = Cream,
    onSurface = Ink,
    surfaceVariant = CreamDeep,
    onSurfaceVariant = Color(0xFF4A5C5B),
    surfaceContainerLowest = Color(0xFFFFFBF6),
    surfaceContainerLow = Color(0xFFF7EFE4),
    surfaceContainer = Color(0xFFEFE4D6),
    surfaceContainerHigh = Color(0xFFE7DACC),
    surfaceContainerHighest = Color(0xFFDED1C1),
    outline = Color(0xFF7A6F62),
    outlineVariant = Color(0xFFD4C6B4),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
)

@Composable
fun PorogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insets = WindowCompat.getInsetsController(window, view)
            insets.isAppearanceLightStatusBars = !darkTheme
            insets.isAppearanceLightNavigationBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = scheme,
        typography = Typography,
        shapes = PorogShapes,
        content = content,
    )
}
