package com.nexify.layoutspeclibrary.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class OverlayBroadcastReceiver(
    private val addOverlay: () -> Unit,
    private val removeOverlay: () -> Unit
) : BroadcastReceiver() {
    private var isOverlayActive = false
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            "com.example.ADD_OVERLAY" -> addOverlay()
            "com.example.REMOVE_OVERLAY" -> removeOverlay()
        }
    }
}
