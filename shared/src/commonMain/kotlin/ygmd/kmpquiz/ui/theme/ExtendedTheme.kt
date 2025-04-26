package ygmd.kmpquiz.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ---- Couleurs de base ----
val SuccessLight = Color(0xFF4CAF50) // vert Material
val OnSuccessLight = Color.White
val WarningLight = Color(0xFFFFC107) // jaune Material
val OnWarningLight = Color.Black

val SuccessDark = Color(0xFF81C784) // vert plus doux
val OnSuccessDark = Color.Black
val WarningDark = Color(0xFFFFB300)
val OnWarningDark = Color.Black

// ---- Extended Colors ----
@Immutable
data class ExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
)

val LightExtendedColors = ExtendedColors(
    success = SuccessLight,
    onSuccess = OnSuccessLight,
    warning = WarningLight,
    onWarning = OnWarningLight,
)

val DarkExtendedColors = ExtendedColors(
    success = SuccessDark,
    onSuccess = OnSuccessDark,
    warning = WarningDark,
    onWarning = OnWarningDark,
)

val LocalExtendedColors = staticCompositionLocalOf {
    LightExtendedColors
}

// ---- Thème principal ----
val LightColors = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    error = Color(0xFFB00020),
    onError = Color.White,
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E1E5),
)