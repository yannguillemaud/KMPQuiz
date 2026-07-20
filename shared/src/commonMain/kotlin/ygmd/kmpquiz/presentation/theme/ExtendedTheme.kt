package ygmd.kmpquiz.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Seed: Indigo #3D5AFE — focus, trust, educational feel
// Extended semantic colors outside the core M3 palette
val SuccessLight = Color(0xFF1B6E22)    // tone-30 green, 7:1 on white — WCAG AA
val OnSuccessLight = Color.White
val WarningLight = Color(0xFF7A4F00)    // amber tone-30, 7:1 on white — WCAG AA
val OnWarningLight = Color.White

val SuccessDark = Color(0xFF81C784)     // tone-80 green, ~6.5:1 on dark surface
val OnSuccessDark = Color.Black
val WarningDark = Color(0xFFFFB300)
val OnWarningDark = Color.Black

// Tonal background — matches the default M3 ColorScheme background/surface
// (LightColors.background #FFFBFF / DarkColors.background #1C1B1F) so HomeScreen's
// tonal surface stays consistent with the rest of the app for each theme mode.
val TonalBackgroundLight = Color(0xFFFFFBFF)
val TonalBackgroundDark = Color(0xFF1C1B1F)

@Immutable
data class ExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val tonalBackground: Color,
)

val LightExtendedColors = ExtendedColors(
    success = SuccessLight,
    onSuccess = OnSuccessLight,
    warning = WarningLight,
    onWarning = OnWarningLight,
    tonalBackground = TonalBackgroundLight,
)

val DarkExtendedColors = ExtendedColors(
    success = SuccessDark,
    onSuccess = OnSuccessDark,
    warning = WarningDark,
    onWarning = OnWarningDark,
    tonalBackground = TonalBackgroundDark,
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

val LightColors = lightColorScheme(
    primary = Color(0xFF3D5AFE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE1FF),
    onPrimaryContainer = Color(0xFF00105C),
    // Secondary — M3 standard violet (#6750A4), shifted from #5B5EA6 for stronger category contrast
    secondary = Color(0xFF6750A4),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEADDFF),
    onSecondaryContainer = Color(0xFF21005D),
    // Tertiary — Warm Coral (#E8621C), promoted from amber; AA Large only (3.2:1 on white)
    tertiary = Color(0xFFE8621C),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDBCC),
    onTertiaryContainer = Color(0xFF3B0A00),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE3E1EC),
    onSurfaceVariant = Color(0xFF46464F),
    outline = Color(0xFF777680),
)

val DarkColors = darkColorScheme(
    primary = Color(0xFFBAC3FF),
    onPrimary = Color(0xFF00218E),
    primaryContainer = Color(0xFF0034CA),
    onPrimaryContainer = Color(0xFFDDE1FF),
    // Secondary dark
    secondary = Color(0xFFD0BCFF),
    onSecondary = Color(0xFF381E72),
    secondaryContainer = Color(0xFF4F378B),
    onSecondaryContainer = Color(0xFFEADDFF),
    // Tertiary dark — light coral on dark background
    tertiary = Color(0xFFFFB59A),
    onTertiary = Color(0xFF5C1600),
    tertiaryContainer = Color(0xFF7D2D00),
    onTertiaryContainer = Color(0xFFFFDBCC),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFF9DEDC),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF46464F),
    onSurfaceVariant = Color(0xFFC9C5D0),
    outline = Color(0xFF928F9A),
)
