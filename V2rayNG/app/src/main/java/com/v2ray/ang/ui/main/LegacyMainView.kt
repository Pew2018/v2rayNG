package com.v2ray.ang.ui.main

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.v2ray.ang.R
import com.v2ray.ang.dto.entities.ServersCache

private const val BLUE = 0xFF2196F3.toInt()
private const val TOOLBAR = 0xFF333333.toInt()
private const val LIGHT_BG = 0xFFFAFAFA.toInt()
private const val DARK_BG = 0xFF303030.toInt()
private const val LIGHT_SURFACE = Color.WHITE
private const val DARK_SURFACE = 0xFF424242.toInt()
private const val DARK_TEXT = Color.WHITE
private const val LIGHT_TEXT = 0xFF212121.toInt()
private const val DARK_SECONDARY = 0xFFD0D0D0.toInt()
private const val LIGHT_SECONDARY = 0xFF757575.toInt()

class LegacyMainView(context: Context, private val onAction: (MainAction) -> Unit, private val onNavigate: (MainDestination) -> Unit) : DrawerLayout(context) {
    private val toolbar = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setBackgroundColor(TOOLBAR) }
    private val title = TextView(context).apply { text = context.getString(R.string.title_server); textSize = 20f; setTextColor(Color.WHITE); gravity = Gravity.CENTER_VERTICAL }
    private val tabScroll = HorizontalScrollView(context).apply { isHorizontalScrollBarEnabled = false }
    private val tabRow = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL }
    private val list = ListView(context).apply { divider = null; dividerHeight = 0; overScrollMode = View.OVER_SCROLL_IF_CONTENT_SCROLLS }
    private val status = TextView(context).apply { textSize = 14f; gravity = Gravity.CENTER_VERTICAL; setPadding(dp(16), 0, dp(16), 0) }
    private val adapter = LegacyServerAdapter(context, onAction)
    private val drawer = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
    private val fab = AppCompatImageButton(context).apply {
        scaleType = ImageButton.ScaleType.CENTER; setPadding(dp(16), dp(16), dp(16), dp(16)); setColorFilter(Color.WHITE); elevation = dp(6).toFloat()
        background = GradientDrawable().apply { shape = GradientDrawable.OVAL }
    }

    init {
        setScrimColor(0x99000000.toInt())
        val main = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(LIGHT_BG) }
        addView(main, LayoutParams(-1, -1))
        addView(drawer, LayoutParams(dp(300), -1).apply { gravity = Gravity.START })
        toolbar.addView(button(R.drawable.ic_menu_24dp) { openDrawer(GravityCompat.START) }, LinearLayout.LayoutParams(dp(48), dp(56)))
        toolbar.addView(title, LinearLayout.LayoutParams(0, dp(56), 1f))
        toolbar.addView(button(R.drawable.ic_search_24dp) { showSearch() }, LinearLayout.LayoutParams(dp(48), dp(56)))
        toolbar.addView(button(R.drawable.ic_add_24dp) { showImportMenu(it) }, LinearLayout.LayoutParams(dp(48), dp(56)))
        toolbar.addView(button(R.drawable.ic_more_vert_24dp) { showMoreMenu(it) }, LinearLayout.LayoutParams(dp(48), dp(56)))
        main.addView(toolbar)
        tabScroll.addView(tabRow, ViewGroup.LayoutParams(-2, dp(48)))
        main.addView(tabScroll)
        main.addView(list, LinearLayout.LayoutParams(-1, 0, 1f))
        val bottom = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(LIGHT_SURFACE) }
        bottom.addView(status, LinearLayout.LayoutParams(-1, dp(64)))
        main.addView(bottom, LinearLayout.LayoutParams(-1, dp(64)))
        addView(fab, LayoutParams(dp(56), dp(56)).apply { gravity = Gravity.END or Gravity.BOTTOM; rightMargin = dp(20); bottomMargin = dp(44) })
        list.adapter = adapter
        fab.setOnClickListener { onAction(MainAction.ToggleService) }
        buildDrawer()
    }

    fun render(state: MainUiState, servers: List<ServersCache>, dark: Boolean) {
        val bg = if (dark) DARK_BG else LIGHT_BG; val surface = if (dark) DARK_SURFACE else LIGHT_SURFACE; val secondary = if (dark) DARK_SECONDARY else LIGHT_SECONDARY
        setBackgroundColor(bg); tabScroll.setBackgroundColor(surface); status.setBackgroundColor(surface); status.setTextColor(secondary)
        status.text = when (state.status) {
            MainStatus.Disconnected -> context.getString(R.string.connection_not_connected)
            MainStatus.Connected -> context.getString(R.string.connection_connected)
            MainStatus.Testing -> context.getString(R.string.connection_test_testing)
            is MainStatus.TestProgress -> state.status.progress
            is MainStatus.ConnectionTest -> if (state.status.result.delayMillis >= 0) context.getString(R.string.server_test_delay_value, state.status.result.delayMillis) else context.getString(R.string.connection_test_empty_message)
        }
        fab.setBackgroundColor(if (state.isRunning) BLUE else if (dark) 0xFF616161.toInt() else 0xFF9E9E9E.toInt())
        fab.setImageResource(if (state.isRunning) R.drawable.ic_stop_24dp else R.drawable.ic_play_24dp)
        tabRow.removeAllViews()
        state.groups.forEach { group -> tabRow.addView(TextView(context).apply { text = group.remarks; textSize = 14f; gravity = Gravity.CENTER; setPadding(dp(18), 0, dp(18), 0); setTextColor(if (group.id == state.selectedGroupId) BLUE else secondary); setOnClickListener { onAction(MainAction.SelectGroup(group.id)) } }, LinearLayout.LayoutParams(-2, dp(48))) }
        adapter.setSelectedGuid(state.selectedGuid); adapter.dark = dark; adapter.submitList(servers)
        drawer.setBackgroundColor(surface); for (i in 1 until drawer.childCount) (drawer.getChildAt(i) as? TextView)?.setTextColor(if (dark) DARK_TEXT else LIGHT_TEXT)
    }

    private fun buildDrawer() {
        drawer.addView(TextView(context).apply { text = context.getString(R.string.app_name); textSize = 22f; gravity = Gravity.CENTER_VERTICAL; setTextColor(Color.WHITE); setPadding(dp(16), 0, dp(16), 0); setBackgroundColor(TOOLBAR) }, LinearLayout.LayoutParams(-1, dp(112)))
        MainDestination.entries.forEach { item -> drawer.addView(TextView(context).apply { text = context.getString(item.labelRes); textSize = 16f; gravity = Gravity.CENTER_VERTICAL; setPadding(dp(16), 0, dp(16), 0); setOnClickListener { closeDrawer(GravityCompat.START); onNavigate(item) } }, LinearLayout.LayoutParams(-1, dp(48))) }
    }

    private fun showSearch() {
        val input = EditText(context).apply { hint = context.getString(R.string.menu_item_search); singleLine = true }
        android.app.AlertDialog.Builder(context).setTitle(context.getString(R.string.menu_item_search)).setView(input).setPositiveButton(context.getString(R.string.action_ok)) { _, _ -> onAction(MainAction.Search(input.text.toString())) }.setNegativeButton(context.getString(R.string.action_cancel), null).show()
    }
    private fun showImportMenu(anchor: View) = PopupMenu(context, anchor).apply {
        menu.add("Import QR").setOnMenuItemClickListener { onAction(MainAction.ImportQRcode); true }
        menu.add("Import clipboard").setOnMenuItemClickListener { onAction(MainAction.ImportClipboard); true }
        menu.add("Import file").setOnMenuItemClickListener { onAction(MainAction.ImportConfigLocal); true }
        menu.add("Export all").setOnMenuItemClickListener { onAction(MainAction.ExportAll); true }
        show()
    }
    private fun showMoreMenu(anchor: View) = PopupMenu(context, anchor).apply {
        menu.add("Restart service").setOnMenuItemClickListener { onAction(MainAction.RestartService); true }
        menu.add("Test current").setOnMenuItemClickListener { onAction(MainAction.TestCurrentServer); true }
        menu.add("Test all").setOnMenuItemClickListener { onAction(MainAction.TestAllServers); true }
        menu.add("Real ping all").setOnMenuItemClickListener { onAction(MainAction.TestRealAllServers); true }
        menu.add("Update subscriptions").setOnMenuItemClickListener { onAction(MainAction.UpdateSubscriptions); true }
        menu.add("Delete all").setOnMenuItemClickListener { onAction(MainAction.RemoveAllServers); true }
        show()
    }
    private fun button(res: Int, action: (View) -> Unit) = AppCompatImageButton(context).apply { setImageResource(res); setColorFilter(Color.WHITE); setBackgroundColor(Color.TRANSPARENT); setPadding(dp(12), dp(12), dp(12), dp(12)); setOnClickListener(action) }
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
