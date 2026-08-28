package com.v2ray.ang.ui.compose

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.ui.Modifier

/**
 * Kept as compatibility helpers for existing callers.
 *
 * The previous implementation observed scroll state on every movement, restarted
 * coroutines and alpha animations, and performed layout calculations during draw.
 * That work was especially visible on long server lists under Android 16.
 * Native Compose scrolling is deliberately left to handle rendering here.
 */
fun Modifier.verticalScrollbar(
    scrollState: ScrollState,
    config: ScrollbarConfig = ScrollbarConfig()
): Modifier = this

fun Modifier.horizontalScrollbar(
    scrollState: ScrollState,
    config: ScrollbarConfig = ScrollbarConfig()
): Modifier = this

fun Modifier.verticalScrollbar(
    lazyListState: LazyListState,
    config: ScrollbarConfig = ScrollbarConfig()
): Modifier = this

fun Modifier.verticalScrollbar(
    lazyGridState: LazyGridState,
    config: ScrollbarConfig = ScrollbarConfig()
): Modifier = this

data class ScrollbarConfig(
    val thickness: androidx.compose.ui.unit.Dp = 4.dp,
    val minThumbSize: androidx.compose.ui.unit.Dp = 24.dp,
    val thumbColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Unspecified,
    val trackColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color.Transparent,
    val padding: androidx.compose.ui.unit.Dp = 2.dp,
    val cornerRadius: androidx.compose.ui.unit.Dp = 2.dp,
    val fadeOutDurationMs: Int = 1500,
    val fadeAnimDurationMs: Int = 300,
)
