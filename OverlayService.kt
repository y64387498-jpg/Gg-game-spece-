package com.example.gamemacro

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.*

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private val handler = Handler(Looper.getMainLooper())
    private val views = mutableListOf<View>()
    private val running = mutableSetOf<Int>()
    private val cps = IntArray(10) { 10 }
    private val xPos = FloatArray(10) { 250f }
    private val yPos = FloatArray(10) { 250f }

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        addControlPanel()
    }

    private fun params(x: Int, y: Int) = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    ).apply { gravity = Gravity.TOP or Gravity.START; this.x = x; this.y = y }

    private fun addControlPanel() {
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(10, 10, 10, 10)
            setBackgroundColor(Color.argb(225, 20, 20, 24))
        }
        val header = TextView(this).apply {
            text = "GAME MACRO  •  10 SLOT"
            setTextColor(Color.WHITE); textSize = 13f
            setPadding(8, 4, 8, 8)
        }
        panel.addView(header)
        val scroll = ScrollView(this)
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        for (i in 0 until 10) {
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
            val label = TextView(this).apply {
                text = "M${i + 1}  ${cps[i]} CPS"
                setTextColor(Color.WHITE); textSize = 12f
            }
            val seek = SeekBar(this).apply {
                max = 50; progress = cps[i]
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(s: SeekBar?, p: Int, user: Boolean) {
                        cps[i] = (p.coerceAtLeast(1)); label.text = "M${i + 1}  ${cps[i]} CPS"
                    }
                    override fun onStartTrackingTouch(s: SeekBar?) {}
                    override fun onStopTrackingTouch(s: SeekBar?) {}
                })
            }
            val macro = TextView(this).apply {
                text = " M${i + 1} "
                textSize = 13f; setTextColor(Color.WHITE)
                setPadding(12, 10, 12, 10)
                setBackgroundColor(Color.rgb(175, 35, 45))
                setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> { startMacro(i); true }
                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> { stopMacro(i); true }
                        else -> true
                    }
                }
            }
            row.addView(label, LinearLayout.LayoutParams(95, -2))
            row.addView(seek, LinearLayout.LayoutParams(130, -2))
            row.addView(macro)
            list.addView(row)
        }
        scroll.addView(list)
        panel.addView(scroll)
        val close = Button(this).apply {
            text = "Paneli kapat"; isAllCaps = false
            setOnClickListener { stopSelf() }
        }
        panel.addView(close)
        val p = params(30, 160)
        wm.addView(panel, p)
        views.add(panel)
        // Paneli ekranda sürüklemek için başlığa dokunup taşı.
        var startX = 0; var startY = 0; var touchX = 0f; var touchY = 0f
        header.setOnTouchListener { _, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = p.x; startY = p.y; touchX = e.rawX; touchY = e.rawY; true
                }
                MotionEvent.ACTION_MOVE -> {
                    p.x = startX + (e.rawX - touchX).toInt()
                    p.y = startY + (e.rawY - touchY).toInt()
                    wm.updateViewLayout(panel, p); true
                }
                else -> true
            }
        }
    }

    private fun startMacro(index: Int) {
        if (!running.add(index)) return
        tick(index)
    }

    private fun tick(index: Int) {
        if (index !in running) return
        val service = MacroAccessibilityService.instance
        if (service == null) {
            Toast.makeText(this, "Önce Erişilebilirlik hizmetini etkinleştir.", Toast.LENGTH_SHORT).show()
            running.remove(index); return
        }
        val dm = resources.displayMetrics
        val x = dm.widthPixels * 0.5f
        val y = dm.heightPixels * 0.5f
        service.tap(x, y)
        val delay = (1000L / cps[index].coerceIn(1, 50))
        handler.postDelayed({ tick(index) }, delay)
    }

    private fun stopMacro(index: Int) { running.remove(index) }

    override fun onDestroy() {
        running.clear()
        handler.removeCallbacksAndMessages(null)
        views.forEach { try { wm.removeView(it) } catch (_: Exception) {} }
        views.clear()
        super.onDestroy()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
