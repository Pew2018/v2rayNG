package com.v2ray.ang.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.v2ray.ang.R
import com.v2ray.ang.ui.compose.AppDivider
import com.v2ray.ang.ui.compose.LocalDarkTheme

enum class MainDestination(@DrawableRes val iconRes: Int, @StringRes val labelRes: Int) {
    Subscriptions(R.drawable.ic_subscriptions_24dp, R.string.title_sub_setting),
    PerAppProxy(R.drawable.ic_per_apps_24dp, R.string.per_app_proxy_settings),
    Routing(R.drawable.ic_routing_24dp, R.string.routing_settings_title),
    UserAssets(R.drawable.ic_file_24dp, R.string.title_user_asset_setting),
    InterfaceSettings(R.drawable.ic_settings_24dp, R.string.title_interface_settings),
    Settings(R.drawable.ic_settings_24dp, R.string.title_settings),
    Promotion(R.drawable.ic_promotion_24dp, R.string.title_pref_promotion),
    Logcat(R.drawable.ic_logcat_24dp, R.string.title_logcat),
    CheckUpdate(R.drawable.ic_check_update_24dp, R.string.update_check_for_update),
    BackupRestore(R.drawable.ic_restore_24dp, R.string.title_configuration_backup_restore),
    About(R.drawable.ic_about_24dp, R.string.title_about)
}

private val primaryDrawerItems = listOf(
    MainDestination.Subscriptions,
    MainDestination.PerAppProxy,
    MainDestination.Routing,
    MainDestination.UserAssets,
    MainDestination.InterfaceSettings,
    MainDestination.Settings,
)

private val drawerItems = primaryDrawerItems + listOf(
    MainDestination.Promotion,
    MainDestination.Logcat,
    MainDestination.CheckUpdate,
    MainDestination.BackupRestore,
    MainDestination.About,
)

@Composable
fun MainDrawerContent(drawerState: DrawerState, onNavigate: (MainDestination) -> Unit) {
    val isDarkTheme = LocalDarkTheme.current
    val background = if (isDarkTheme) Color(0xFF303030) else Color.White
    val primaryText = if (isDarkTheme) Color.White else Color(0xFF212121)
    val iconColor = if (isDarkTheme) Color(0xFFBDBDBD) else Color(0xFF616161)
    val squareShape = RoundedCornerShape(0.dp)

    ModalDrawerSheet(
        drawerState,
        modifier = Modifier.fillMaxWidth(0.78f),
        drawerShape = squareShape,
        drawerContainerColor = background,
        drawerContentColor = primaryText,
        drawerTonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.fillMaxSize().background(background)) {
            Surface(
                modifier = Modifier.fillMaxWidth().height(152.dp),
                color = Color(0xFF333333),
                shape = squareShape,
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(start = 20.dp, end = 20.dp, bottom = 18.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall, color = Color.White)
                    Text(stringResource(R.string.title_server), style = MaterialTheme.typography.bodyMedium, color = Color(0xFFBDBDBD))
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                overscrollEffect = null,
            ) {
                items(items = drawerItems, key = { it.name }) { item ->
                    if (item == drawerItems[primaryDrawerItems.size]) {
                        AppDivider()
                    }
                    LegacyDrawerItem(
                        iconRes = item.iconRes,
                        labelRes = item.labelRes,
                        primaryText = primaryText,
                        iconColor = iconColor,
                        onClick = { onNavigate(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun LegacyDrawerItem(
    @DrawableRes iconRes: Int,
    @StringRes labelRes: Int,
    primaryText: Color,
    iconColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = iconColor,
        )
        Spacer(Modifier.size(28.dp))
        Text(
            stringResource(labelRes),
            color = primaryText,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
