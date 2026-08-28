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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.v2ray.ang.R
import com.v2ray.ang.dto.entities.ServersCache
import com.v2ray.ang.ui.compose.LocalDarkTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpressiveMainScreen(
    mainViewModel: MainViewModel,
    onAction: (MainAction) -> Unit,
    onNavigate: (MainDestination) -> Unit,
) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by mainViewModel.isLoading.collectAsStateWithLifecycle()
    val groups = uiState.groups
    val selectedGroupId = uiState.selectedGroupId
    val selectedGuid = uiState.selectedGuid
    val isRunning = uiState.isRunning
    val dark = LocalDarkTheme.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    var searchOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(groups, selectedGroupId) {
        val index = groups.indexOfFirst { it.id == selectedGroupId }
        if (index >= 0) selectedTab = index
    }

    val serversFlow = selectedGroupId.takeIf { it.isNotBlank() }?.let { mainViewModel.serversForGroup(it) }
    val servers by (serversFlow?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(emptyList()) })

    fun selectGroup(index: Int) {
        val group = groups.getOrNull(index) ?: return
        selectedTab = index
        onAction(MainAction.SelectGroup(group.id))
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp)
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "v2rayNG",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(20.dp, 12.dp)
                )
                Spacer(Modifier.height(8.dp))
                MainDestination.entries.forEach { destination ->
                    NavigationDrawerItem(
                        label = { Text(destination.name) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            onNavigate(destination)
                        },
                        colors = NavigationDrawerItemDefaults.colors(),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (searchOpen) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            onAction(MainAction.Search(it))
                        },
                        onSearch = { onAction(MainAction.Search(searchQuery)) },
                        active = true,
                        onActiveChange = { active ->
                            if (!active) {
                                searchOpen = false
                                searchQuery = ""
                                onAction(MainAction.Search(""))
                            }
                        },
                        placeholder = { Text("Search servers") },
                        leadingIcon = {
                            IconButton(onClick = { searchOpen = false }) {
                                Icon(painterResource(R.drawable.ic_arrow_back_24dp), contentDescription = "Back")
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {}
                } else {
                    LargeTopAppBar(
                        title = {
                            Column {
                                Text(
                                    text = "Servers",
                                    style = MaterialTheme.typography.headlineSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                AnimatedVisibility(visible = isLoading) {
                                    Text(
                                        text = "Updating…",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(painterResource(R.drawable.ic_menu_24dp), contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { searchOpen = true }) {
                                Icon(painterResource(R.drawable.ic_search_24dp), contentDescription = "Search")
                            }
                            IconButton(onClick = { onAction(MainAction.ImportManually(0)) }) {
                                Icon(painterResource(R.drawable.ic_add_24dp), contentDescription = "Add")
                            }
                            IconButton(onClick = { onAction(MainAction.UpdateSubscriptions) }) {
                                Icon(painterResource(R.drawable.ic_refresh_24dp), contentDescription = "Refresh")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                        )
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (isRunning) onAction(MainAction.ToggleService) else onAction(MainAction.ToggleService)
                    },
                    shape = MaterialTheme.shapes.extraLarge,
                    containerColor = if (isRunning) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary,
                    contentColor = if (isRunning) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Icon(
                        painterResource(if (isRunning) R.drawable.ic_stop_24dp else R.drawable.ic_play_24dp),
                        contentDescription = if (isRunning) "Stop" else "Start",
                    )
                }
            }
        ) { innerPadding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (groups.size > 1) {
                    TabRow(
                        selectedTabIndex = selectedTab.coerceIn(0, groups.lastIndex),
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ) {
                        groups.forEachIndexed { index, group ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectGroup(index) },
                                text = { Text(group.remarks, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            )
                        }
                    }
                }

                ElevatedCard(
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = if (isRunning) "Connected" else "Disconnected",
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = mainViewModel.formatStatus(uiState.status),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        FilledTonalButton(onClick = { onAction(MainAction.TestCurrentServer) }) {
                            Text("Test")
                        }
                    }
                }

                if (servers.isEmpty()) {
                    ExpressiveEmptyState(onImport = { onAction(MainAction.ImportClipboard) })
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(servers, key = { it.guid }) { server ->
                            ExpressiveServerCard(
                                server = server,
                                selected = server.guid == selectedGuid,
                                dark = dark,
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
private fun ExpressiveEmptyState(onImport: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        ElevatedCard(
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(28.dp),
            ) {
                Text("No servers", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Import a server or subscription to get started.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(18.dp))
                FilledTonalButton(onClick = onImport) { Text("Import from clipboard") }
            }
        }
    }
}

@Composable
private fun ExpressiveServerCard(
    server: ServersCache,
    selected: Boolean,
    dark: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
) {
    val container by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
        label = "server-card",
    )
    val onContainer = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    val secondary = if (selected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.76f) else MaterialTheme.colorScheme.onSurfaceVariant

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(containerColor = container),
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = if (selected) 0.22f else 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = server.profile.remarks.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    server.profile.remarks,
                    style = MaterialTheme.typography.titleMedium,
                    color = onContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    server.profile.description.ifBlank { server.profile.configType.name },
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(7.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(server.profile.configType.name, style = MaterialTheme.typography.labelMedium, color = secondary)
                    Spacer(Modifier.width(10.dp))
                    val ping = server.testDelayMillis
                    if (ping >= 0L) {
                        Text("${ping} ms", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    } else if (ping < 0L) {
                        Text("Unavailable", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
            IconButton(onClick = onEdit) {
                Icon(painterResource(R.drawable.ic_edit_24dp), contentDescription = "Edit")
            }
            IconButton(onClick = onShare) {
                Icon(painterResource(R.drawable.ic_share_24dp), contentDescription = "Share")
            }
            IconButton(onClick = onDelete) {
                Icon(painterResource(R.drawable.ic_delete_24dp), contentDescription = "Delete")
            }
        }
    }
}
