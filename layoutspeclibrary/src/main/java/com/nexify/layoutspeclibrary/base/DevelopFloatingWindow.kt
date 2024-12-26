package com.nexify.layoutspeclibrary.base

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.nexify.layoutspeclibrary.R
import com.nexify.layoutspeclibrary.databinding.ActivityFloatItemBinding
import com.nexify.layoutspeclibrary.databinding.FragmentFunctionMapBinding
import com.nexify.layoutspeclibrary.utils.dp2px


class DevelopFloatingWindow(context: Context) : FloatingWindow<ActivityFloatItemBinding>(context) {
    var isShow = true
    var expendView:View? = null
    override fun onClick(event: MotionEvent, v: View?) {
        val yRoundelMenu = v?.findViewById<ImageView>(R.id.text)
        val result = yRoundelMenu?.onTouchEvent(event) ?: false

        if (!result) {
            // do click event
            if (v != null) {
                binding?.text?.playAnimation()
                if (isShow) {
                    val inflater = LayoutInflater.from(v.context)
                    val mBinding = FragmentFunctionMapBinding.inflate(inflater)
                    mWindowLayoutParams?.apply {
                        width =v.context.dp2px(200)
                        height = v.context.dp2px(200)
                    }
                    mWindowManager?.updateViewLayout(binding?.root, mWindowLayoutParams)
                    // 定义LayoutParam
                    val params = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    mBinding.attrButton.setOnClickListener { it->
                        //打开布局检测
                        Toast.makeText(it.context,"开启布局检测",Toast.LENGTH_SHORT).show()
                        val intent = Intent("com.example.ADD_OVERLAY")
                        it.context.sendBroadcast(intent)
                    }
                    expendView = mBinding.root
                    params.leftMargin = v.context.dp2px(30)
                    binding?.floatContainer?.addView(expendView,params)
                    isShow = false
                } else {
                    isShow = true
                    binding?.floatContainer?.removeView(expendView)
                    mWindowLayoutParams?.apply {
                        width =v.context.dp2px(30)
                        height = v.context.dp2px(30)
                    }
                    mWindowManager?.updateViewLayout(binding?.root, mWindowLayoutParams)
                }
            }
        }
    }

    override fun viewClick(t: ActivityFloatItemBinding?, event: MotionEvent) {
        super.viewClick(t, event)
        val result = t?.root?.onTouchEvent(event)?:false
        if (!result) {
            if (binding != null) {
                Toast.makeText(binding?.root?.context,"mos",Toast.LENGTH_SHORT).show()

            }
        }
    }


    override fun onMove(v: View?, x: Float, y: Float) {
    }
}