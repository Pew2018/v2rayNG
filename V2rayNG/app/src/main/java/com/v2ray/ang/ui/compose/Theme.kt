package com.v2ray.ang.ui.compose

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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

private val LightColor = lightColorScheme(
    primary = PixelBlue,
    onPrimary = Color.White,
    primaryContainer = PixelBlueContainer,
    onPrimaryContainer = PixelBlueDarkContainer,
    secondary = PixelBlue,
    onSecondary = Color.White,
    secondaryContainer = PixelBlueContainer,
    onSecondaryContainer = PixelBlueDarkContainer,
    tertiary = PixelBlue,
    onTertiary = Color.White,
    tertiaryContainer = PixelBlueContainer,
    onTertiaryContainer = PixelBlueDarkContainer,
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color.White,
    onErrorContainer = Color(0xFF410002),
    background = Color.White,
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = PixelBlueLight,
    scrim = Color.Black,
    surfaceTint = PixelBlue,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF7F7F7),
    surfaceContainer = Color(0xFFF1F1F1),
    surfaceContainerHigh = Color(0xFFEBEBEB),
    surfaceContainerHighest = Color(0xFFE5E5E5),
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
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    inverseSurface = Color(0xFFE6E1E5),
    inverseOnSurface = Color(0xFF1C1B1F),
    inversePrimary = PixelBlueLight,
    scrim = Color.Black,
    surfaceTint = PixelBlue,
    surfaceContainerLowest = Color(0xFF0F0F12),
    surfaceContainerLow = Color(0xFF1A191D),
    surfaceContainer = Color(0xFF1E1D21),
    surfaceContainerHigh = Color(0xFF282729),
    surfaceContainerHighest = Color(0xFF333234),
)

// Semantic colors
val colorPing = PixelBlue
val colorPingRed = Color(0xFFFF0099)
val colorConfigType = PixelBlue
val colorFabActive = PixelBlue
val colorFabInactiveLight = Color(0xFF9C9C9C)
val colorFabInactiveDark = Color(0xFF646464)
val dividerColorLight = Color(0xFFE0E0E0)
val dividerColorDark = Color(0xFF424242)

// Toast colors 70%
val toastNormalBgLight = Color(0xB3353A3E)
val toastNormalBgDark = Color(0xB34A4F54)
val toastSuccessBg = Color(0xB3388E3C)
val toastErrorBg = Color(0xB3D50000)
val toastInfoBg = Color(0xB33F51B5)
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
    // Fixed Pixel-style palette: Android 12+ dynamic colors are intentionally disabled
    // so the app consistently uses #2196F3 as its accent color.
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
            colorScheme = colorScheme
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AppSnackbarBridge(controller = snackbarController)
                content()
                AppSnackbarHost(hostState = snackbarController.hostState)
            }
        }
    }
}
