package com.v2ray.ang.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.v2ray.ang.R
import com.v2ray.ang.dto.entities.ServersCache
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpressiveMainScreen(
    mainViewModel: MainViewModel,
    onAction: (MainAction) -> Unit,
    onNavigate: (MainDestination) -> Unit,
) {
    val state by mainViewModel.uiState.collectAsStateWithLifecycle()
    val loading by mainViewModel.isLoading.collectAsStateWithLifecycle()
    val groups = state.groups
    val selectedGroupId = state.selectedGroupId
    val selectedGuid = state.selectedGuid
    val running = state.isRunning
    val scope = rememberCoroutineScope()
    val drawer = rememberDrawerState(DrawerValue.Closed)
    val snackbar = remember { SnackbarHostState() }
    var searching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var tab by remember { mutableIntStateOf(0) }
    val servers by mainViewModel.serversForGroup(selectedGroupId).collectAsStateWithLifecycle()

    LaunchedEffect(groups, selectedGroupId) {
        val i = groups.indexOfFirst { it.id == selectedGroupId }
        if (i >= 0) tab = i
    }

    ModalNavigationDrawer(
        drawerState = drawer,
        drawerContent = {
            Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).padding(12.dp)) {
                Spacer(Modifier.height(24.dp))
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(20.dp, 12.dp))
                Spacer(Modifier.height(8.dp))
                MainDestination.entries.forEach { destination ->
                    NavigationDrawerItem(
                        label = { Text(stringResource(destination.labelRes)) },
                        selected = false,
                        onClick = { scope.launch { drawer.close() }; onNavigate(destination) },
                        colors = NavigationDrawerItemDefaults.colors(),
                        modifier = Modifier.padding(vertical = 2.dp),
                    )
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                if (searching) {
                    SearchBar(
                        query = query,
                        onQueryChange = { query = it; onAction(MainAction.Search(it)) },
                        onSearch = { onAction(MainAction.Search(query)) },
                        active = true,
                        onActiveChange = { active -> if (!active) { searching = false; query = ""; onAction(MainAction.Search("")) } },
                        placeholder = { Text("Search servers") },
                        leadingIcon = {
                            IconButton(onClick = { searching = false; query = ""; onAction(MainAction.Search("")) }) {
                                Icon(painterResource(R.drawable.ic_arrow_back_24dp), contentDescription = "Back")
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {}
                } else {
                    LargeTopAppBar(
                        title = {
                            Column {
                                Text(stringResource(R.string.title_server), style = MaterialTheme.typography.headlineSmall)
                                AnimatedVisibility(loading) { Text("Updating…", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary) }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawer.open() } }) { Icon(painterResource(R.drawable.ic_menu_24dp), contentDescription = "Menu") }
                        },
                        actions = {
                            IconButton(onClick = { searching = true }) { Icon(painterResource(R.drawable.ic_search_24dp), contentDescription = "Search") }
                            IconButton(onClick = { onAction(MainAction.ImportClipboard) }) { Icon(painterResource(R.drawable.ic_add_24dp), contentDescription = "Import") }
                            IconButton(onClick = { onAction(MainAction.UpdateSubscriptions) }) { Icon(painterResource(R.drawable.ic_more_vert_24dp), contentDescription = "Refresh") }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbar) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onAction(MainAction.ToggleService) },
                    shape = MaterialTheme.shapes.extraLarge,
                    containerColor = if (running) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary,
                    contentColor = if (running) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.navigationBarsPadding(),
                ) {
                    Icon(painterResource(if (running) R.drawable.ic_stop_24dp else R.drawable.ic_play_24dp), contentDescription = if (running) "Stop" else "Start")
                }
            },
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                if (groups.size > 1) {
                    TabRow(
                        selectedTabIndex = tab.coerceIn(0, groups.lastIndex),
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ) {
                        groups.forEachIndexed { i, group ->
                            Tab(selected = tab == i, onClick = { tab = i; onAction(MainAction.SelectGroup(group.id)) }, text = { Text(group.remarks, maxLines = 1, overflow = TextOverflow.Ellipsis) })
                        }
                    }
                }

                ElevatedCard(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Row(Modifier.padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(if (running) "Connected" else "Disconnected", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(4.dp))
                            Text(state.status.toString(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        FilledTonalButton(onClick = { onAction(MainAction.TestCurrentServer) }) { Text("Test") }
                    }
                }

                if (servers.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        FilledTonalButton(onClick = { onAction(MainAction.ImportClipboard) }) { Text("Import from clipboard") }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 4.dp,
                            end = 16.dp,
                            bottom = 96.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(servers, key = { it.guid }) { server ->
                            ExpressiveServerCard(
                                server = server,
                                selected = server.guid == selectedGuid,
                                onClick = { onAction(MainAction.SelectServer(server.guid)) },
                                onEdit = { onAction(MainAction.EditServer(server.guid, server.profile)) },
                                onShare = { onAction(MainAction.ShareQRCode(server.guid)) },
                                onDelete = { onAction(MainAction.RemoveServer(server.guid)) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpressiveServerCard(
    server: ServersCache,
    selected: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
) {
    val container by animateColorAsState(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer, label = "server-card")
    val primaryText = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    val secondaryText = if (selected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.76f) else MaterialTheme.colorScheme.onSurfaceVariant
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(containerColor = container),
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(46.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = if (selected) .22f else .12f)), contentAlignment = Alignment.Center) {
                Text(server.profile.remarks.take(1).uppercase(), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(server.profile.remarks, style = MaterialTheme.typography.titleMedium, color = primaryText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Text(server.profile.description?.ifBlank { server.profile.configType.name } ?: server.profile.configType.name, style = MaterialTheme.typography.bodyMedium, color = secondaryText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(6.dp))
                Text(server.profile.configType.name, style = MaterialTheme.typography.labelMedium, color = secondaryText)
            }
            IconButton(onClick = onEdit) { Icon(painterResource(R.drawable.ic_edit_24dp), contentDescription = "Edit") }
            IconButton(onClick = onShare) { Icon(painterResource(R.drawable.ic_share_24dp), contentDescription = "Share") }
            IconButton(onClick = onDelete) { Icon(painterResource(R.drawable.ic_delete_24dp), contentDescription = "Delete") }
        }
    }
}
