package com.v2ray.ang.ui.main

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import com.v2ray.ang.R
import com.v2ray.ang.dto.entities.ServersCache
import com.v2ray.ang.extension.nullIfBlank
import com.v2ray.ang.handler.AngConfigManager

private const val BLUE = 0xFF2196F3.toInt()
private const val DARK_SURFACE = 0xFF424242.toInt()
private const val LIGHT_SURFACE = Color.WHITE
private const val DARK_TEXT = Color.WHITE
private const val LIGHT_TEXT = 0xFF212121.toInt()
private const val DARK_SECONDARY = 0xFFD0D0D0.toInt()
private const val LIGHT_SECONDARY = 0xFF757575.toInt()

class LegacyServerAdapter(private val context: Context, private val onAction: (MainAction) -> Unit) : BaseAdapter() {
    private var items: List<ServersCache> = emptyList()
    private var selectedGuid: String? = null
    var dark = false
    fun submitList(value: List<ServersCache>) { if (items !== value) { items = value; notifyDataSetChanged() } }
    fun setSelectedGuid(guid: String?) { if (selectedGuid != guid) { selectedGuid = guid; notifyDataSetChanged() } }
    override fun getCount() = items.size
    override fun getItem(position: Int) = items[position]
    override fun getItemId(position: Int) = position.toLong()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder = if (convertView?.tag is Holder) convertView.tag as Holder else Holder(parent.context).also { it.view.tag = it }
        holder.bind(getItem(position), selectedGuid, dark)
        return holder.view
    }

    private inner class Holder(ctx: Context) {
        val view = createRow(ctx)
        private val name = view.findViewById<TextView>(1001)
        private val detail = view.findViewById<TextView>(1002)
        private val type = view.findViewById<TextView>(1003)
        private val ping = view.findViewById<TextView>(1004)
        private val menu = view.findViewById<ImageButton>(1005)
        private val strip = view.findViewById<View>(1006)
        fun bind(item: ServersCache, selected: String?, isDark: Boolean) {
            view.setBackgroundColor(if (isDark) DARK_SURFACE else LIGHT_SURFACE)
            name.setTextColor(if (isDark) DARK_TEXT else LIGHT_TEXT)
            detail.setTextColor(if (isDark) DARK_SECONDARY else LIGHT_SECONDARY)
            type.setTextColor(if (isDark) 0xFF90CAF9.toInt() else LIGHT_SECONDARY)
            ping.setTextColor(if (item.testDelayMillis < 0) 0xFFFF5252.toInt() else BLUE)
            menu.setColorFilter(if (isDark) Color.WHITE else 0xFF424242.toInt())
            strip.setBackgroundColor(if (item.guid == selected) BLUE else Color.TRANSPARENT)
            name.text = item.profile.remarks
            detail.text = item.profile.description.nullIfBlank() ?: AngConfigManager.generateDescription(item.profile)
            type.text = item.profile.configType.name
            ping.text = if (item.testDelayMillis == 0L) "" else context.getString(R.string.server_test_delay_value, item.testDelayMillis)
            view.setOnClickListener { onAction(MainAction.SelectServer(item.guid)) }
            menu.setOnClickListener { showMenu(menu, item) }
        }
        private fun showMenu(anchor: View, item: ServersCache) {
            PopupMenu(context, anchor).apply {
                menu.add("Edit").setOnMenuItemClickListener { onAction(MainAction.EditServer(item.guid, item.profile)); true }
                menu.add("Share QR").setOnMenuItemClickListener { onAction(MainAction.ShareQRCode(item.guid)); true }
                menu.add("Copy").setOnMenuItemClickListener { onAction(MainAction.ShareClipboard(item.guid)); true }
                menu.add("Delete").setOnMenuItemClickListener { onAction(MainAction.RemoveServer(item.guid)); true }
                show()
            }
        }
    }

    companion object {
        private fun createRow(context: Context): View {
            val dp = { v: Int -> (v * context.resources.displayMetrics.density).toInt() }
            val root = FrameLayout(context)
            root.addView(View(context).apply { id = 1006 }, FrameLayout.LayoutParams(dp(4), -1))
            val body = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(12), dp(8), dp(4), dp(8)) }
            val first = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
            first.addView(TextView(context).apply { id = 1001; textSize = 16f; maxLines = 1; ellipsize = android.text.TextUtils.TruncateAt.END }, LinearLayout.LayoutParams(0, -2, 1f))
            first.addView(AppCompatImageButton(context).apply { id = 1005; setImageResource(R.drawable.ic_more_vert_24dp); setBackgroundColor(Color.TRANSPARENT); setPadding(dp(8), dp(8), dp(8), dp(8)) }, LinearLayout.LayoutParams(dp(44), dp(44)))
            body.addView(first)
            body.addView(TextView(context).apply { id = 1002; textSize = 14f; maxLines = 1; ellipsize = android.text.TextUtils.TruncateAt.END })
            val bottom = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL }
            bottom.addView(TextView(context).apply { id = 1003; textSize = 12f }, LinearLayout.LayoutParams(0, -2, 1f))
            bottom.addView(TextView(context).apply { id = 1004; textSize = 12f; gravity = Gravity.END }, LinearLayout.LayoutParams(-2, -2))
            body.addView(bottom)
            root.addView(body, FrameLayout.LayoutParams(-1, -2).apply { leftMargin = dp(4) })
            return root
        }
    }
}
