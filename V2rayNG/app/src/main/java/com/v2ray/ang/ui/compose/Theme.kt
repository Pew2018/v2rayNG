package com.v2ray.ang.ui.compose

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

private val LightColor = lightColorScheme(
    primary = PixelBlue,
    onPrimary = Color.White,
    primaryContainer = PixelBlueContainer,
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = PixelBlue,
    onSecondary = Color.White,
    secondaryContainer = PixelBlueContainer,
    onSecondaryContainer = Color(0xFF0D47A1),
    tertiary = PixelBlue,
    onTertiary = Color.White,
    tertiaryContainer = PixelBlueContainer,
    onTertiaryContainer = Color(0xFF0D47A1),
    error = Color(0xFFB00020),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color.White,
    onErrorContainer = Color(0xFF410002),
    background = LightBackground,
    onBackground = Color(0xFF212121),
    surface = Color.White,
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFECEFF1),
    onSurfaceVariant = Color(0xFF616161),
    outline = Color(0xFF9E9E9E),
    outlineVariant = Color(0xFFD0D0D0),
    inverseSurface = PixelToolbar,
    inverseOnSurface = Color.White,
    inversePrimary = PixelBlueLight,
    scrim = Color.Black,
    surfaceTint = PixelBlue,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFAFAFA),
    surfaceContainer = Color(0xFFF5F5F5),
    surfaceContainerHigh = Color(0xFFEEEEEE),
    surfaceContainerHighest = Color(0xFFE0E0E0),
)

private val DarkColor = darkColorScheme(
    primary = PixelBlue,
    onPrimary = Color.White,
    primaryContainer = PixelBlueDarkContainer,
    onPrimaryContainer = Color.White,
    secondary = PixelBlue,
    onSecondary = Color.White,
    secondaryContainer = PixelBlueDarkContainer,
    onSecondaryContainer = Color.White,
    tertiary = PixelBlue,
    onTertiary = Color.White,
    tertiaryContainer = PixelBlueDarkContainer,
    onTertiaryContainer = Color.White,
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = DarkBackground,
    onBackground = Color(0xFFF5F5F5),
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF505050),
    onSurfaceVariant = Color(0xFFD0D0D0),
    outline = Color(0xFF9E9E9E),
    outlineVariant = Color(0xFF666666),
    inverseSurface = Color(0xFFF5F5F5),
    inverseOnSurface = Color(0xFF212121),
    inversePrimary = PixelBlueLight,
    scrim = Color.Black,
    surfaceTint = PixelBlue,
    surfaceContainerLowest = Color(0xFF212121),
    surfaceContainerLow = Color(0xFF292929),
    surfaceContainer = Color(0xFF303030),
    surfaceContainerHigh = Color(0xFF383838),
    surfaceContainerHighest = Color(0xFF424242),
)

private val PixelShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(2.dp),
    medium = RoundedCornerShape(2.dp),
    large = RoundedCornerShape(3.dp),
    extraLarge = RoundedCornerShape(4.dp),
)

// Keep ordinary text neutral; use blue mainly for controls and indicators.
val colorPing = PixelBlue
val colorPingRed = Color(0xFFFF5252)
val colorConfigType = Color(0xFF616161)
val colorFabActive = PixelBlue
val colorFabInactiveLight = Color(0xFF9E9E9E)
val colorFabInactiveDark = Color(0xFF616161)
val dividerColorLight = Color(0xFFE0E0E0)
val dividerColorDark = Color(0xFF555555)

// Toast colors 70%
val toastNormalBgLight = Color(0xB3353A3E)
val toastNormalBgDark = Color(0xB34A4F54)
val toastSuccessBg = Color(0xB3388E3C)
val toastErrorBg = Color(0xB3D50000)
val toastInfoBg = Color(0xB32196F3)
val toastIconCircleBg = Color(0x33FFFFFF)
val toastTextColor = Color.White

object ThemeManager {
    private val _themeMode = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
    )
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _dynamicColorEnabled = MutableStateFlow(
        MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, true)
    )
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled.asStateFlow()

    fun setThemeMode(mode: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_UI_MODE_NIGHT, mode)
        _themeMode.value = mode
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        MmkvManager.encodeSettings(AppConfig.PREF_DYNAMIC_COLOR, enabled)
        _dynamicColorEnabled.value = enabled
    }

    fun refresh() {
        _themeMode.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
        _dynamicColorEnabled.value =
            MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, true)
    }
}

@Composable
fun resolveDarkTheme(): Boolean {
    val mode by ThemeManager.themeMode.collectAsState()
    return when (mode) {
        "1" -> false
        "2" -> true
        else -> isSystemInDarkTheme()
    }
}

val LocalDarkTheme = compositionLocalOf { false }

@Composable
fun AppTheme(
    darkTheme: Boolean = resolveDarkTheme(),
    content: @Composable () -> Unit
) {
    // Fixed palette: Android 12+ wallpaper-based dynamic color cannot replace #2196F3.
    val colorScheme = if (darkTheme) DarkColor else LightColor
    val snackbarController = rememberAppSnackbarController()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalAppSnackbar provides snackbarController
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = PixelShapes
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AppSnackbarBridge(controller = snackbarController)
                content()
                AppSnackbarHost(hostState = snackbarController.hostState)
            }
        }
    }
}
