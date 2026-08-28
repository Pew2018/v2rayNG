package com.v2ray.ang.ui.compose

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val Blue = Color(0xFF2196F3)
private val FixedLight = lightColorScheme(primary = Blue, onPrimary = Color.White, secondary = Blue, onSecondary = Color.White, tertiary = Blue, onTertiary = Color.White, background = Color(0xFFFAFAFA), onBackground = Color(0xFF212121), surface = Color.White, onSurface = Color(0xFF212121), surfaceVariant = Color(0xFFECEFF1), onSurfaceVariant = Color(0xFF616161), outline = Color(0xFF9E9E9E), outlineVariant = Color(0xFFD0D0D0), inverseSurface = Color(0xFF333333), inverseOnSurface = Color.White, inversePrimary = Color(0xFF90CAF9), surfaceTint = Blue)
private val FixedDark = darkColorScheme(primary = Blue, onPrimary = Color.White, secondary = Blue, onSecondary = Color.White, tertiary = Blue, onTertiary = Color.White, background = Color(0xFF303030), onBackground = Color(0xFFF5F5F5), surface = Color(0xFF424242), onSurface = Color.White, surfaceVariant = Color(0xFF505050), onSurfaceVariant = Color(0xFFD0D0D0), outline = Color(0xFF9E9E9E), outlineVariant = Color(0xFF666666), inverseSurface = Color(0xFFF5F5F5), inverseOnSurface = Color(0xFF212121), inversePrimary = Color(0xFF90CAF9), surfaceTint = Blue)

val LocalLegacyStyle = androidx.compose.runtime.compositionLocalOf { false }

@Composable
fun DualAppTheme(darkTheme: Boolean = resolveDarkTheme(), content: @Composable () -> Unit) {
    val context = LocalContext.current
    val dynamic by ThemeManager.dynamicColorEnabled.collectAsState()
    val accent by ThemeCustomization.customAccent.collectAsState()
    val custom = accent.takeIf { it.isNotBlank() }?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() }
    val scheme = when {
        custom != null -> if (darkTheme) darkColorScheme(primary = custom, onPrimary = Color.White, secondary = custom, onSecondary = Color.White, tertiary = custom, onTertiary = Color.White, background = FixedDark.background, onBackground = FixedDark.onBackground, surface = FixedDark.surface, onSurface = FixedDark.onSurface, surfaceVariant = FixedDark.surfaceVariant, onSurfaceVariant = FixedDark.onSurfaceVariant, outline = FixedDark.outline, outlineVariant = FixedDark.outlineVariant, inverseSurface = FixedDark.inverseSurface, inverseOnSurface = FixedDark.inverseOnSurface, inversePrimary = custom, surfaceTint = custom) else lightColorScheme(primary = custom, onPrimary = Color.White, primaryContainer = custom.copy(alpha = 0.14f), onPrimaryContainer = Color(0xFF212121), secondary = custom, onSecondary = Color.White, secondaryContainer = custom.copy(alpha = 0.14f), onSecondaryContainer = Color(0xFF212121), tertiary = custom, onTertiary = Color.White, tertiaryContainer = custom.copy(alpha = 0.14f), onTertiaryContainer = Color(0xFF212121), background = FixedLight.background, onBackground = FixedLight.onBackground, surface = FixedLight.surface, onSurface = FixedLight.onSurface, surfaceVariant = FixedLight.surfaceVariant, onSurfaceVariant = FixedLight.onSurfaceVariant, outline = FixedLight.outline, outlineVariant = FixedLight.outlineVariant, inverseSurface = FixedLight.inverseSurface, inverseOnSurface = FixedLight.inverseOnSurface, inversePrimary = custom, surfaceTint = custom)
        dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> FixedDark
        else -> FixedLight
    }
    MaterialTheme(colorScheme = scheme) { CompositionLocalProvider(LocalLegacyStyle provides false) { content() } }
}
