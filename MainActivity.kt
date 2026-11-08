package com.civic.launcher

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class AppItem(val label: String, val pkg: String, val icon: android.graphics.drawable.Drawable)

class MainActivity : AppCompatActivity() {

    private lateinit var grid: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideSystemBars()

        grid = findViewById(R.id.appGrid)
        grid.layoutManager = GridLayoutManager(this, 4)

        findViewById<View>(R.id.btnMusic).setOnClickListener {
            safeStart(Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC))
        }
        findViewById<View>(R.id.btnMaps).setOnClickListener {
            safeStart(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=")))
        }
        findViewById<View>(R.id.btnPhone).setOnClickListener {
            safeStart(Intent(Intent.ACTION_DIAL))
        }
        findViewById<View>(R.id.btnSettings).setOnClickListener {
            safeStart(Intent(Settings.ACTION_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
        grid.adapter = AppAdapter(loadApps()) { item ->
            packageManager.getLaunchIntentForPackage(item.pkg)?.let { safeStart(it) }
        }
    }

    // Home button: do nothing (we already are home)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        grid.scrollToPosition(0)
    }

    @Deprecated("Disable back on launcher")
    override fun onBackPressed() { /* launcher: no-op */ }

    private fun loadApps(): List<AppItem> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentActivities(intent, 0)
            .filter { it.activityInfo.packageName != packageName }
            .map {
                AppItem(
                    it.loadLabel(packageManager).toString(),
                    it.activityInfo.packageName,
                    it.loadIcon(packageManager)
                )
            }
            .sortedBy { it.label.lowercase() }
    }

    private fun safeStart(i: Intent) {
        try { startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (_: Exception) {}
    }

    @Suppress("DEPRECATION")
    private fun hideSystemBars() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    }
}

class AppAdapter(
    private val items: List<AppItem>,
    private val onClick: (AppItem) -> Unit
) : RecyclerView.Adapter<AppAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.appIcon)
        val label: TextView = v.findViewById(R.id.appLabel)
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_app, p, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = items[pos]
        h.icon.setImageDrawable(item.icon)
        h.label.text = item.label
        h.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}
