package com.nexify.nextool

import CatOverlayLayout
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nexify.layoutspeclibrary.NexApplication
import com.nexify.layoutspeclibrary.broadcast.OverlayBroadcastReceiver
import com.nexify.layoutspeclibrary.service.SuspendWindowService
import com.nexify.layoutspeclibrary.utils.Utils
import com.nexify.layoutspeclibrary.utils.ViewModleMain
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {


    private var catOverlayLayout: CatOverlayLayout? = null

    private val REQUEST_CODE_OVERLAY_PERMISSION = -1
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var lastShakeTime = 0L
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        checkOverlayPermission()
        startService(Intent(this, SuspendWindowService::class.java))
        // 创建并设置 OverlayFrameLayout
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
        }
        // 初始化广播接收器
        broadcastReceiver = OverlayBroadcastReceiver(
            addOverlay = { addOverlay() },
            removeOverlay = { removeOverlay() }
        )

        // 注册广播
        val intentFilter = IntentFilter().apply {
            addAction("com.example.ADD_OVERLAY")
            addAction("com.example.REMOVE_OVERLAY")
        }
        registerReceiver(broadcastReceiver, intentFilter, RECEIVER_EXPORTED)

        // 初始化传感器
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val button = this.findViewById<Button>(R.id.toggle_button)
        button.setOnClickListener {
            val intent = Intent(this,MainActivity2::class.java)
            startActivity(intent)

        }
    }
    fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 1234)
            }
        }
    }
    private fun addOverlay() {
        if (catOverlayLayout == null) {
            catOverlayLayout = CatOverlayLayout(this, null)
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                android.graphics.PixelFormat.TRANSLUCENT
            )
            (getSystemService(WINDOW_SERVICE) as WindowManager).addView(catOverlayLayout, params)
        }
    }

    private fun removeOverlay() {
        catOverlayLayout?.let {
            (getSystemService(WINDOW_SERVICE) as WindowManager).removeView(it)
            catOverlayLayout = null
        }
    }


    override fun onResume() {
        super.onResume()
        val botton = findViewById<Button>(R.id.button)
        botton.setOnClickListener {
            Utils.checkSuspendedWindowPermission(this) {
                ViewModleMain.isShowSuspendWindow.postValue(true)
            }
        }
        // 注册传感器监听
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        // 取消传感器监听
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            // 计算加速度的模
            val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val currentTime = System.currentTimeMillis()

            // 检测晃动
            if (acceleration > 12) { // 12 是一个经验值，表示晃动强度阈值
                if (currentTime - lastShakeTime > 1000) { // 防止重复触发
                    lastShakeTime = currentTime
//                    if (isOverlayActive) {
                        val intent = Intent("com.example.REMOVE_OVERLAY")
                        sendBroadcast(intent)
//                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 不需要实现
    }
}