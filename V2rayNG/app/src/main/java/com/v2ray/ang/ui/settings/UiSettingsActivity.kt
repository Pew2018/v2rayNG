package com.v2ray.ang.ui.settings

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.v2ray.ang.R
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.ui.base.BaseComponentActivity
import com.v2ray.ang.ui.compose.AppTopBar
import com.v2ray.ang.ui.compose.SettingsEditItem
import com.v2ray.ang.ui.compose.SettingsListItem
import com.v2ray.ang.ui.compose.SettingsSwitchItem
import com.v2ray.ang.ui.compose.ThemeCustomization
import com.v2ray.ang.ui.compose.ThemeManager
import com.v2ray.ang.ui.compose.UiPreferences

class UiSettingsActivity : BaseComponentActivity() {
    @Composable
    override fun ScreenContent() = UiSettingsScreen { finish() }
}

@Composable
private fun UiSettingsScreen(onBackClick: () -> Unit) {
    val scroll = rememberScrollState()
    var style by remember { mutableStateOf(MmkvManager.decodeSettingsString(UiPreferences.PREF_UI_STYLE, UiPreferences.STYLE_MATERIAL3) ?: UiPreferences.STYLE_MATERIAL3) }
    var dynamic by remember { mutableStateOf(MmkvManager.decodeSettingsBool(UiPreferences.PREF_DYNAMIC_COLOR, true)) }
    var accent by remember { mutableStateOf(MmkvManager.decodeSettingsString(UiPreferences.PREF_ACCENT_COLOR, "") ?: "") }
    val entries = listOf(stringResource(R.string.interface_material3), stringResource(R.string.interface_legacy))
    val values = listOf(UiPreferences.STYLE_MATERIAL3, UiPreferences.STYLE_LEGACY)

    Scaffold(topBar = { AppTopBar(title = stringResource(R.string.title_interface_settings), onBackClick = onBackClick) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(scroll)) {
            SettingsListItem(title = stringResource(R.string.title_interface_settings), entries = entries, values = values, selectedValue = style, onSelected = {
                style = it
                MmkvManager.encodeSettings(UiPreferences.PREF_UI_STYLE, it)
            })
            SettingsSwitchItem(title = stringResource(R.string.interface_dynamic_color), summary = stringResource(R.string.interface_dynamic_color_summary), checked = dynamic, enabled = accent.isBlank() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S, onCheckedChange = {
                dynamic = it
                ThemeManager.setDynamicColorEnabled(it)
            })
            SettingsEditItem(title = stringResource(R.string.interface_custom_color), value = accent, onValueChanged = { value ->
                val normalized = ThemeCustomization.normalize(value)
                if (value.isBlank() || normalized != null) {
                    accent = normalized ?: ""
                    ThemeCustomization.setAccent(accent)
                    if (accent.isNotBlank()) dynamic = false
                }
            })
        }
    }
}
