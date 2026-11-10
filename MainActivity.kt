package com.example.gamemacro

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import android.graphics.Color
import android.graphics.Typeface

class MainActivity : Activity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(15, 15, 18)
        window.navigationBarColor = Color.BLACK

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 30, 24, 24)
            setBackgroundColor(Color.rgb(18, 18, 22))
        }
        val title = TextView(this).apply {
            text = "GAME MACRO"
            textSize = 28f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
        }
        val subtitle = TextView(this).apply {
            text = "Oyun içi yüzen makro paneli"
            textSize = 14f
            setTextColor(Color.LTGRAY)
            setPadding(0, 6, 0, 24)
        }
        status = TextView(this).apply {
            textSize = 13f
            setTextColor(Color.YELLOW)
            setPadding(0, 12, 0, 12)
        }
        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(status)

        addButton(layout, "1. Üstte gösterme izni", Color.rgb(180, 35, 45)) {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")))
            } else refreshStatus()
        }
        addButton(layout, "2. Dokunma hizmetini etkinleştir", Color.rgb(45, 45, 52)) {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        addButton(layout, "3. Oyun seç", Color.rgb(45, 45, 52)) { showGamePicker() }
        addButton(layout, "4. Makro panelini başlat", Color.rgb(180, 35, 45)) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Önce üstte gösterme iznini ver.", Toast.LENGTH_LONG).show()
            } else {
                startService(Intent(this, OverlayService::class.java))
                Toast.makeText(this, "Makro paneli başlatıldı.", Toast.LENGTH_SHORT).show()
            }
        }
        addButton(layout, "Paneli durdur", Color.DKGRAY) {
            stopService(Intent(this, OverlayService::class.java))
        }
        val note = TextView(this).apply {
            text = "\nNot: Makrolar yalnızca sen yüzen düğmeye basılı tutarken çalışır. Erişilebilirlik iznini Android ayarlarından kendin etkinleştirmen gerekir. Oyunların kuralları ve Android kısıtlamaları geçerlidir."
            setTextColor(Color.LTGRAY)
            textSize = 12f
        }
        layout.addView(note)
        setContentView(ScrollView(this).apply { addView(layout) })
        refreshStatus()
    }

    private fun addButton(parent: LinearLayout, label: String, color: Int, action: () -> Unit) {
        val button = Button(this).apply {
            text = label
            setTextColor(Color.WHITE)
            setBackgroundColor(color)
            isAllCaps = false
            setOnClickListener { action() }
        }
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 56)
        lp.bottomMargin = 10
        parent.addView(button, lp)
    }

    private fun showGamePicker() {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = packageManager.queryIntentActivities(intent, 0)
            .sortedBy { it.loadLabel(packageManager).toString().lowercase() }
        if (apps.isEmpty()) {
            Toast.makeText(this, "Başlatılabilir uygulama bulunamadı.", Toast.LENGTH_SHORT).show()
            return
        }
        val labels = apps.map { it.loadLabel(packageManager).toString() }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Oyun veya uygulama seç")
            .setItems(labels) { _, which ->
                val launch = packageManager.getLaunchIntentForPackage(apps[which].activityInfo.packageName)
                if (launch != null) startActivity(launch)
            }
            .setNegativeButton("Kapat", null)
            .show()
    }

    private fun refreshStatus() {
        status.text = "Üstte gösterme: ${if (Settings.canDrawOverlays(this)) "Açık" else "Kapalı"}\n" +
            "Dokunma hizmeti: Ayarlar > Erişilebilirlik bölümünden açılmalı."
    }
}
