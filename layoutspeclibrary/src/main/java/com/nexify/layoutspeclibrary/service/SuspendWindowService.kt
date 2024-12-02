package com.nexify.layoutspeclibrary.service

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleService
import com.nexify.layoutspeclibrary.R
import com.nexify.layoutspeclibrary.base.DevelopFloatingWindow
import com.nexify.layoutspeclibrary.databinding.ActivityFloatItemBinding
import com.nexify.layoutspeclibrary.utils.ViewModleMain

class SuspendWindowService : LifecycleService() {
    private var floatRootView: View? = null//悬浮窗View
    private var developFloatingWindow: DevelopFloatingWindow? = null
    private var viewClick: (() -> Unit?)? = null
    override fun onCreate() {
        super.onCreate()
        initObserve()
    }

    private fun initObserve() {
        developFloatingWindow = DevelopFloatingWindow(baseContext)
        ViewModleMain.apply {

            isVisible.observe(this@SuspendWindowService) {
                floatRootView?.visibility = if (it) View.VISIBLE else View.GONE
            }

            isShowSuspendWindow.observe(this@SuspendWindowService) {
                if (it) {
                    showWindow()
                    //TextConnect
                } else {
                    dismissWindow()
                }
            }
        }
    }

    /**
     * 创建悬浮窗
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun showWindow() {
        //获取WindowManager1`
        developFloatingWindow?.show()
        floatRootView = developFloatingWindow?.getRootView()
//        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//        val outMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(outMetrics)
//        val layoutParam = WindowManager.LayoutParams().apply {
//            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//            } else {
//                WindowManager.LayoutParams.TYPE_PHONE
//            }
//            format = PixelFormat.RGBA_8888
//            flags =
//                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//            //位置大小设置
//            width = WRAP_CONTENT
//            height = WRAP_CONTENT
//            gravity = Gravity.LEFT or Gravity.TOP
//            //设置剧中屏幕显示
//            x = outMetrics.widthPixels / 2 - width / 2
//            y = outMetrics.heightPixels / 2 - height / 2
//        }
//        // 新建悬浮窗控件
//        val inflater = LayoutInflater.from(baseContext)
//        val binding = ActivityFloatItemBinding.inflate(inflater).apply {
//            this.root.layoutParams = layoutParam
//        }
//
//        binding.text.setOnClickListener {
//            Toast.makeText(this,"mos",Toast.LENGTH_SHORT).show()
//        }
//
//        floatRootView = binding.root
//        floatRootView?.setOnTouchListener(ItemViewTouchListener(layoutParam, windowManager))
//        // 将悬浮窗控件添加到WindowManager
//        windowManager.addView(floatRootView, layoutParam)
    }

    fun setOnclick(binding: ActivityFloatItemBinding) {

    }
    private fun dismissWindow() {
        developFloatingWindow?.dismiss()
    }


}