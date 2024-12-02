package com.nexify.layoutspeclibrary.base

import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

interface Floating : OnTouchListener {
    override fun onTouch(v: View?, event: MotionEvent?): Boolean
    fun addView(rootView: View)
    fun onClick(event: MotionEvent, v: View?)
    fun onMove(v: View?, x: Float, y: Float)
    fun show()
    fun dismiss()
}