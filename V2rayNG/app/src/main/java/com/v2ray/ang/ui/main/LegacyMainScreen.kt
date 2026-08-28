package com.v2ray.ang.ui.main

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.v2ray.ang.ui.compose.LocalDarkTheme

@Composable
fun LegacyMainScreen(
    mainViewModel: MainViewModel,
    onAction: (MainAction) -> Unit,
    onNavigate: (MainDestination) -> Unit,
) {
    val state by mainViewModel.uiState.collectAsStateWithLifecycle()
    val servers by mainViewModel.serversForGroup(state.selectedGroupId).collectAsStateWithLifecycle()
    val dark = LocalDarkTheme.current
    val context = LocalContext.current

    BackHandler { (context as? Activity)?.moveTaskToBack(false) }

    AndroidView(
        factory = { viewContext -> LegacyMainView(viewContext, onAction, onNavigate) },
        modifier = Modifier.fillMaxSize(),
        update = { view -> view.render(state, servers, dark) },
    )
}
