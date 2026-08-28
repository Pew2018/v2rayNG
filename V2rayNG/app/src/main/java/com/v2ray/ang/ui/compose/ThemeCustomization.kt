package com.v2ray.ang.ui.compose

import com.v2ray.ang.handler.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ThemeCustomization {
    private val _customAccent = MutableStateFlow(
        MmkvManager.decodeSettingsString(UiPreferences.PREF_ACCENT_COLOR, "") ?: ""
    )
    val customAccent: StateFlow<String> = _customAccent.asStateFlow()

    fun setAccent(value: String) {
        val normalized = normalize(value) ?: if (value.isBlank()) "" else return
        MmkvManager.encodeSettings(UiPreferences.PREF_ACCENT_COLOR, normalized)
        _customAccent.value = normalized
        if (normalized.isNotBlank()) {
            MmkvManager.encodeSettings(UiPreferences.PREF_DYNAMIC_COLOR, false)
        }
    }

    fun refresh() {
        _customAccent.value = MmkvManager.decodeSettingsString(UiPreferences.PREF_ACCENT_COLOR, "") ?: ""
    }

    fun normalize(value: String): String? {
        if (value.isBlank()) return ""
        val candidate = if (value.startsWith("#")) value else "#$value"
        return if (candidate.matches(Regex("#[0-9A-Fa-f]{6}([0-9A-Fa-f]{2})?"))) candidate.uppercase() else null
    }
}
