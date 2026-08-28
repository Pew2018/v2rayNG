package com.v2ray.ang.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.v2ray.ang.AppConfig
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.service.V2RayServiceManager
import com.v2ray.ang.ui.components.AppTopBar
import com.v2ray.ang.ui.components.MainViewModel
import com.v2ray.ang.ui.components.ServerListItem
import com.v2ray.ang.ui.components.rememberAppSnackbarController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveMainScreen(
    viewModel: MainViewModel,
    onMenu: () -> Unit,
    onSearch: (String) -> Unit,
    onImport: () -> Unit,
    onAdd: () -> Unit,
    onAction: (MainAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val servers = viewModel.servers
    val running = V2RayServiceManager.isRunning()
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchExpanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val groupTitles = viewModel.groups.map { it.remarks }
    val tabTitles = if (groupTitles.isEmpty()) listOf("All") else groupTitles

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "v2rayNG",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(24.dp),
                )
                tabTitles.forEachIndexed { index, title ->
                    NavigationDrawerItem(
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(Icons.Filled.Menu, contentDescription = null) },
                        modifier = Modifier.padding(horizontal = 12.dp),
                    )
                }
            }
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                Column {
                    LargeTopAppBar(
                        title = {
                            Text(
                                text = if (selectedTab < tabTitles.size) tabTitles[selectedTab] else "Servers"
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { searchExpanded = true }) {
                                Icon(Icons.Filled.Search, contentDescription = "Search")
                            }
                            IconButton(onClick = { onAction(MainAction.ShowMoreMenu) }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "More")
                            }
                        },
                    )
                    TabRow(selectedTabIndex = selectedTab) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) },
                            )
                        }
                    }
                    if (searchExpanded) {
                        SearchBar(
                            query = searchText,
                            onQueryChange = {
                                searchText = it
                                onSearch(it)
                            },
                            onSearch = { onSearch(it) },
                            active = true,
                            onActiveChange = { active ->
                                if (!active) {
                                    searchExpanded = false
                                    searchText = ""
                                    onSearch("")
                                }
                            },
                            placeholder = { Text("Search servers") },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {}
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAdd) {
                    Icon(Icons.Filled.Add, contentDescription = "Add server")
                }
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ConnectionStatusCard(
                    running = running,
                    onTest = { onAction(MainAction.TestCurrentServer) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )

                if (servers.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        FilledTonalButton(onClick = onImport) {
                            Text("Import from clipboard")
                        }
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
                        items(
                            items = servers,
                            key = { it.guid },
                            contentType = { "server" },
                        ) { server ->
                            ExpressiveServerCard(
                                server = server,
                                selected = server.guid == viewModel.selectedGuid,
                                onClick = { onAction(MainAction.SelectServer(server.guid)) },
                                onEdit = { onAction(MainAction.EditServer(server.guid, server.profile)) },
                                onShare = { onAction(MainAction.ShareQRCode(server.guid)) },
                                onDuplicate = { onAction(MainAction.DuplicateServer(server.guid)) },
                                onDelete = { onAction(MainAction.DeleteServer(server.guid)) },
                                onConnect = { onAction(MainAction.ConnectServer(server.guid)) },
                            )
                        }
                    }
                }
            }
        }
    }

    if (searchExpanded) {
        // Keep the search UI in the Expressive surface; this is only a state holder so
        // the SearchBar can remain inline with the app bar without introducing an
        // additional custom dialog layer.
    }
}

@Composable
private fun ConnectionStatusCard(
    running: Boolean,
    onTest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (running) "Connected" else "Disconnected",
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (running) "VPN service is active" else "VPN service is stopped",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            FilledTonalButton(onClick = onTest) { Text("Test") }
        }
    }
}

@Composable
private fun ExpressiveServerCard(
    server: com.v2ray.ang.dto.ServerAffiliationBean,
    selected: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onConnect: () -> Unit,
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest
    }

    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = server.remarks,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = server.profileType,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onShare) {
                    Icon(Icons.Filled.QrCode, contentDescription = "QR code")
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(onClick = onConnect) {
                    Text(if (selected) "Connect" else "Select")
                }
                IconButton(onClick = onDuplicate) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = "Duplicate")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
