package com.nexify.layoutspeclibrary.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.LifecycleService
import androidx.viewbinding.ViewBinding
import com.nexify.layoutspeclibrary.databinding.ActivityFloatItemBinding
import com.nexify.layoutspeclibrary.utils.dp2px
import kotlin.math.abs

abstract class FloatingWindow<T:ViewBinding>(private val context: Context) : Floating {
    var mLastTouchX = 0f
    var mLastTouchY = 0f
    private var mInitTouchX = 0f
    private var mInitTouchY = 0f
    private var mUpPointX = Float.MAX_VALUE
    private var mUpPointY = Float.MAX_VALUE
    var mWindowManager: WindowManager? = null
    var mWindowLayoutParams: WindowManager.LayoutParams? = null
    private var isShowing = false
    var binding:T? = null
    var viewClick: ((T?) -> Unit?)? = null
    init {
        onCreate()
    }

    fun getRootView(): View? {
        return binding?.root
    }

    override fun addView(rootView: View) {
        mWindowManager?.addView(rootView, generateLayoutParams(rootView))
    }

    abstract override fun onClick(event: MotionEvent, v: View?)

    abstract override fun onMove(v: View?, x: Float, y: Float)

    override fun show() {
        if (isShowing) return
        binding?.root?.let {
            addView(it)
            isShowing = true
        }
    }
    open fun viewClick(t:T?,event: MotionEvent) {

    }
    override fun dismiss() {
        if (isShowing) {
            mWindowManager?.removeView(binding?.root)
            isShowing = false
        }
    }
//    open fun expendView() {
//
//    }
open fun generateLayoutParamsExpend(view: View,position: ExpandPosition = ExpandPosition.RIGHT): WindowManager.LayoutParams? {
    mWindowLayoutParams = WindowManager.LayoutParams().apply {
        width = context.dp2px(100)
        height = context.dp2px(100)
        type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        format = PixelFormat.RGBA_8888
        gravity = Gravity.LEFT or Gravity.TOP
        when(position) {
            ExpandPosition.LEFT -> {

            }
            ExpandPosition.RIGHT -> {
                x = (mLastTouchX + width  ).toInt()
                y = (mLastTouchY + width  ).toInt()
            }
            ExpandPosition.TOP -> {

            }
            ExpandPosition.BOTTOM -> {

            }
        }

//        x = context.resources.displayMetrics.widthPixels / 2 + width / 2
//        y = context.resources.displayMetrics.heightPixels / 2 + height / 2
    }
    return mWindowLayoutParams
}
    open fun generateLayoutParams(view: View): WindowManager.LayoutParams? {
        mWindowLayoutParams = WindowManager.LayoutParams().apply {
            width = context.dp2px(30)
            height = context.dp2px(30)
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            format = PixelFormat.RGBA_8888
            gravity = Gravity.LEFT or Gravity.TOP
            x = context.resources.displayMetrics.widthPixels / 2 - width / 2
            y = context.resources.displayMetrics.heightPixels / 2 - height / 2
        }
        return mWindowLayoutParams
    }

    open fun updateLayoutParams(movedX: Float, movedY: Float) {
        mWindowLayoutParams?.apply {
            x += movedX.toInt()
            y += movedY.toInt()
        }
        mWindowManager?.updateViewLayout(binding?.root, mWindowLayoutParams)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val action = event?.action
        action?.let {
            val curX = event.rawX
            val curY = event.rawY
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    mLastTouchX = curX
                    mLastTouchY = curY
                    mInitTouchX = curX
                    mInitTouchY = curY
                }

                MotionEvent.ACTION_MOVE -> {
                    val movedX = curX - mLastTouchX
                    val movedY = curY - mLastTouchY
                    updateLayoutParams(movedX, movedY)
                    mLastTouchX = curX
                    mLastTouchY = curY
                    onMove(binding?.root, mLastTouchX, mLastTouchY)
                }

                MotionEvent.ACTION_UP -> {
                    mUpPointX = curX
                    mUpPointY = curY
                    if (abs(mUpPointX - mInitTouchX) < 10f && abs(mUpPointY - mInitTouchY) < 10f) {
                        onClick(event, binding?.root)
                    }
                    mUpPointX = Float.MAX_VALUE
                    mUpPointY = Float.MAX_VALUE
                }
            }
        }
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onCreate() {
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val inflater = LayoutInflater.from(context)
        val mBinding = ActivityFloatItemBinding.inflate(inflater)
        binding = mBinding as T
        binding?.root?.setOnTouchListener(this)
    }
}