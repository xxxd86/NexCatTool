package com.nexify.nextool

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nexify.layoutspeclibrary.service.SuspendWindowService
import com.nexify.layoutspeclibrary.utils.Utils
import com.nexify.layoutspeclibrary.utils.ViewModleMain

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        startService(Intent(this, SuspendWindowService::class.java))
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
    }
}