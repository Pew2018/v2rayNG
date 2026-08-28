package com.v2ray.ang.ui.main

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.v2ray.ang.R
import com.v2ray.ang.dto.entities.ServersCache
import com.v2ray.ang.extension.isComplexType
import com.v2ray.ang.extension.nullIfBlank
import com.v2ray.ang.handler.AngConfigManager

private const val BLUE = 0xFF2196F3.toInt()
private const val DARK_SURFACE = 0xFF424242.toInt()
private const val LIGHT_SURFACE = Color.WHITE
private const val DARK_TEXT = Color.WHITE
private const val LIGHT_TEXT = 0xFF212121.toInt()
private const val DARK_SECONDARY = 0xFFD0D0D0.toInt()
private const val LIGHT_SECONDARY = 0xFF757575.toInt()

class LegacyServerAdapter(
    private val context: Context,
    private val onAction: (MainAction) -> Unit,
) : ListAdapter<ServersCache, LegacyServerAdapter.Holder>(DIFF) {
    var dark = false
    private var selectedGuid: String? = null
    fun setSelectedGuid(guid: String?) { selectedGuid = guid }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(context, onAction)
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position), selectedGuid, dark)

    class Holder(private val context: Context, private val onAction: (MainAction) -> Unit) : RecyclerView.ViewHolder(createRow(context)) {
        private val name = itemView.findViewById<TextView>(1001)
        private val detail = itemView.findViewById<TextView>(1002)
        private val type = itemView.findViewById<TextView>(1003)
        private val ping = itemView.findViewById<TextView>(1004)
        private val menu = itemView.findViewById<ImageButton>(1005)
        private val strip = itemView.findViewById<View>(1006)

        fun bind(item: ServersCache, selected: String?, dark: Boolean) {
            itemView.setBackgroundColor(if (dark) DARK_SURFACE else LIGHT_SURFACE)
            name.setTextColor(if (dark) DARK_TEXT else LIGHT_TEXT)
            detail.setTextColor(if (dark) DARK_SECONDARY else LIGHT_SECONDARY)
            type.setTextColor(if (dark) 0xFF90CAF9.toInt() else LIGHT_SECONDARY)
            ping.setTextColor(if (item.testDelayMillis < 0) 0xFFFF5252.toInt() else BLUE)
            menu.setColorFilter(if (dark) Color.WHITE else 0xFF424242.toInt())
            strip.setBackgroundColor(if (item.guid == selected) BLUE else Color.TRANSPARENT)
            name.text = item.profile.remarks
            detail.text = item.profile.description.nullIfBlank() ?: AngConfigManager.generateDescription(item.profile)
            type.text = item.profile.configType.name
            ping.text = if (item.testDelayMillis == 0L) "" else context.getString(R.string.server_test_delay_value, item.testDelayMillis)
            itemView.setOnClickListener { onAction(MainAction.SelectServer(item.guid)) }
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

        companion object {
            fun createRow(context: Context): View {
                val root = FrameLayout(context)
                root.addView(View(context).apply { id = 1006 }, FrameLayout.LayoutParams(dp(context, 4), -1))
                val body = LinearLayout(context).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(context, 12), dp(context, 8), dp(context, 4), dp(context, 8)) }
                val first = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
                first.addView(TextView(context).apply { id = 1001; textSize = 16f; maxLines = 1; ellipsize = android.text.TextUtils.TruncateAt.END }, LinearLayout.LayoutParams(0, -2, 1f))
                first.addView(AppCompatImageButton(context).apply { id = 1005; setImageResource(R.drawable.ic_more_vert_24dp); setBackgroundColor(Color.TRANSPARENT); setPadding(dp(context, 8), dp(context, 8), dp(context, 8), dp(context, 8)) }, LinearLayout.LayoutParams(dp(context, 44), dp(context, 44)))
                body.addView(first)
                body.addView(TextView(context).apply { id = 1002; textSize = 14f; maxLines = 1; ellipsize = android.text.TextUtils.TruncateAt.END })
                val bottom = LinearLayout(context).apply { orientation = LinearLayout.HORIZONTAL }
                bottom.addView(TextView(context).apply { id = 1003; textSize = 12f }, LinearLayout.LayoutParams(0, -2, 1f))
                bottom.addView(TextView(context).apply { id = 1004; textSize = 12f; gravity = Gravity.END }, LinearLayout.LayoutParams(-2, -2))
                body.addView(bottom)
                root.addView(body, FrameLayout.LayoutParams(-1, -2).apply { leftMargin = dp(context, 4) })
                return root
            }
            private fun dp(context: Context, value: Int) = (value * context.resources.displayMetrics.density).toInt()
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ServersCache>() {
            override fun areItemsTheSame(oldItem: ServersCache, newItem: ServersCache) = oldItem.guid == newItem.guid
            override fun areContentsTheSame(oldItem: ServersCache, newItem: ServersCache) = oldItem.guid == newItem.guid && oldItem.testDelayMillis == newItem.testDelayMillis && oldItem.profile.remarks == newItem.profile.remarks && oldItem.profile.description == newItem.profile.description && oldItem.profile.server == newItem.profile.server && oldItem.profile.configType == newItem.profile.configType
        }
    }
}
