package com.v2ray.ang.ui.compose

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Kept as compatibility helpers for existing callers.
 *
 * The previous implementation observed scroll state on every movement, restarted
 * coroutines and alpha animations, and performed layout calculations during draw.
 * That work was especially visible on long server lists under Android 16.
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
    val thickness: Dp = 4.dp,
    val minThumbSize: Dp = 24.dp,
    val thumbColor: Color = Color.Unspecified,
    val trackColor: Color = Color.Transparent,
    val padding: Dp = 2.dp,
    val cornerRadius: Dp = 2.dp,
    val fadeOutDurationMs: Int = 1500,
    val fadeAnimDurationMs: Int = 300,
)
