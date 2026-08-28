package com.v2ray.ang.ui.compose

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.v2ray.ang.AppConfig
import com.v2ray.ang.handler.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val PixelBlue = Color(0xFF2196F3)
private val PixelBlueLight = Color(0xFF90CAF9)
private val PixelBlueContainer = Color(0xFFBBDEFB)
private val PixelBlueDarkContainer = Color(0xFF0D47A1)
private val PixelToolbar = Color(0xFF333333)
private val LightBackground = Color(0xFFFAFAFA)
private val DarkBackground = Color(0xFF303030)
private val DarkSurface = Color(0xFF424242)

private val PixelLightColor = androidx.compose.material3.lightColorScheme(
    primary = PixelBlue, onPrimary = Color.White,
    primaryContainer = PixelBlueContainer, onPrimaryContainer = Color(0xFF0D47A1),
    secondary = PixelBlue, onSecondary = Color.White,
    secondaryContainer = PixelBlueContainer, onSecondaryContainer = Color(0xFF0D47A1),
    tertiary = PixelBlue, onTertiary = Color.White,
    tertiaryContainer = PixelBlueContainer, onTertiaryContainer = Color(0xFF0D47A1),
    background = LightBackground, onBackground = Color(0xFF212121),
    surface = Color.White, onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFECEFF1), onSurfaceVariant = Color(0xFF616161),
    outline = Color(0xFF9E9E9E), outlineVariant = Color(0xFFD0D0D0),
    inverseSurface = PixelToolbar, inverseOnSurface = Color.White,
    inversePrimary = PixelBlueLight, surfaceTint = PixelBlue,
)

private val PixelDarkColor = darkColorScheme(
    primary = PixelBlue, onPrimary = Color.White,
    primaryContainer = PixelBlueDarkContainer, onPrimaryContainer = Color.White,
    secondary = PixelBlue, onSecondary = Color.White,
    secondaryContainer = PixelBlueDarkContainer, onSecondaryContainer = Color.White,
    tertiary = PixelBlue, onTertiary = Color.White,
    tertiaryContainer = PixelBlueDarkContainer, onTertiaryContainer = Color.White,
    background = DarkBackground, onBackground = Color(0xFFF5F5F5),
    surface = DarkSurface, onSurface = Color.White,
    surfaceVariant = Color(0xFF505050), onSurfaceVariant = Color(0xFFD0D0D0),
    outline = Color(0xFF9E9E9E), outlineVariant = Color(0xFF666666),
    inverseSurface = Color(0xFFF5F5F5), inverseOnSurface = Color(0xFF212121),
    inversePrimary = PixelBlueLight, surfaceTint = PixelBlue,
)

private val PixelShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp), small = RoundedCornerShape(2.dp),
    medium = RoundedCornerShape(2.dp), large = RoundedCornerShape(3.dp),
    extraLarge = RoundedCornerShape(4.dp),
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp), small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp), large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp), largeIncreased = RoundedCornerShape(36.dp),
    extraLargeIncreased = RoundedCornerShape(40.dp), extraExtraLarge = RoundedCornerShape(48.dp),
)

val colorPing = PixelBlue
val colorPingRed = Color(0xFFFF5252)
val colorConfigType = Color(0xFF616161)
val colorFabActive = PixelBlue
val colorFabInactiveLight = Color(0xFF9E9E9E)
val colorFabInactiveDark = Color(0xFF616161)
val dividerColorLight = Color(0xFFE0E0E0)
val dividerColorDark = Color(0xFF555555)
val toastNormalBgLight = Color(0xB3353A3E)
val toastNormalBgDark = Color(0xB34A4F54)
val toastSuccessBg = Color(0xB3388E3C)
val toastErrorBg = Color(0xB3D50000)
val toastInfoBg = Color(0xB32196F3)
val toastIconCircleBg = Color(0x33FFFFFF)
val toastTextColor = Color.White

object ThemeManager {
    private val _themeMode = MutableStateFlow(MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()
    private val _uiStyle = MutableStateFlow(MmkvManager.decodeSettingsString(UiPreferences.PREF_UI_STYLE, UiPreferences.STYLE_MATERIAL3) ?: UiPreferences.STYLE_MATERIAL3)
    val uiStyle: StateFlow<String> = _uiStyle.asStateFlow()
    private val _dynamicColorEnabled = MutableStateFlow(MmkvManager.decodeSettingsBool(UiPreferences.PREF_DYNAMIC_COLOR, true))
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled.asStateFlow()

    fun setThemeMode(mode: String) { MmkvManager.encodeSettings(AppConfig.PREF_UI_MODE_NIGHT, mode); _themeMode.value = mode }
    fun setUiStyle(style: String) { MmkvManager.encodeSettings(UiPreferences.PREF_UI_STYLE, style); _uiStyle.value = style }
    fun setDynamicColorEnabled(enabled: Boolean) { MmkvManager.encodeSettings(UiPreferences.PREF_DYNAMIC_COLOR, enabled); _dynamicColorEnabled.value = enabled }
    fun refresh() {
        _themeMode.value = MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
        _uiStyle.value = MmkvManager.decodeSettingsString(UiPreferences.PREF_UI_STYLE, UiPreferences.STYLE_MATERIAL3) ?: UiPreferences.STYLE_MATERIAL3
        _dynamicColorEnabled.value = MmkvManager.decodeSettingsBool(UiPreferences.PREF_DYNAMIC_COLOR, true)
    }
    fun normalizeAccentColor(value: String): String? {
        if (value.isBlank()) return ""
        val candidate = if (value.startsWith("#")) value else "#$value"
        return if (candidate.matches(Regex("#[0-9A-Fa-f]{6}([0-9A-Fa-f]{2})?"))) candidate.uppercase() else null
    }
    fun setCustomAccentColor(value: String) {
        val normalized = normalizeAccentColor(value) ?: return
        MmkvManager.encodeSettings(UiPreferences.PREF_ACCENT_COLOR, normalized)
        if (normalized.isNotBlank()) setDynamicColorEnabled(false)
    }
}

@Composable
fun resolveDarkTheme(): Boolean {
    val mode by ThemeManager.themeMode.collectAsState()
    return when (mode) { "1" -> false; "2" -> true; else -> isSystemInDarkTheme() }
}

val LocalDarkTheme = compositionLocalOf { false }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppTheme(darkTheme: Boolean = resolveDarkTheme(), content: @Composable () -> Unit) {
    val style by ThemeManager.uiStyle.collectAsState()
    val dynamic by ThemeManager.dynamicColorEnabled.collectAsState()
    val context = LocalContext.current
    val accentText = MmkvManager.decodeSettingsString(UiPreferences.PREF_ACCENT_COLOR, "") ?: ""
    val accent = accentText.takeIf { it.isNotBlank() }?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            WindowCompat.getInsetsController(activity.window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    val snackbarController = rememberAppSnackbarController()
    CompositionLocalProvider(LocalDarkTheme provides darkTheme, LocalAppSnackbar provides snackbarController) {
        val appContent: @Composable () -> Unit = {
            Box(Modifier.fillMaxSize()) {
                AppSnackbarBridge(controller = snackbarController)
                content()
                AppSnackbarHost(hostState = snackbarController.hostState)
            }
        }
        if (style == UiPreferences.STYLE_LEGACY) {
            MaterialTheme(colorScheme = if (darkTheme) PixelDarkColor else PixelLightColor, shapes = PixelShapes, content = appContent)
        } else {
            val scheme = when {
                accent != null -> if (darkTheme) {
                    darkColorScheme().copy(primary = accent, primaryContainer = accent.copy(alpha = 0.28f), onPrimaryContainer = Color.White, secondary = accent, tertiary = accent, inversePrimary = accent, surfaceTint = accent)
                } else {
                    expressiveLightColorScheme().copy(primary = accent, primaryContainer = accent.copy(alpha = 0.16f), onPrimaryContainer = Color(0xFF202124), secondary = accent, tertiary = accent, inversePrimary = accent, surfaceTint = accent)
                }
                dynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                darkTheme -> darkColorScheme()
                else -> expressiveLightColorScheme()
            }
            MaterialExpressiveTheme(colorScheme = scheme, motionScheme = MotionScheme.expressive(), shapes = ExpressiveShapes, content = appContent)
        }
    }
}

@Composable
fun DualAppTheme(darkTheme: Boolean = resolveDarkTheme(), content: @Composable () -> Unit) = AppTheme(darkTheme, content)
